
package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class ChatWindow extends JFrame {

    // Colors for our dark chat interface
    private final Color background = new Color(10, 10, 10);
    private final Color panelColor = new Color(20, 20, 20);
    private final Color messageColor = new Color(49, 49, 49);
    private final Color textColor = new Color(235, 235, 235);

    // Main components used in the chat window
    private final JPanel messagesPanel;
    private final JTextField messageField;
    private final JLabel chatTitle;
    private final Map<String, StringBuilder> chatHistory = new HashMap<>();

    private String currentChat = "Team Chat";

    public ChatWindow() {

        // Set the window size and title
        setTitle("Navy Chatroom - Team 3 Ravens");
        setSize(900, 550);
        setMinimumSize(new Dimension(650, 400));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main black background
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setBackground(background);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Create the conversations panel on the left
        JPanel sidebar = new JPanel(new BorderLayout(0, 15));
        sidebar.setBackground(background);
        sidebar.setPreferredSize(new Dimension(260, 0));

        JLabel sidebarTitle = new JLabel("Chats");
        sidebarTitle.setForeground(textColor);
        sidebarTitle.setFont(new Font("Arial", Font.BOLD, 12));
        sidebar.add(sidebarTitle, BorderLayout.NORTH);

        // Sample chats for testing the interface
        String[] conversations = {"Team Chat", "Sailor A", "Sailor B"};

        JList<String> conversationList = new JList<>(conversations);
        conversationList.setBackground(background);
        conversationList.setForeground(textColor);
        conversationList.setFont(new Font("Arial", Font.BOLD, 15));
        conversationList.setSelectionBackground(messageColor);
        conversationList.setSelectionForeground(Color.WHITE);
        conversationList.setFixedCellHeight(65);
        conversationList.setBorder(null);

        sidebar.add(new JScrollPane(conversationList), BorderLayout.CENTER);
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Create the chat section on the right
        JPanel chatPanel = new JPanel(new BorderLayout(0, 20));
        chatPanel.setBackground(background);

        chatTitle = new JLabel(currentChat);
        chatTitle.setForeground(textColor);
        chatTitle.setFont(new Font("Arial", Font.BOLD, 17));

        chatPanel.add(chatTitle, BorderLayout.NORTH);

        // Panel where messages will be displayed
        messagesPanel = new JPanel();
        messagesPanel.setLayout(
                new BoxLayout(messagesPanel, BoxLayout.Y_AXIS)
        );
        messagesPanel.setBackground(background);

        JScrollPane messageScroll = new JScrollPane(messagesPanel);
        messageScroll.setBorder(null);
        messageScroll.getViewport().setBackground(background);
        chatPanel.add(messageScroll, BorderLayout.CENTER);

        // Bottom panel containing the message box
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(background);

        messageField = new JTextField();
        messageField.setBackground(messageColor);
        messageField.setForeground(textColor);
        messageField.setCaretColor(Color.WHITE);
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        messageField.setBorder(new EmptyBorder(12, 12, 12, 12));

        JButton sendButton = new JButton("➤");
        sendButton.setBackground(messageColor);
        sendButton.setForeground(textColor);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(new EmptyBorder(10, 14, 10, 14));

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        mainPanel.add(chatPanel, BorderLayout.CENTER);

        // Add sample messages so we can see the layout
        chatHistory.put("Team Chat",
                new StringBuilder(
                        "Sailor A: Is everyone connected?\n" +
                        "You: Yes, ready!\n"
                )
        );

        // Show the Team Chat when the window opens
        conversationList.setSelectedIndex(0);
        showChat(currentChat);

        // Change the conversation when a user selects another chat
        conversationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                currentChat = conversationList.getSelectedValue();
                if (currentChat != null) {
                    showChat(currentChat);
                }
            }
        });

        // Send messages by clicking the button or pressing Enter
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
    }

    // Display messages from the selected conversation
    private void showChat(String chatName) {

        chatTitle.setText(chatName);
        messagesPanel.removeAll();

        StringBuilder history = chatHistory.get(chatName);

        if (history != null) {
            for (String line : history.toString().split("\n")) {
                if (line.isEmpty()) {
                    continue;
                }

                boolean isSent = line.startsWith("You:");

                JPanel row = new JPanel(new FlowLayout(
                        isSent ? FlowLayout.RIGHT : FlowLayout.LEFT
                ));
                row.setBackground(background);

                JLabel messageLabel = new JLabel(line);
                messageLabel.setOpaque(true);
                messageLabel.setBackground(messageColor);
                messageLabel.setForeground(textColor);
                messageLabel.setBorder(
                        new EmptyBorder(12, 12, 12, 12)
                );

                row.add(messageLabel);
                messagesPanel.add(row);
            }
        }

        // Refresh the messages when switching conversations
        messagesPanel.revalidate();
        messagesPanel.repaint();
    }

    // Add a new message to the selected conversation
    private void sendMessage() {

        String text = messageField.getText().trim();

        if (!text.isEmpty()) {

            chatHistory.putIfAbsent(
                    currentChat, new StringBuilder()
            );

            chatHistory.get(currentChat)
                    .append("You: ")
                    .append(text)
                    .append("\n");

            messageField.setText("");
            showChat(currentChat);
        }
    }

    // Start the Java Swing application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatWindow window = new ChatWindow();
            window.setVisible(true);
        });
    }
}
