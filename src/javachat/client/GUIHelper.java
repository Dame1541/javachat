package javachat.client;

import javachat.ChatHelper;
import javachat.ConnectMessage;
import javachat.DisconnectMessage;
import javachat.UserMessage;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.text.SimpleDateFormat;

public class GUIHelper {
    private static StyleContext sc = new StyleContext();
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");

    private static void loadStyles() {
        Style cRed = sc.addStyle("cRed", null);
        Style cBlue = sc.addStyle("cBlue", null);
        Style cGreen = sc.addStyle("cGreen", null);
        Style cBlack = sc.addStyle("cBlack", null);
        Style fMono = sc.addStyle("fMono", null);
        cRed.addAttribute(StyleConstants.Foreground, Color.RED);
        cBlue.addAttribute(StyleConstants.Foreground, Color.BLUE);
        cGreen.addAttribute(StyleConstants.Foreground, new Color(0, 180, 0));
        cBlack.addAttribute(StyleConstants.Foreground, Color.BLACK);
        fMono.addAttribute(StyleConstants.FontFamily, "Monospaced");
    }

    private static Style getStyle(String style) {
        return sc.getStyle(style);
    }

    // Add a text to a Chatpane with proper formatting
    public static void putMessage(UserMessage userMessage, ChatPane pane) {
        loadStyles();

        if (userMessage.getImage() != null) {
            double imgWidth = userMessage.getImage().getIconWidth(),
                    imgHeight = userMessage.getImage().getIconHeight(),
                    paneWidth = pane.getSize().getWidth(),
                    paneHeight = pane.getSize().getHeight(), ratio;

            if (imgWidth > paneWidth || imgHeight > paneHeight) { // If image is wider/higher than Chatpane, resize
                if (imgWidth / imgHeight > paneWidth / paneHeight)
                    ratio = paneWidth / imgWidth;
                else
                    ratio = paneHeight / imgHeight;
                imgWidth *= ratio;
                imgHeight *= ratio;
                pane.insertImage(ChatHelper.getScaledImage(userMessage.getImage().getImageIcon(), (int) imgWidth, (int) imgHeight));
            } else
                pane.insertImage(userMessage.getImage());

            pane.insertText("\n", getStyle("cBlack"));
        }

        pane.insertText("[" + dateFormatter.format(userMessage.getDelivered()) + "] ", getStyle("black"));
        pane.insertText("<", getStyle("cBlue"));
        pane.insertText(userMessage.getSender().toString(), getStyle("cRed"));
        pane.insertText("> ", getStyle("cBlue"));
        pane.insertText(userMessage.getContent() + "\n", getStyle("cBlack"));
        pane.jumpToBottom();
    }

    // Add a connect message to Chatpane with proper formatting
    public static void putConnect(ConnectMessage connect, ChatPane pane) {
        loadStyles();
        pane.insertText("[" + dateFormatter.format(connect.getDelivered()) + "] " + connect.getSender() + " has connected\n", getStyle("cGreen"));
        pane.jumpToBottom();
    }

    // Add a disconnect messsage to Chatpane with proper formatting
    public static void putDisconnect(DisconnectMessage disconnect, ChatPane pane) {
        loadStyles();
        pane.insertText("[" + dateFormatter.format(disconnect.getDelivered()) + "] " + disconnect.getSender() + " has disconnected\n", getStyle("cRed"));
        pane.jumpToBottom();
    }
}