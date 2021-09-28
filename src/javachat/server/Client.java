package javachat.server;

import javachat.UserMessage;
import javachat.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class Client implements Serializable {
	private User user;
	private Queue<UserMessage> toBeDelivered = new LinkedList<UserMessage>();
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private static final long serialversionUID = 2L;
	
	public Client(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setOnline(boolean online) {
		user.setOnline(online);
	}
	
	public boolean isOnline() {
		return user.isOnline();
	}

	public void setInputStream(ObjectInputStream ois) { this.ois = ois; }

	public ObjectInputStream getInputStream() {
		return ois;
	}

	public void setOutputStream(ObjectOutputStream oos) {
		this.oos = oos;
	}

	public ObjectOutputStream getOutputStream() {
		return oos;
	}

	public synchronized void addMessage(UserMessage userMessage) {
		toBeDelivered.add(userMessage);
	}
	
	public int getMessageBufferSize() {
		return toBeDelivered.size();
	}

	public synchronized UserMessage getNextMessage() { return toBeDelivered.remove();}
	
	public String toString() {
		return user.toString();
	}
}
