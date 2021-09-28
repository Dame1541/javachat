package javachat;

import java.util.Set;

public class UserMessage extends Message {
	private String content;
	private SerializableImage image;
	
	public UserMessage(User sender, Set<User> receivers, String content, SerializableImage image) {
		super(sender);
		super.users = receivers;
		this.content = content;
		this.image = image;
	}

	public String getContent() {
		return content;
	}
	
	public SerializableImage getImage() {
		return image;
	}
}