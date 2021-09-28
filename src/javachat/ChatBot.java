package javachat;

import javachat.server.ServerControl;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ChatBot  extends Thread {
	private User user;
	private String[] phrases;
	private ServerControl sc;
	public static final Random rand = new Random();
	private int pause;


	public ChatBot(User user, String[] phrases, ServerControl sc, int pause) {
		this.user = user;
		this.phrases = phrases;
		this.sc = sc;
		this.pause = pause;
	}

	public User getUser() {
		return user;
	}

	public UserMessage getMessage() {
		String content = phrases[rand.nextInt(phrases.length)];
		if (rand.nextBoolean())
			return new UserMessage(user, null, content, null);
		else {
			Set<User> receivers = new HashSet<User>();
			receivers.add(new User("Euler", null));
			return new UserMessage(user, receivers, content, null);
		}
	}

	public void run() {
		try {
			while(!Thread.interrupted()) {
				Thread.sleep(rand.nextInt(1000)+3000);
				sc.receivePacket(new ConnectMessage(user));
				int r = rand.nextInt(2)+2;
				for (int i = 0; i < r; i++) {
					sc.receivePacket(getMessage());
					Thread.sleep(rand.nextInt(2500)+1500);
				}
				sc.receivePacket(new DisconnectMessage(user));
			}
		} catch (InterruptedException e) {
			System.out.println(e.toString());
		}
	}
}