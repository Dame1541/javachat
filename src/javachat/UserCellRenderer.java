package javachat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.border.BevelBorder;

// Class for showing how to render a user in a JList (name, online, favorite, avatar)
public class UserCellRenderer extends JPanel implements ListCellRenderer<User> {

    @Override
    public Component getListCellRendererComponent(JList<? extends User> list, User user, int index, boolean isSelected, boolean cellHasFocus) {
        removeAll();
        setSize(200, 100);
        JLabel lbStatus = new JLabel();
        lbStatus.setPreferredSize(new Dimension(20, 20));
        if (user.isOnline()) {
            lbStatus.setOpaque(true);
            lbStatus.setBackground(Color.GREEN);
        }
        if (user.isFavorite())
            lbStatus.setText(Character.toString((char) 9733));
        add(lbStatus);
        if (isSelected)
            setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        else
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        JLabel lbName = new JLabel(user.toString());
        lbName.setPreferredSize(new Dimension(80, 100));
        add(lbName);
        JLabel lbAvatar = new JLabel();
        lbAvatar.setPreferredSize(new Dimension(100, 100));
        lbAvatar.setIcon(ChatHelper.getScaledImage(user.getAvatar().getImageIcon(), 100, 100));
        add(lbAvatar);

        return this;
    }
}