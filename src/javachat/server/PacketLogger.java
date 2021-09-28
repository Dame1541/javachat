package javachat.server;

import javachat.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * PacketLogger saves all received messages on disk.
 */
public class PacketLogger {
    private File file;
    private ArrayList<Message> messages = new ArrayList<>();

    /**
     * Creates a new logger given a path. Reads the log if it exists.
     *
     * @param path Path to the packet log.
     */
    public PacketLogger(String path) {
        file = new File(path);

        load();
    }

    /**
     * Writes a {@link Message} to the log.
     *
     * @param message The message to log.
     */
    public synchronized void put(Message message) {
        messages.add(message);

        // Kanske inte så effektivt att skriva hela loggen varje gång ett paket läggs till, men det är en lösning...
        write();
    }

    /**
     * Retrieves a list of messages matching a date range.
     *
     * @param from Beginning of range.
     * @param to   End of range.
     * @return List of messages.
     */
    public List<Message> filter(Calendar from, Calendar to) {
        ArrayList<Message> list = new ArrayList<>();
        for (Message p : messages) {
            Calendar arrived = Calendar.getInstance();
            arrived.setTime(p.getArrived());
            if ((arrived.equals(from) || arrived.after(from)) && (arrived.equals(to) || arrived.before(to))) {
                list.add(p);
            }
        }
        return list;
    }

    /**
     * Loads the packet log from disk.
     */
    public synchronized void load() {
        if (!file.exists()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            ArrayList<Message> obj = (ArrayList<Message>) ois.readObject();
            messages = obj;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Unable to load packet log:");
            e.printStackTrace();
        }
    }

    /**
     * Serializes the packet log.
     */
    public synchronized void write() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(messages);
        } catch (IOException e) {
            System.err.println("Unable to save packet log:");
            e.printStackTrace();
        }
    }

    /**
     * @return Number of messages logged.
     */
    public int size() {
        return messages.size();
    }
}
