package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import model.Message;

public class Controller implements ActionListener {

    private final ChatWindow chatWindow;

    // Keep track of buttons by their action names
    private final Map<String, JButton> registeredButtons = new HashMap<>();

    public static final String SEND = "SEND";

    // Socket and streams used for talking to Server.java
    private volatile Socket socket;
    private volatile DataOutputStream output;
    private volatile String username;
    private volatile boolean connected;

    // Separate thread for sending so the Swing window does not freeze
    private final ExecutorService sendThread = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "chat-send-thread");
        thread.setDaemon(true);
        return thread;
    });

    public Controller(ChatWindow chatWindow) {
        this.chatWindow = chatWindow;

        // Register the real Send button and Enter key with this controller
        registerButton(SEND, chatWindow.getSendButton());
        chatWindow.registerEnterAction(this);
    }

    // Register an actual Swing button with an action name
    public void registerButton(String id, JButton button) {
        if (id == null || id.isBlank() || button == null) {
            throw new IllegalArgumentException("Button ID and button are required");
        }
        if (registeredButtons.containsKey(id)) {
            throw new IllegalArgumentException("Button already registered: " + id);
        }
        button.setActionCommand(id);
        button.addActionListener(this);
        registeredButtons.put(id, button);
    }

    // Let another part of the program find a registered button
    public JButton getButton(String id) {
        return registeredButtons.get(id);
    }

    // Return the buttons 
    public Map<String, JButton> getAllButtons() {
        return Collections.unmodifiableMap(registeredButtons);
    }

    // Keep all button clicks in one place, like our team's example
    @Override
    public void actionPerformed(ActionEvent event) {
        switch (event.getActionCommand()) {
            case SEND:
                btnSend();
                break;
            default:
                // TODO: Need to Add more actions
                break;
        }
    }

    // Get the selected user and typed text from the chat window
    public void btnSend() {
        String recipient = chatWindow.getSelectedRecipient();
        String text = chatWindow.getMessageText();
        if (recipient == null || text.isEmpty()) {
            return;
        }
        sendMessage(recipient, text);
    }

    // Connect to the server
    public void connect(String host, int port) {
        Thread receiveThread = new Thread(() -> {
            String result = "Disconnected from server";
            try (Socket connection = new Socket()) {

                connection.connect(new InetSocketAddress(host, port), 5000);
                socket = connection;

                DataInputStream input = new DataInputStream(connection.getInputStream());
                output = new DataOutputStream(connection.getOutputStream());

                while (!connection.isClosed()) {
                    String received = input.readUTF();
                    handleServerMessage(received);
                }

            } catch (IOException e) {
                result = "Connection error: " + e.getMessage();
            } finally {
                connected = false;
                output = null;
                socket = null;
                showStatus(result, false);
            }
        }, "chat-receive-thread");

        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    // Deciding whether a received line is our username, a message or a server notice
    private void handleServerMessage(String received) {

        // Server currently sends: Connected as client N. Use message>client 0 or logout.
        if (received.startsWith("Connected as ")) {
            int start = "Connected as ".length();
            int end = received.indexOf('.', start);

            if (end > start) {
                username = received.substring(start, end).trim();
                connected = true;

                SwingUtilities.invokeLater(() -> {
                    chatWindow.setMyUsername(username);
                    chatWindow.setConnectionStatus("Connected as " + username, true);
                });
                return;
            }
        }

        // These are server notices, not messages from other users
        if (received.startsWith("Recipient not connected:")
                || received.startsWith("Use: ")) {
            showStatus(received, connected);
            return;
        }

        // Current server sends private messages as: sender : message
        int separator = received.indexOf(" : ");
        if (separator > 0) {
            String sender = received.substring(0, separator).trim();
            String text = received.substring(separator + 3);

            SwingUtilities.invokeLater(() -> {
                chatWindow.addReceivedMessage(sender, text);
            });
            return;
        }

        // Show unexpected server messages as status
        showStatus(received, connected);
    }

    // Called when the real Send button or Enter is pressed
    private void sendMessage(String recipient, String text) {

        if (!connected || output == null || username == null) {
            showStatus("Not connected to the server", false);
            return;
        }


        // Use the existing Message model with the real username from the server
        Message message = new Message(username, text);

        sendThread.execute(() -> {
            try {
                // Server.java expects the message first, then >, then recipient name
                DataOutputStream stream = output;
                if (stream == null) {
                    throw new IOException("Not connected");
                }
                synchronized (stream) {
                    stream.writeUTF(message.getTxt() + ">" + recipient);
                    stream.flush();
                }

                SwingUtilities.invokeLater(() -> {
                    chatWindow.addSentMessage(recipient, message.getTxt());
                    chatWindow.clearMessageIfUnchanged(text);
                });
            } catch (IOException e) {
                showStatus("Could not send message: " + e.getMessage(), connected);
            }
        });
    }

    // TODO: Server.active has the connected client handlers, but the server
    // does not send the user list to clients. When we fixed it then
    // call this method with the real usernames instead of sample names.
    public void updateConnectedUsers(List<String> usernames) {
        SwingUtilities.invokeLater(() -> chatWindow.setConnectedUsers(usernames));
    }

    // TODO: Login and Create Account will be added by the TA.

    // TODO: Group chats

    // Show server or connection notices on Swing's UI thread
    private void showStatus(String text, boolean isConnected) {
        SwingUtilities.invokeLater(() -> chatWindow.setConnectionStatus(text, isConnected));
    }

    // Close the socket when the application window closes
    public void disconnect() {
        connected = false;
        Socket connection = socket;
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException ignored) {
                // Socket may already be closed
            }
        }
        sendThread.shutdownNow();
    }
}
