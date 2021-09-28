package javachat.client;

import javachat.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class Controller {
    private File fiFavorites = new File("data/favorites.dat");
    private File fiUsers = new File("data/users.dat");
    private Set<User> usersOnline = new HashSet<>();
    private Set<User> usersFavorite = new HashSet<>();
    private Set<User> usersLocal = new HashSet<>();
    private User userLocal;
    private ClientUI ui;
    private ClientConnection cc;

    @SuppressWarnings("unchecked")
	private Controller() {
        // Create data folder
        File foData = new File("data/");
        if (!foData.exists()) {
            foData.mkdir();
        }

        // Read list of favorites
        if (fiFavorites.exists()) {
            usersFavorite = (HashSet<User>) ChatHelper.readFile(fiFavorites);
            if (usersFavorite == null)
                usersFavorite = new HashSet<>();
//            ObjectInputStream ny = new ObjectInputStream();
        }

        // Read list of local users
        if (fiUsers.exists()) {
            System.out.println("Reading filed users.dat...");
            usersLocal = (HashSet<User>) ChatHelper.readFile(fiUsers);
            if (usersLocal == null)
                usersLocal = new HashSet<>();
        }
    }

    public void setUI(ClientUI ui) {
        this.ui = ui;
    }

    public void addLocalUser(String name, String imagePath) {
        SerializableImage ii = null;
        if (imagePath == null) { // If no image is set, set image to gubbe.jpg
            try {
                BufferedImage bIm = ImageIO.read(new URL("https://i.imgur.com/B87P6eN.jpg"));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bIm, "png", baos);
                baos.flush();
                ii = new SerializableImage(baos.toByteArray());
            } catch (IOException e) {
                System.out.println("Image error: [" + e.toString() + "]");
            }
        } else {
            ii = new SerializableImage(imagePath);
        }
        User newUser = new User(name, ii);
        System.out.println("Adding user " + newUser);
        if (usersLocal.contains(newUser)) {
            System.out.println("User exists");
            for (User u : usersLocal)
                if (u.equals(newUser))
                    u.setAvatar(newUser.getAvatar()); // If user exists, update avatar
        } else {
            System.out.println("User doesn't exist");
            usersLocal.add(newUser);
        }
        ChatHelper.writeToFile(usersLocal, fiUsers);
    }

    public Set<User> getLocalUsers() {
        return usersLocal;
    }

    // Select which user to log in with
    public void setLocalUser(String name) {
        for (User u : usersLocal)
            if (name.equals(u.toString()))
                userLocal = u;
    }

    public User getLocalUser() {
        return userLocal;
    }

    // Get list of online users, and offline favorites
    public Set<User> getUserlist() {
        Set<User> users = new HashSet<>();
        for (User u : usersOnline)
            if (usersFavorite.contains(u))
                u.setFavorite(true);
        System.out.println("Online size: " + usersOnline.size() + ", " + usersOnline);
        users.addAll(usersOnline);
        System.out.println("Favorite size: " + usersFavorite.size() + ", " + usersFavorite);
        users.addAll(usersFavorite);

        return users;
    }

    public void connect(String ip, int port) {
        cc = new ClientConnection(this, ip, port);
        cc.connect(new ConnectMessage(userLocal));
    }

    public void disconnect() {
        if (cc != null)
            cc.sendPacket(new DisconnectMessage(userLocal));
    }

    public void sendPacket(Message message) {
        cc.sendPacket(message);
    }

    public void receivePacket(Message message) {
        if (message instanceof UserMessage)
            ui.receiveMessage((UserMessage) message);
        else if (message instanceof ConnectMessage)
            receiveConnect(((ConnectMessage) message));
        else if (message instanceof DisconnectMessage)
            receiveDisconnect((DisconnectMessage) message);
    }

    // Receiving a Message of a new connected user
    private void receiveConnect(ConnectMessage connect) {
        if (connect.getSender().equals(userLocal)) { // If it's my own connections message, add all users
            usersOnline.addAll(connect.getUsers());
        } else {
            usersOnline.add(connect.getSender()); // Otherwise, just add the latest user
            System.out.println("Server/Client list match: " + usersOnline.equals(connect.getUsers()));
        }
        System.out.println("Client: " + connect.getSender() + " connected");
        ui.receiveConnect(connect);
    }

    // Receiving a Message of a disconnected user
    private void receiveDisconnect(DisconnectMessage disconnect) {
        if (disconnect.getSender().equals(userLocal)) { // If it's my own disconnect message echoing, shut down connection
            usersOnline.clear();
            cc = null;
            ChatHelper.writeToFile(usersFavorite, fiFavorites);
            System.out.println("Client disconnected.");
        } else {
            usersOnline.remove(disconnect.getSender());
            System.out.println("Client: " + disconnect.getSender() + " disconnected");
        }
        ui.receiveDisconnect(disconnect);
    }

    public void toggleFavorite(User user, boolean state) {
        if (true) { //!user.equals(userLocal)) { For testing purposes, one can favorite themselves atm
            user.setFavorite(state);
            System.out.println("User is favorite: " + user.isFavorite());
            if (!user.isFavorite()) {
                usersFavorite.remove(user);
            } else {
                if (true) { // !user.equals(userLocal)) {
                    User fav = new User(user.toString(), user.getAvatar());
                    fav.setFavorite(true);
                    fav.setOnline(false);
                    usersFavorite.add(fav);
                }
            }
        }
        ChatHelper.writeToFile(usersFavorite, fiFavorites);
    }

    public static void main(String[] args) {
        //System.setProperty("sun.java2d.uiScale", "1.0");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        Controller cc = new Controller();
        ClientGUI gui = new ClientGUI(cc);
        cc.setUI(gui);
        gui.showUI();
    }
}