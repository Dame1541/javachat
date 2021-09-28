	package javachat.server;

import javachat.*;

import java.io.*;
import java.util.*;

import javax.swing.*;

public class ServerControl {
    private ServerConnection sc;
    private ServerGUI ui = new ServerGUI(this);
    private Clients clients = new Clients();
    private PacketLogger packetLogger = new PacketLogger("data/log.dat");
    private File fiClients = new File("data/clients.dat");

    public ServerControl() {
        sc = new ServerConnection(this, 5561);

        // Reads file of previously connected users
        if (fiClients.exists()) {
            Set<User> users = (Set<User>) ChatHelper.readFile(fiClients);
            if (users == null)
                clients = new Clients();
            else
                for (User u: users) {
                    u.setOnline(false);
                    clients.put(u, new Client(u));
                }
        }

        ui.setUserList();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Server GUI");
            frame.add(ui);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    // Send to server log window
    public void sendToMonitor(String input) {
        ui.textOut(input);
    }

    public void receivePacket(Message message) {
        packetLogger.put(message);
        if (message instanceof UserMessage)
            sendPacket(message);
        else if (message instanceof DisconnectMessage)
            disconnectClient((DisconnectMessage) message);
    }

    // Connects a client to the server
    public synchronized void connectClient(ConnectMessage connect, ObjectInputStream ois) {
        User u = connect.getSender();
        if (!clients.containsKey(u)) { // Check if client already exists
            clients.put(u, new Client(u));
            ChatHelper.writeToFile(getUserlist(), fiClients);
        }
        Client c = clients.get(u);
        c.setOnline(true);
        c.setInputStream(ois);
        try {
            c.setOutputStream(new ObjectOutputStream(new BufferedOutputStream(connect.getSocket().getOutputStream())));
            sendToMonitor("Client's OOS connected: " + c.getOutputStream());
        } catch (IOException e) {
            sendToMonitor("ServerControl: Error creating client's OOS: [" + e + "]");
        }
        sc.startListener(c); // Start listening to Messages from client
        connect.setSocket(null);
        sendToMonitor("User \"" + u + "\" connected.");
        ui.setUserList();

        connect.setUsers(getOnlineUsers(true));
        sendPacket(connect);
    }

    // Disconnect a client
    public synchronized void disconnectClient(DisconnectMessage disconnect) {
        sendToMonitor("User \"" + disconnect.getSender() + "\" disconnected");
        disconnect.setUsers(getOnlineUsers(true));
        sendPacket(disconnect);
    }

    private void sendPacket(Message message) {
        if (message.getUsers() == null || message instanceof DisconnectMessage) {
            for (Client c : getOnlineClients()) // Send Message to all online clients
                sc.sendPacket(message, c);
            if (message instanceof DisconnectMessage) { // If the message is a Disconnect, set client offline
                clients.get(message.getSender()).setOnline(false);
                ui.setUserList();
            }
        } else if (message instanceof UserMessage) {
            for (User u : message.getUsers()) {
                Client c = clients.get(u);
                if (c.isOnline()) {
                    sendToMonitor("ServerControl: Client online, sending message to ServerConnections");
                    sc.sendPacket(message, c);
                } else {
                    sendToMonitor("ServerControl: Client not online, adding message to buffer...");
                    c.addMessage((UserMessage) message);
                }
            }
        } else if (message instanceof ConnectMessage) { // If Message is user wanting to connect
            ConnectMessage fullConnect = new ConnectMessage(message.getSender()); // Get a full list of online users
            fullConnect.setUsers(getOnlineUsers(false));
            for (Client c : getOnlineClients())
                if (!c.equals(clients.get(message.getSender())))
                    sc.sendPacket(message, c);
                else // and send full list to recently connected user
                    sc.sendPacket(fullConnect, clients.get(message.getSender()));
        }
    }

    private Set<Client> getOnlineClients() {
        Set<Client> clientsOnline = new HashSet<>();
        for (Map.Entry<User, Client> entry : clients.entrySet())
            if (entry.getKey().isOnline())
                clientsOnline.add(entry.getValue());
        return clientsOnline;
    }

    // Get all users
    Set<User> getUserlist() {
        Set<User> users = new HashSet<>();
        for (Map.Entry<User, Client> entry : clients.entrySet())
            users.add(entry.getKey());
        return users;
    }

    // Get online users
    private Set<User> getOnlineUsers(boolean thin) {
        Set<User> users = new HashSet<>();
        for (Map.Entry<User, Client> entry : clients.entrySet())
            if (entry.getKey().isOnline())
                if(thin)
                    users.add(entry.getKey().getThinUser());
                else
                    users.add(entry.getKey());
        return users;
    }

    public static void main(String[] args) {
        ServerControl sControl = new ServerControl();
    }
}