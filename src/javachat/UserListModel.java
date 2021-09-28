package javachat;

import javachat.User;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

// Class for helping organize user list after name, online, or favorite
public class UserListModel implements ListModel<User> {
	private List<User> users = new LinkedList<User>();

	public UserListModel(Set<User> userList) {
		addList(userList);
	}

	public void addList(Set<User> userList) {
		for (User u: userList)
			users.add(u);
	}

	@Override
	public void addListDataListener(ListDataListener arg0) {
	}

	@Override
	public User getElementAt(int index) {
		return users.get(index);
	}

	@Override
	public int getSize() {
		return users.size();
	}

	@Override
	public void removeListDataListener(ListDataListener arg0) {
	}

	public void sort(String button) {
		if (button.equals("Name"))
			users.sort(new SortByName());
		else if (button.equals("Favorite"))
			users.sort(new SortByFavorite());
		else if (button.equals("Online"))
			users.sort(new SortByOnline());
	}

	private class SortByName implements Comparator<User> {

		@Override
		public int compare(User u1, User u2) {
			return u1.toString().compareTo(u2.toString());
		}
	}

	private class SortByOnline implements Comparator<User> {

		@Override
		public int compare(User u1, User u2) {
			if (u1.isOnline() == u2.isOnline())
				return u1.toString().compareTo(u2.toString());
			else if (u1.isOnline() && !u2.isOnline())
				return -1;
			else
				return 1;
		}
	}

	private class SortByFavorite implements Comparator<User> {

		@Override
		public int compare(User u1, User u2) {
			if (u1.isFavorite() == u2.isFavorite())
				return u1.toString().compareTo(u2.toString());
			else if (u1.isFavorite() && !u2.isFavorite())
				return -1;
			else
				return 1;
		}
	}
}