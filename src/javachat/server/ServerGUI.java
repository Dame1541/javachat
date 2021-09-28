package javachat.server;

import javachat.User;
import javachat.UserCellRenderer;
import javachat.UserListModel;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

// Class for monitoring server activity
public class ServerGUI extends JPanel {
    private JTextArea taOutput = new JTextArea(10, 40);
    private JScrollPane scrollPane = new JScrollPane(taOutput);
    private UserListModel lmUsers;
    private JList<User> jlUsers = new JList<>();
    private ServerControl control;

    public ServerGUI(ServerControl control) {
        this.control = control;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Setup user list
        jlUsers.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jlUsers.setLayoutOrientation(JList.VERTICAL);
        jlUsers.setCellRenderer(new UserCellRenderer());
        jlUsers.setFixedCellWidth(200);
        JScrollPane spUsers = new JScrollPane(jlUsers);
        spUsers.setPreferredSize(new Dimension(200, 400));
        spUsers.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spUsers.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(spUsers, BorderLayout.NORTH);

        // Setup activity monitor
        taOutput.setEditable(false);
        taOutput.setLineWrap(true);
        ((DefaultCaret) taOutput.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
        // Setup traffic search button
        JButton bnTraffic = new JButton("Get traffic");
        bnTraffic.addActionListener(e -> getTraffic());
        add(bnTraffic, BorderLayout.SOUTH);
    }

    // Starts a frame for checking past traffic
    private void getTraffic() {
        JFrame frame = new JFrame("Logger");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.add(new LogViewer());
        frame.pack();
        frame.setVisible(true);
    }

    synchronized void setUserList() { // Update the user list (for example, when a new user connects)
        lmUsers = new UserListModel(control.getUserlist());
        jlUsers.setModel(lmUsers);
    }

    public void textOut(String input) {
        taOutput.append(input + "\n");
    }
}