package javachat.server;

import javachat.ConnectMessage;
import javachat.DisconnectMessage;
import javachat.Message;
import javachat.UserMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * LogViewer is a program that can be used to inspect packets sent/received.
 */
public class LogViewer extends JPanel {
    JTextArea textArea = new JTextArea(24, 80);
    JScrollPane scrollPane = new JScrollPane(textArea);
    JLabel lblStatus = new JLabel("Status");
    private String path;
    private PacketLogger packetLogger;
    private Calendar calFrom, calTo;

    /**
     * Creates a LogViewer with a default log path set.
     */
    public LogViewer() {
        this("data/log.dat");
    }

    /**
     * Creates a LogViewer that reads from a specified file.
     *
     * @param path The path to the log file to read.
     */
    public LogViewer(String path) {
        this.path = path;

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        add(makeFilterPanel(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);

        load();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("LogViewer");
            frame.add(new LogViewer());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    /**
     * Reads the server log and updates the UI to show how many packets were read.
     */
    private void load() {
        packetLogger = new PacketLogger(path);
        lblStatus.setText("Packets loaded: " + packetLogger.size());
    }

    /**
     * Asks for date/time input from the user.
     *
     * @return An instance of Calendar with time set by the user, or null if invalid format/no entry.
     */
    private Calendar datePicker() {
        Calendar calendar = Calendar.getInstance();
        String input = JOptionPane.showInputDialog("Enter date, format: YYYY-mm-dd HH:mm");

        if (input == null) {
            return null;
        }

        // "YYYY-mm-dd HH:mm".length() = 16
        if (input.length() != 16) {
            JOptionPane.showMessageDialog(null, "Invalid format, expected YYYY-mm-dd HH:mm.");
            return null;
        }

        String sYear = input.substring(0, 4);
        String sMonth = input.substring(5, 7);
        String sDay = input.substring(8, 10);
        String sHour = input.substring(11, 13);
        String sMinute = input.substring(14, 16);

        try {
            calendar.set(Calendar.YEAR, Integer.parseInt(sYear));
            calendar.set(Calendar.MONTH, Integer.parseInt(sMonth) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sDay));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sHour));
            calendar.set(Calendar.MINUTE, Integer.parseInt(sMinute));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input, got:" + e.toString());
            return null;
        }

        return calendar;
    }

    /**
     * Formats a Calendar object into the format YYYY-mm-dd HH:mm:ss
     *
     * @param c Instance of Calendar
     * @return Formatted date.
     */
    private String formatCalendar(Calendar c) {
        return String.format("%04d-%02d-%02d %02d:%02d",
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE));
    }

    private JPanel makeFilterPanel() {
        final JButton
                btnFromDate = new JButton("From"),
                btnToDate = new JButton("To");
        final JTextField
                tfFrom = new JTextField(11),
                tfTo = new JTextField(11);

        ActionListener buttonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar c = datePicker();
                if (c == null) {
                    return;
                }

                Object src = e.getSource();
                if (src == btnFromDate) {
                    tfFrom.setText(formatCalendar(c));
                    calFrom = c;
                } else if (src == btnToDate) {
                    tfTo.setText(formatCalendar(c));
                    calTo = c;
                }
            }
        };

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        panel.add(new JLabel("Date range:"));
        tfFrom.setEditable(false);
        panel.add(tfFrom);

        btnFromDate.addActionListener(buttonListener);
        panel.add(btnFromDate);

        panel.add(new JLabel("-"));

        tfTo.setEditable(false);
        panel.add(tfTo);

        btnToDate.addActionListener(buttonListener);
        panel.add(btnToDate);

        JButton btnFilter = new JButton("Filter");
        btnFilter.addActionListener(e -> filter());
        panel.add(btnFilter);

        JButton btnReloadLog = new JButton("Reload log");
        btnReloadLog.addActionListener(e -> load());
        panel.add(btnReloadLog);

        return panel;
    }

    /**
     * Requests packets received during the specified range, then writes them to the text window.
     */
    private void filter() {
        if (calFrom == null || calTo == null) {
            JOptionPane.showMessageDialog(null, "Set the date range before filtering.");
            return;
        }
        List<Message> messages = packetLogger.filter(calFrom, calTo);
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Message p : messages) {
            sb.append("[");
            sb.append(sdf.format(p.getArrived()));
            sb.append("] ");

            if (p instanceof ConnectMessage) {
                sb.append("ConnectMessage: ");
                sb.append(p.getSender().getName());
                sb.append('\n');
            } else if (p instanceof DisconnectMessage) {
                sb.append("DisconnectMessage: ");
                sb.append(p.getSender().getName());
                sb.append('\n');
            } else if (p instanceof UserMessage) {
                sb.append('<');
                sb.append(p.getSender().getName());
                sb.append("> -> ");
                sb.append(p.getUsers() != null ? p.getUsers().toString() : "All");
                sb.append(": ");
                sb.append(((UserMessage) p).getContent());
                sb.append('\n');
            } else {
                sb.append("Message type: ");
                sb.append(p.getClass().getSimpleName());
                sb.append(", toString() = ");
                sb.append(p.toString());
                sb.append('\n');
            }
        }
        textArea.setText(sb.toString());
    }
}
