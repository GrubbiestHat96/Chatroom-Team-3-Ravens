package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionListener;

public class ChatWindow extends JFrame {

    // Colors for our dark chat window
    private final Color background = new Color(10, 10, 10);
    private final Color messageColor = new Color(49, 49, 49);
    private final Color textColor = new Color(235, 235, 235);

    // List starts empty until we get real users from the server
    private final DefaultListModel<String> conversationModel = new DefaultListModel<>();
    private final JList<String> conversationList = new JList<>(conversationModel);

    // Components used to display and send messages
    private final JPanel messagesPanel = new JPanel();
    private final JTextField messageField = new JTextField();
    private final JButton sendButton = new JButton("➤");
    private final JLabel chatTitle = new JLabel("Select a chat");
    private final JLabel statusLabel = new JLabel("Not connected");
    private final JScrollPane messageScroll;

    // Keep messages separate for each conversation while the app is open
    private final Map<String, List<ChatLine>> chatHistory = new HashMap<>();

    // The controller registers the Send button and handles its clicks
    private boolean connected = false;
    private String myUsername;

    // Store the message and whether it was sent by us
    private static class ChatLine {
        final String sender;
        final String text;
        final boolean sentByMe;

        ChatLine(String sender, String text, boolean sentByMe) {
            this.sender = sender;
            this.text = text;
            this.sentByMe = sentByMe;
        }
    }

    public ChatWindow() {

        // Set up the main window
        setTitle("Navy Chatroom - Team 3 Ravens");
        setSize(900, 550);
        setMinimumSize(new Dimension(650, 400));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 12));
        mainPanel.setBackground(background);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Left side for conversations
        JPanel sidebar = new JPanel(new BorderLayout(0, 15));
        sidebar.setBackground(background);
        sidebar.setPreferredSize(new Dimension(260, 0));

        JLabel sidebarTitle = new JLabel("Chats");
        sidebarTitle.setForeground(textColor);
        sidebarTitle.setFont(new Font("Arial", Font.BOLD, 12));
        sidebar.add(sidebarTitle, BorderLayout.NORTH);

        conversationList.setBackground(background);
        conversationList.setForeground(textColor);
        conversationList.setSelectionBackground(messageColor);
        conversationList.setSelectionForeground(Color.WHITE);
        conversationList.setFont(new Font("Arial", Font.BOLD, 15));
        conversationList.setFixedCellHeight(65);

        JScrollPane conversationsScroll = new JScrollPane(conversationList);
        conversationsScroll.setBorder(null);
        sidebar.add(conversationsScroll, BorderLayout.CENTER);
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Right side for the selected chat
        JPanel chatPanel = new JPanel(new BorderLayout(0, 20));
        chatPanel.setBackground(background);

        chatTitle.setForeground(textColor);
        chatTitle.setFont(new Font("Arial", Font.BOLD, 17));
        chatPanel.add(chatTitle, BorderLayout.NORTH);

        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(background);

        messageScroll = new JScrollPane(messagesPanel);
        messageScroll.setBorder(null);
        messageScroll.getViewport().setBackground(background);
        chatPanel.add(messageScroll, BorderLayout.CENTER);

        // Bottom input field and Send button
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(background);

        messageField.setBackground(messageColor);
        messageField.setForeground(textColor);
        messageField.setCaretColor(Color.WHITE);
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        messageField.setBorder(new EmptyBorder(12, 12, 12, 12));

        sendButton.setBackground(messageColor);
        sendButton.setForeground(textColor);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(new EmptyBorder(10, 14, 10, 14));

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        mainPanel.add(chatPanel, BorderLayout.CENTER);

        // Status shows the connection and assigned username
        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        // Change the messages when a different chat is selected
        conversationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showChat(conversationList.getSelectedValue());
                updateSendButton();
            }
        });

        // The controller will register the Send button and Enter key
        updateSendButton();
    }

    // Give the controller the actual Send button to register
    public JButton getSendButton() {
        return sendButton;
    }

    // Pressing Enter should do the same thing as clicking Send
    public void registerEnterAction(ActionListener listener) {
        messageField.setActionCommand("SEND");
        messageField.addActionListener(listener);
    }

    // Read the conversation selected in the list
    public String getSelectedRecipient() {
        return conversationList.getSelectedValue();
    }

    // Read the message that was typed
    public String getMessageText() {
        return messageField.getText().trim();
    }

    // Update the connection status shown at the bottom
    public void setConnectionStatus(String status, boolean isConnected) {
        connected = isConnected;
        statusLabel.setText(status);
        updateSendButton();
    }

    // This username comes from the server after connecting
    public void setMyUsername(String username) {
        myUsername = username;
    }

    // TODO: server.ClientHandler has an active list but it doesn't send that list
    // to the clients yet. Call this method once that server feature is ready.
    public void setConnectedUsers(List<String> usernames) {
        String selected = conversationList.getSelectedValue();
        conversationModel.clear();

        for (String username : usernames) {
            if (username != null && !username.isBlank()
                    && !username.equals(myUsername)
                    && !conversationModel.contains(username)) {
                conversationModel.addElement(username);
            }
        }

        if (selected != null && conversationModel.contains(selected)) {
            conversationList.setSelectedValue(selected, true);
        } else {
            conversationList.clearSelection();
            showChat(null);
        }
        updateSendButton();
    }

    // Add a real conversation when a message arrives from that user
    public void addConversation(String username) {
        if (username == null || username.isBlank() || username.equals(myUsername)) {
            return;
        }
        if (!conversationModel.contains(username)) {
            conversationModel.addElement(username);
        }
    }

    // Called by the controller after the message was written to the socket
    public void addSentMessage(String recipient, String text) {
        saveMessage(recipient, new ChatLine(myUsername, text, true));
    }

    // Called by the controller when a message arrives from the server
    public void addReceivedMessage(String sender, String text) {
        addConversation(sender);
        saveMessage(sender, new ChatLine(sender, text, false));
    }

    // Save a message in the right conversation
    private void saveMessage(String chat, ChatLine line) {
        chatHistory.computeIfAbsent(chat, name -> new ArrayList<>()).add(line);
        if (chat.equals(conversationList.getSelectedValue())) {
            showChat(chat);
        }
    }

    // Draw messages for the selected conversation
    private void showChat(String chat) {
        chatTitle.setText(chat == null ? "Select a chat" : chat);
        messagesPanel.removeAll();

        if (chat != null) {
            for (ChatLine line : chatHistory.getOrDefault(chat, List.of())) {
                JPanel row = new JPanel(new FlowLayout(
                        line.sentByMe ? FlowLayout.RIGHT : FlowLayout.LEFT, 6, 5));
                row.setBackground(background);

                JPanel bubble = new JPanel(new BorderLayout(0, 4));
                bubble.setBackground(messageColor);
                bubble.setBorder(new EmptyBorder(10, 12, 10, 12));

                JLabel senderLabel = new JLabel(line.sentByMe ? "You" : line.sender);
                senderLabel.setForeground(Color.LIGHT_GRAY);
                senderLabel.setFont(new Font("Arial", Font.BOLD, 11));

                JTextArea messageText = new JTextArea(line.text);
                messageText.setColumns(28);
                messageText.setLineWrap(true);
                messageText.setWrapStyleWord(true);
                messageText.setEditable(false);
                messageText.setFocusable(false);
                messageText.setForeground(textColor);
                messageText.setBackground(messageColor);
                messageText.setFont(new Font("Arial", Font.PLAIN, 13));
                messageText.setBorder(null);

                bubble.add(senderLabel, BorderLayout.NORTH);
                bubble.add(messageText, BorderLayout.CENTER);
                row.add(bubble);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                        bubble.getPreferredSize().height + 10));
                messagesPanel.add(row);
            }
        }

        messagesPanel.revalidate();
        messagesPanel.repaint();
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = messageScroll.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    // Clear input after the controller reports the socket write succeeded
    public void clearMessageIfUnchanged(String text) {
        if (messageField.getText().trim().equals(text)) {
            messageField.setText("");
        }
    }

    // Keep sending disabled until connected and a conversation is selected
    private void updateSendButton() {
        boolean enabled = connected && conversationList.getSelectedValue() != null;
        messageField.setEnabled(enabled);
        sendButton.setEnabled(enabled);
    }

    // TODO: Group chats
    // TODO: Login and Create Account screens are being made by TA
    // TODO: Chat history here is temporary until DatabaseManager is implemented

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";

        SwingUtilities.invokeLater(() -> {
            ChatWindow window = new ChatWindow();
            Controller controller = new Controller(window);

            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    controller.disconnect();
                }
            });

            window.setVisible(true);
            controller.connect(host, 5000);
        });
    }
}
