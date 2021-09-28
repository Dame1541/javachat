package javachat;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public abstract class Message implements Serializable {

	private static final long serialVersionUID = -8375731807804305953L;
	private User sender;
	protected Set<User> users;
	private Date arrived;
	private Date delivered;
	
	public Message(User sender) {
		this.sender = sender;
	}
	
	public User getSender() {
		return sender;
	}
	
	public Set<User> getUsers() {
		return users;
	}
	
	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
	public void setArrived(Date date) {
		arrived = date;
	}

	public Date getArrived() {
		return arrived;
	}

	public void setDelivered(Date date) {
		delivered = date;
	}
	
	public Date getDelivered() {
		return delivered;
	}
}
