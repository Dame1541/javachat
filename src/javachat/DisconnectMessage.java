package javachat;

public class DisconnectMessage extends Message {

	public DisconnectMessage(User sender) {
		super(sender.getThinUser());
	}
}
