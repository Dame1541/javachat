package javachat;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;

/**
 * SerializableImage wraps ImageIcon so that it can be serialized between
 * different versions of Java.
 */
public class SerializableImage implements Serializable, Icon {
    private byte[] imageData;
    private transient ImageIcon imageIcon;

    /**
     * Constructs a SerializableImage from the contents in the specified file.
     *
     * @param path Path to image to load.
     */
    public SerializableImage(String path) {
        try {
            imageData = Files.readAllBytes(new File(path).toPath());
            getImageIcon();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs a SerializableImage from a byte array of image data.
     *
     * @param imageData The image to load.
     */
    public SerializableImage(byte[] imageData) {
        this.imageData = imageData;
        getImageIcon();
    }

    /**
     * Returns a Swing ImageIcon from the encapsulated image data.
     *
     * @return An instance of ImageIcon.
     */
    public ImageIcon getImageIcon() {
        if (imageData != null && imageIcon == null) {
            imageIcon = new ImageIcon(imageData);
        }
        return imageIcon;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        imageIcon.paintIcon(c, g, x, y);
    }

    @Override
    public int getIconWidth() {
        if (imageIcon == null)
            getImageIcon();
        return imageIcon.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        if (imageIcon == null)
            getImageIcon();
        return imageIcon.getIconHeight();
    }
}
