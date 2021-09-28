package javachat.client;

import javachat.ConnectMessage;
import javachat.DisconnectMessage;
import javachat.UserMessage;

// Interface if one wants to make their own UI for the chat client
public interface ClientUI {
    void receiveMessage(UserMessage userMessage);

    void receiveConnect(ConnectMessage connect);

    void receiveDisconnect(DisconnectMessage disconnect);

    void disconnect();
}
