package javachat;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = -8518522418186109463L;
	private String name;
	private SerializableImage avatar;
	private boolean online = false;
	private boolean favorite = false;

	public User(String name, SerializableImage avatar) {
		this.name = name;
		this.avatar = avatar;
	}

	public String getName() {
		return name;
	}

	public void setAvatar(SerializableImage avatar) {
		this.avatar = avatar;
	}

	public SerializableImage getAvatar() {
		return avatar;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public boolean isOnline() {
		return online;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public boolean isFavorite() {
		return favorite;
	}

	@Override
	public String toString() {
		return name;
	}

	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		// self check
		if (this == o) {
			return true;
		}

		if (!(o instanceof User)) {
			return false;
		}

		User u = (User) o;

		// field comparison
		return u.toString().equals(name);                                       
	}
	
	public User getThinUser() {
		User u = new User(name, null);
		u.setOnline(true);
		return u;
	}
}