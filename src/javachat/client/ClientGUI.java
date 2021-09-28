package javachat.client;

import javachat.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

class ClientGUI extends JPanel implements ClientUI {
    private JButton bnImage = new JButton("Select image");
    private JButton bnSend = new JButton("Send");
    private JTextArea taMessage = new JTextArea();
    private JLabel lbStatusUser = new JLabel("User not set");
    private JLabel lbStatusServer = new JLabel();
    private JTabbedPane tpChatPanes = new JTabbedPane();
    private ChatPaneController paneController = new ChatPaneController(tpChatPanes);
    private UserListModel lmUsers;
    private JList<User> jlUsers = new JList<>();
    private Controller cc;
    private String imageMessage = null;
    private JPopupMenu puUserMenu = new JPopupMenu();
    private JCheckBoxMenuItem miFavorite = new JCheckBoxMenuItem("Favorite");
    private JMenuItem miConnect = new JMenuItem("Connect");
    private JMenuItem miDisconnect = new JMenuItem("Disconnect");
    private JMenuItem miNewUser = new JMenuItem("New user");
    private JMenu mnUser = new JMenu("User");
    private JMenuBar menuBar = new JMenuBar();
    private User userInList;
    private String sortBy = "Name";

    ClientGUI(Controller cc) {
        this.cc = cc;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 400));

        // Setup input area
        JPanel pnInput = new JPanel();
        pnInput.setLayout(new BorderLayout());
        JPanel pnButtons = new JPanel();
        pnButtons.setLayout(new GridLayout(1, 2));
        bnImage.addActionListener(e -> setMessageImage());
        bnImage.setEnabled(false);
        pnButtons.add(bnImage);
        bnSend.addActionListener(e -> sendMessage());
        bnSend.setEnabled(false);
        pnButtons.add(bnSend);
        taMessage.setPreferredSize(new Dimension(500, 80));
        taMessage.setBorder(BorderFactory.createLineBorder(Color.black));
        taMessage.addKeyListener(new EnterListener());
        pnInput.add(taMessage, BorderLayout.CENTER);
        pnInput.add(pnButtons, BorderLayout.EAST);
        add(pnInput, BorderLayout.SOUTH);

        // Setup main chatroom
        ChatPane cpMain = new ChatPane();
        tpChatPanes.addTab(null, cpMain);
        tpChatPanes.setTabComponentAt(0, cpMain.getTabComponent());
        tpChatPanes.addChangeListener(e -> ((ChatPane) tpChatPanes.getSelectedComponent()).removeNotification());
        add(tpChatPanes, BorderLayout.CENTER);

        // Setup user list
        jlUsers.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jlUsers.setLayoutOrientation(JList.VERTICAL);
        jlUsers.setCellRenderer(new UserCellRenderer());
        jlUsers.setFixedCellWidth(220);
        JScrollPane spUsers = new JScrollPane(jlUsers);
        spUsers.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spUsers.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(spUsers, BorderLayout.EAST);

        // Setup user list context menu
        JMenuItem miSort = new JMenuItem("Sort by:");
        miSort.setEnabled(false);
        miFavorite.addActionListener(e -> toggleFavorite());
        puUserMenu.add(miFavorite);
        puUserMenu.add(miSort);
        ButtonGroup bnSortGroup = new ButtonGroup();
        JRadioButtonMenuItem miSortName = new JRadioButtonMenuItem("Name");
        JRadioButtonMenuItem miSortOnli = new JRadioButtonMenuItem("Online");
        JRadioButtonMenuItem miSortFavo = new JRadioButtonMenuItem("Favorite");
        bnSortGroup.add(miSortName);
        bnSortGroup.add(miSortFavo);
        bnSortGroup.add(miSortOnli);
        puUserMenu.add(miSortName);
        puUserMenu.add(miSortFavo);
        puUserMenu.add(miSortOnli);
        SortListener sortListener = new SortListener();
        miSortName.addActionListener(sortListener);
        miSortFavo.addActionListener(sortListener);
        miSortOnli.addActionListener(sortListener);
        miSortName.setSelected(true);
        jlUsers.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = jlUsers.locationToIndex(e.getPoint());
                    userInList = lmUsers.getElementAt(index);
                    miFavorite.setState(userInList.isFavorite());
                    puUserMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        // Setup menu
        JMenu mnFile = new JMenu("File");
        mnFile.setMnemonic(KeyEvent.VK_F);
        menuBar.add(mnFile);

        miConnect.setMnemonic(KeyEvent.VK_C);
        miConnect.addActionListener(e -> connect());
        miDisconnect.setEnabled(false);
        miDisconnect.setMnemonic(KeyEvent.VK_D);
        miDisconnect.addActionListener(e -> disconnect());
        miConnect.setEnabled(false);
        JMenuItem miExit = new JMenuItem("Exit");
        miExit.addActionListener(e -> exit());
        mnFile.add(miConnect);
        mnFile.add(miDisconnect);
        mnFile.addSeparator();
        mnFile.add(miExit);

        mnUser.setMnemonic(KeyEvent.VK_U);
        menuBar.add(mnUser);
        miNewUser.addActionListener(e -> addNewUser());
        mnUser.add(miNewUser);

        JMenu mnHelp = new JMenu("Help");
        mnHelp.setMnemonic(KeyEvent.VK_H);
        menuBar.add(mnHelp);

        JMenuItem miHelp = new JMenuItem("Help");
        mnHelp.setMnemonic(KeyEvent.VK_H);
        miHelp.addActionListener(e -> help());
        JMenuItem miAbout = new JMenuItem("About");
        mnHelp.setMnemonic(KeyEvent.VK_A);
        miAbout.addActionListener(e -> about());
        mnHelp.add(miHelp);
        mnHelp.addSeparator();
        mnHelp.add(miAbout);
    }

    void showUI() {
        JFrame frame = new JFrame("Client");

        // Setup statusbar
        JPanel pnStatusBar = new JPanel();
        pnStatusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));

        pnStatusBar.setPreferredSize(new Dimension(frame.getWidth(), 20));
        pnStatusBar.setLayout(new BoxLayout(pnStatusBar, BoxLayout.X_AXIS));
        pnStatusBar.add(lbStatusUser);
        pnStatusBar.add(lbStatusServer);

        setUserMenu();
        setUserList();

        frame.setLayout(new BorderLayout());
        frame.setJMenuBar(menuBar);
        frame.add(pnStatusBar, BorderLayout.SOUTH);
        frame.add(this, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    // Starts a connection to the server, sets all GUI elements
    private void connect() {
        String server = JOptionPane.showInputDialog("Enter server [IP],[port]:", "127.0.0.1,5561");
        String serverValues[] = server.split(",");
        String ip = serverValues[0];
        int port = Integer.parseInt(serverValues[1]);
        cc.connect(ip, port);
        lbStatusServer.setText(", Server: IP: " + ip + ", port: " + port);
        miConnect.setEnabled(false);
        miDisconnect.setEnabled(true);
        bnSend.setEnabled(true);
        bnImage.setEnabled(true);
        mnUser.setEnabled(false);
    }

    // Ends a connection to the server, sets all GUI elements
    public void disconnect() {
        cc.disconnect();
        miConnect.setEnabled(true);
        miDisconnect.setEnabled(false);
        lbStatusServer.setText(", Disconnected");
        bnSend.setEnabled(false);
        bnImage.setEnabled(false);
        mnUser.setEnabled(true);
    }

    private void exit() {
        disconnect();
        System.exit(0);
    }

    // Add new local user
    private void addNewUser() {
        String name = "";
        while (name.equals(""))
            name = JOptionPane.showInputDialog("Enter username:");
        String imagePath = getImagePath();
        cc.addLocalUser(name, imagePath);
        setUserSelected(name);
        setUserMenu();
    }

    void selectUser(ActionEvent e) { // Select a user from the menu to connect with
        JRadioButtonMenuItem mi = (JRadioButtonMenuItem) e.getSource();
        setUserSelected(mi.getText());
    }

    void setUserMenu() { // Update the user menu (of local users) when adding a new user
        mnUser.removeAll();
        if (cc.getLocalUsers().size() > 0) {
            ButtonGroup bgUsers = new ButtonGroup();
            for (User u : cc.getLocalUsers()) {
                JRadioButtonMenuItem mi = new JRadioButtonMenuItem(u.toString());
                mi.addActionListener(e -> selectUser(e));
                bgUsers.add(mi);
                mnUser.add(mi);
                if (cc.getLocalUser() != null && mi.getText().equals(cc.getLocalUser().toString()))
                    mi.setSelected(true);
            }
            mnUser.add(new JSeparator());
        }
        mnUser.add(miNewUser);
    }

    private void setUserSelected(String sUser) { // What to do when a user has been selected from the menu
        cc.setLocalUser(sUser);
        miConnect.setEnabled(true);
        lbStatusUser.setText("User: " + sUser);
    }

    private void help() {
        JOptionPane.showMessageDialog(null, "To connect, first set username and avatar.\n" +
                "Sending messages: press Send button or ctrl-Enter.\n" +
                "Sending private messages: Select users from the user list (hold ctrl to select more than one).\n" +
                "Managing favorites: right-click on user and select Favorite.\n" +
                "Sorting user list: right click and select sort order.");
    }

    private void about() {

    }

    synchronized void setUserList() { // Update the user list (for example, when a new user connects)
        lmUsers = new UserListModel(cc.getUserlist());
        lmUsers.sort(sortBy);
        jlUsers.setModel(lmUsers);
    }


    private void toggleFavorite() { // Toggle wether a user in the user list is a favorite or not
        cc.toggleFavorite(userInList, miFavorite.getState());
        setUserList();
        jlUsers.repaint();
        userInList = null;
    }

    void sendMessage() {
        Set<User> seReceivers = null;
        int[] iaReceivers = jlUsers.getSelectedIndices(); // Get selected users from the user list
        jlUsers.clearSelection();

        if (iaReceivers.length > 0) { // See if any users were selected
            seReceivers = new HashSet<>();
            for (int u : iaReceivers) {
                seReceivers.add(lmUsers.getElementAt(u).getThinUser());
                seReceivers.add(cc.getLocalUser().getThinUser());
            }
        } else { // If not, get the members of the current chat pane
            Set<User> members = ((ChatPane) tpChatPanes.getSelectedComponent()).getMembers();
            if (members != null) {
                seReceivers = new HashSet<>();
                for (User u : members)
                    seReceivers.add(u.getThinUser());
            }
        }

        tpChatPanes.setSelectedComponent(paneController.getChatPane(seReceivers)); // Set the selected chat pane belonging to the receiver group as active

        UserMessage userMessage = new UserMessage(cc.getLocalUser(), seReceivers, taMessage.getText(), (imageMessage != null ? new SerializableImage(imageMessage) : null));

        taMessage.setText("");
        imageMessage = null;
        cc.sendPacket(userMessage);
    }

    public void receiveMessage(UserMessage userMessage) {
        ChatPane pane = paneController.getChatPane(userMessage.getUsers());

        GUIHelper.putMessage(userMessage, pane);

        if (!tpChatPanes.getSelectedComponent().equals(pane))
            pane.addNotification();
    }

    public void receiveConnect(ConnectMessage connect) {
        setUserList();

        List<ChatPane> panes = paneController.getChatPanes(connect.getSender());
        for (ChatPane p : panes) { // Add a connect message to all panes the user is member of
            GUIHelper.putConnect(connect, p);
        }
    }

    public void receiveDisconnect(DisconnectMessage disconnect) {
        setUserList();

        List<ChatPane> panes = paneController.getChatPanes(disconnect.getSender());
        for (ChatPane p : panes) { // Add a disconnect message to all panes the user is member of
            GUIHelper.putDisconnect(disconnect, p);
        }
        // If it's this client that is disconnecting, set all GUI elements and end connection
        if (disconnect.getSender().equals(cc.getLocalUser()))
            disconnect();
    }

    void setMessageImage() {
        imageMessage = getImagePath();
    }

    // A method to get an image from a JFilePicker
    String getImagePath() {
        String imagePath = null;
        JFileChooser jfc = new JFileChooser();
        FileFilter imageFilter = new FileNameExtensionFilter(
                "Image files", ImageIO.getReaderFileSuffixes());
        jfc.setFileFilter(imageFilter);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int f = jfc.showOpenDialog(null);
        if (f == JFileChooser.APPROVE_OPTION) {
            imagePath = jfc.getSelectedFile().getPath();
        }
        return imagePath;
    }

    private class SortListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) { // Sets the user list sort order (Name, Online, Favorite)
            sortBy = ((JRadioButtonMenuItem) e.getSource()).getText();
            lmUsers.sort(sortBy);
            jlUsers.repaint();
        }
    }

    private class EnterListener implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) { // Activate ctrl-enter to send message
            if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                e.consume();
                bnSend.doClick();
            }
        }

        @Override
        public void keyReleased(KeyEvent arg0) {
        }

        @Override
        public void keyTyped(KeyEvent arg0) {
        }
    }
}