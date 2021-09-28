package javachat.client;

import javachat.ConnectMessage;
import javachat.DisconnectMessage;
import javachat.Message;

import java.io.*;
import java.net.Socket;
import java.util.Date;

// Class that handles all connections to the server
public class ClientConnection {
    private String ip;
    private int port;
    private Socket socket;
    private Controller cc;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public ClientConnection(Controller cc, String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.cc = cc;
    }

    public void sendPacket(Message message) {
        new ClientSender(message).start();
    }

    public void connect(ConnectMessage connect) {
        new StartConnection(connect).start();
    }

    // Initiates a connection with the server
    private class StartConnection extends Thread {
        private ConnectMessage connect;

        public StartConnection(ConnectMessage connect) {
            this.connect = connect;
        }

        public void run() {
            try {
                System.out.println("Connecting socket...");
                socket = new Socket(ip, port);
                new ClientListener().start();
                System.out.println("Trying to connect output stream...");
                oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                System.out.println("Sending connect...");
                sendPacket(connect);
            } catch (IOException e) {
                System.out.println("Error connecting socket: " + e.toString());
            }
        }
    }

    // Listens for Messages from the server
    private class ClientListener extends Thread {
        private boolean streamOK = true;
        public void run() {
            try {
                System.out.println("Trying to connect input stream...");
                ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                System.out.println("Input stream connected.");
            } catch (IOException e) {
                System.out.println("ClientListener: Error creating OIS: [" + e + "]");
                streamOK = false;
            }
            while (streamOK) {
                try {
                    Message message = (Message) ois.readObject();
                    message.setDelivered(new Date());
                    System.out.println("Received message " + message.getClass().getSimpleName());
                    cc.receivePacket(message);
                } catch (Exception e) {
                    System.out.println("Connection error: [" + e.toString() + "]");
                    streamOK = false;
                    DisconnectMessage dc = new DisconnectMessage(cc.getLocalUser());
                    dc.setDelivered(new Date());
                    cc.receivePacket(dc);
                }
            }
        }
    }

    // Sends Messages to the server
    private class ClientSender extends Thread {
        private Message message;

        public ClientSender(Message message) {
            this.message = message;
        }

        public void run() {
            try {
                oos.writeObject(message);
                System.out.println("Message sent.");
                oos.flush();
            } catch (Exception e) {
                System.out.println("ClientSender: " + e.toString());
            }
        }
    }
}