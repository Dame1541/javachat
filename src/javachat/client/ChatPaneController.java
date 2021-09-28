package javachat.client;

import javachat.User;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

// Control class for all Chatpanes, to get a pane with a specific
// set of members, och get all panes with a single user
public class ChatPaneController {
    JTabbedPane parent;

    ChatPaneController(JTabbedPane parent) {
        this.parent = parent;
    }

    ChatPane getChatPane(Set<User> users) {
        ChatPane pane;
        if (users == null) {
            pane = (ChatPane) parent.getComponentAt(0);
        } else {
            int tab = tabExists(users);
            if (tab == -1) {
                pane = newChatGroup(users);
            } else {
                pane = (ChatPane) parent.getComponentAt(tab);
            }
        }
        return pane;
    }

    // Get panes user is member of
    List<ChatPane> getChatPanes(User user) {
        List<ChatPane> panes = new LinkedList<>();
        panes.add((ChatPane) parent.getComponentAt(0)); // Add main pain (where members = null)
        int count = parent.getTabCount();
        for (int i = 1; i < count; i++) { // Cycle through the other panes,
            ChatPane pane = ((ChatPane) parent.getComponentAt(i));
            if (pane.getMembers().contains(user))
                panes.add(pane); // And add if user is member, add pane
        }
        return panes;
    }

    // Check if there exists a tab for the group users, and return its tab number if so, else return -1
    int tabExists(Set<User> users) {
        for (int t = 1; t < parent.getTabCount(); t++)
            if (((ChatPane) parent.getComponentAt(t)).getMembers().equals(users))
                return t;
        return -1;
    }

    ChatPane newChatGroup(Set<User> users) {
        StringBuilder title = new StringBuilder();
        for (User u : users)
            title.append(u).append(", ");
        title.delete(title.length() - 2, title.length());
        ChatPane pane = new ChatPane(users, title.toString());
        parent.addTab(null, pane);
        int tabNumber = parent.getTabCount() - 1;
        parent.setTabComponentAt(tabNumber, pane.getTabComponent());
        return pane;
    }
}