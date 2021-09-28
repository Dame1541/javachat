package javachat.client;

import javachat.User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

// Class that creates a Chatpane, which is a panel a group of users are members of
public class ChatPane extends JScrollPane implements ActionListener {
	private StyledDocument content;
	private JTextPane tpView;
	private Set<User> members = null;
	private String title = "Main";
	private JLabel lbTitle = new JLabel(title);
	private JPanel pnComp = new JPanel();
	private int notifications = 0;

	// Constructor for main group, where all online users are members
	public ChatPane() {
		super(new JTextPane());
		tpView = (JTextPane)this.getViewport().getView();
		tpView.setLayout(new BorderLayout());
		content = tpView.getStyledDocument();
		tpView.setEditable(false);
		lbTitle.setOpaque(true);
		pnComp.add(lbTitle);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	}

	// Constructor for all other panes, with specific members
	public ChatPane(Set<User> members, String title) {
		this();
		this.members = members;
		this.title = title;
		JButton bnClose = new JButton("x");
		bnClose.setMaximumSize(new Dimension(20, 20));
		bnClose.addActionListener(this);
		pnComp.add(bnClose);
	}

	public void insertText(String input, Style style) {
		try {
			content.insertString(content.getLength(), input, style);
		} catch (BadLocationException e) {
			System.out.println(e.toString());
		}	
	}
	
	public void insertImage(Icon icon) {
		tpView.insertIcon(icon);
	}

	// Method to keep the incoming text being seen at the bottom by jumping down the pane
	public void jumpToBottom() {
		if (getVerticalScrollBar().getValue() > getVerticalScrollBar().getMaximum()-(getVerticalScrollBar().getVisibleAmount()+100))
			tpView.setCaretPosition(tpView.getDocument().getLength());
	}

	// Gets the panel at the top of the tab
	public JPanel getTabComponent() {
		return pnComp;
	}

	public Set<User> getMembers() {
		return members;
	}
	
	public void addNotification() {
		lbTitle.setBackground(new Color(255, 111, 111));
		notifications++;
		lbTitle.setText(title + "(" + notifications + ")");
	}
	
	public void removeNotification() {
		lbTitle.setBackground(new Color(238, 238, 238));
		lbTitle.setText(title);
		notifications = 0;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		getParent().remove(this);
	}
}
