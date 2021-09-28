package javachat;

import javachat.ChatBot;
import javachat.SerializableImage;
import javachat.User;
import javachat.server.ServerControl;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ChatHelper {

    public static void writeToFile(Object o, File file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            oos.writeObject(o);
        } catch (Exception e) {
            System.out.println("Error creating file: [" + e.toString() + "]");
        }
    }

    public static Object readFile(File file) {
        Object o = null;
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            o = ois.readObject();
        } catch (Exception e) {
            System.out.println("Error reading file: [" + e.toString() + "]");
        }
        return o;
    }

    public static ImageIcon getScaledImage(ImageIcon ii, int w, int h) {
        Image image = ii.getImage();
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, w, h, null);
        g2.dispose();

        return new ImageIcon(resizedImg);
    }

    public Set<User> getFavorites() {
        Set<User> usersFavorite = new HashSet<>();

        User rolf = new User("Rolf", new SerializableImage("images/gubbe.jpg"));
        rolf.setFavorite(true);
        User alice = new User("Alice", new SerializableImage("images/alice.png"));
        alice.setFavorite(true);
        usersFavorite.add(rolf);
        usersFavorite.add(alice);

        return usersFavorite;
    }

    public static java.util.List<ChatBot> getBots(ServerControl sc) {
        List<ChatBot> bots = new LinkedList<>();
        User uAlice = new User("Alice", new SerializableImage("images/alice.png"));
        String[] saAlice = {"Into the hole again, we hurried along our way, into a once-glorious garden now steeped in dark decay."
                , "I'm not afraid of her, or her creatures. Never was, really!"
                , "Mange-ridden to the core, he leads me through the fray. With the toss of a Jackbomb, I clear abominations from our way."
                , "They taunt me about the burning as if I were to blame, I clear them from my conscious with the eloquence of my blade."
                , "If it's my keen invention you'd like to destroy, I'll withstand your best shot; I've got the right toy."
                , "Everyone I love dies violently; unnaturally. I'm cursed! Why go on? I'll just hurt others."
                , "Save myself from death, is that it? Is that why I've come here? I'm not afraid to die! At times I've welcomed death."
                , "\"Mushrooms, poppies, sugar and spice, all those things are very nice. When combined, the proper mixture makes a getting small elixir.\" Hm. I don't really like sweets."
                , "Promise only what you are prepared to deliver. I am destined to do battle with the Red Queen. The outcome is uncertain."
                , "How many times must I tell you? I only take tea with friends!"
                , "If ignorance is bliss, I must be ecstatic."
                , "Where does that smushy lay-about hang his hookah these days?"
                , "I enjoy the taste of mushrooms, but not the ones that bite back."
                , "I wish I were hallucinating. What a terrible choice: eat a toadstool or become food for insects!"
                , "Such order in the midst of chaos makes me woozy and disoriented."
                , "I'm not edible."
                , "I fear nothing."};
        ChatBot alice = new ChatBot(uAlice, saAlice, sc, 2000);
        bots.add(alice);
        User uCheshire = new User("Cheshire", new SerializableImage("images/cheshire.jpg"));
        String[] saCheshire = {"Here's a riddle: When is a croquet mallet like a billy club? I'll tell you: whenever you want it to be.",
                "Every adventure requires a first step. Trite, but true, even here.",
                "The uninformed must improve their deficit or die.",
                "To the royal guards of this realm, we are all victims-in-waiting.",
                "Only the foolish believe that suffering is just wages for being different.",
                "Only the insane equate pain with success.",
                "Only the savage regard the endurance of pain as a measure of worth.",
                "Only a few find the way; some don't recognize it when they do; some don't ever want to.",
                "What is sought is most often found, if it is truly sought.",
                "Tell yourself, \"I've seen worse at Rutledge's.\" Prevarication in this instance may help.",
                "There's an ugly name for those who do things the hard way.",
                "When the remarkable turns bizarre, reason turns rancid.",
                "I can't know everything. Pretend you're an orphan – oh! That was rude, you are.",
                "I suppose \"experience teaches best\", \"learn by doing\", and similar clichés have merit. Take their advice; I'm busy.",
                "Observe, learn, and react.",
                "Work if you must. It's my nature to unwind from time to time.",
                "I've heard self-reliance is a virtue. Now you've heard it.",
                "Whatever says too much of a good thing must be bad tells a lie.",
                "Whoever says too much of a good thing is not enough speaks the truth.",
                "He's not the brightest star in the sky, but he shines at one thing.",
                "The proper order of things is often a mystery to me. You too?"};
        ChatBot cheshire = new ChatBot(uCheshire, saCheshire, sc, 1000);
        bots.add(cheshire);
        return bots;
    }
}
