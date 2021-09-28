package javachat;

import java.net.Socket;

public class ConnectMessage extends Message {
	private transient Socket socket;

	public ConnectMessage(User sender) {
		super(sender);
		super.getSender().setOnline(true);
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
}
