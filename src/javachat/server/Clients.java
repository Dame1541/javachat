package javachat.server;

import javachat.User;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Clients implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<User, Client> clients = new HashMap<>();

	public synchronized void put(User user,Client client) {
		clients.put(user,client);
	}
	
	public synchronized Client get(User user) {
		return clients.get(user);
	}
	
	public synchronized boolean containsKey(User user) {
		return clients.containsKey(user);
	}
	
	public synchronized Set<Entry<User,Client>> entrySet() {
		return clients.entrySet();
	}
}
