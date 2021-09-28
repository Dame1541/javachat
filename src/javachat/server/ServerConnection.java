package javachat.server;

import javachat.ConnectMessage;
import javachat.DisconnectMessage;
import javachat.Message;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

// Class for handling connections to users
public class ServerConnection {
    private ServerSocket serverSocket;
    private ServerControl sc;

    public ServerConnection(ServerControl sc, int port) {
        this.sc = sc;
        try {
            serverSocket = new ServerSocket(port);
            sc.sendToMonitor("ServerConnection: Listening on port :" + port);
        } catch (IOException e) {
            sc.sendToMonitor(e.toString());
        }
        Thread sl = new ServerListener();
        sl.start();
    }

    public void sendPacket(Message message, Client client) {
        new ServerSender(message, client).start();
    }

    void startListener(Client client) {
        new SocketListener(client).start();
    }

    // Listens for new connections and sets up ObjectInputStream
    private class ServerListener extends Thread {
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    Socket socket = serverSocket.accept();
                    sc.sendToMonitor(
                            String.format("ServerListener: Heard socket @ IP: %s, port: %d",
                                    socket.getInetAddress().getHostAddress(),
                                    socket.getPort()));
                    ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    Object o = ois.readObject();
                    if (o instanceof ConnectMessage) {
                        ConnectMessage connect = (ConnectMessage) o;
                        connect.setSocket(socket);
                        sc.connectClient(connect, ois);
                    }
                } catch (Exception e) {
                    sc.sendToMonitor("ServerListener: [" + e + "]");
                }
            }
        }
    }

    // Send Messages to users
    private class ServerSender extends Thread {
        private Message message;
        private Client client;

        public ServerSender(Message message, Client client) {
            this.message = message;
            this.client = client;
        }

        public void run() {
            sc.sendToMonitor("ServerSender: sending " + message.getClass().getSimpleName() + " to " + client.getUser());
            try {
                message.setDelivered(new Date());

                synchronized (client.getOutputStream()) {
                    client.getOutputStream().writeObject(message);
                    client.getOutputStream().flush();

                    while (client.getMessageBufferSize() > 0) {
                        sc.sendToMonitor(client.getUser() + " has " + client.getMessageBufferSize() + " messages on server. Sending...");
                        client.getOutputStream().writeObject(client.getNextMessage());
                        client.getOutputStream().flush();
                    }
                }
            } catch (Exception e) {
                sc.sendToMonitor("ServerSender: [" + e.toString() + "]");
            }
        }
    }

    private class SocketListener extends Thread {
        private Client client;

        public SocketListener(Client client) {
            this.client = client;
        }

        public void run() {
            sc.sendToMonitor("SocketListener: Going to work...");
            while (client.isOnline()) {
                try {
                    Message message = (Message) client.getInputStream().readObject();
                    sc.sendToMonitor(String.format("SocketListener: Received %s from %s to %s",
                            message.getClass().getSimpleName(),
                            message.getSender(),
                            (message.getUsers() == null ? "all" : message.getUsers())));
                    message.setArrived(new Date());
                    sc.receivePacket(message);
                    if (message instanceof DisconnectMessage)
                        client.setOnline(false);
                } catch (ClassNotFoundException | IOException e) {
                    sc.sendToMonitor("SocketListener: [" + e.toString() + "], resetting connection...");
                    client.setOnline(false);
                }
            }
            sc.sendToMonitor("SocketListener: Ending. Daisy, Daisy...");
        }
    }
}