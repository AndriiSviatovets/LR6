package task2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.io.IOException;

public class GUIChat extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private String userName;
    private MulticastSocket socket;
    private InetAddress group;
    
    private static final String MULTICAST_ADDRESS = "224.0.0.1";
    private static final int PORT = 8080;

    public GUIChat() {
        // Prompt for the user's name at startup
        userName = JOptionPane.showInputDialog(this, "Enter your name:", "Join Chat", JOptionPane.PLAIN_MESSAGE);
        if (userName == null || userName.trim().isEmpty()) {
            System.exit(0);
        }

        // Set up the GUI
        setTitle("Multicast Conference - " + userName);
        setSize(450, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Text area for the chat
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Input panel with text field and send button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendButton = new JButton("Send");
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action listener for the send button and Enter key
        ActionListener sendAction = e -> sendMessage();
        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);

        // Initialize the network
        initNetwork();
    }

    private void initNetwork() {
        try {
            group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket = new MulticastSocket(PORT);
            socket.joinGroup(group);

            // Thread to listen for incoming messages
            Thread listener = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    while (true) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                        
                        // Update the chat area on the Event Dispatch Thread
                        SwingUtilities.invokeLater(() -> chatArea.append(message + "\n"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            listener.start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Network error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            try {
                String message = userName + ": " + text;
                byte[] data = message.getBytes("UTF-8");
                DatagramPacket packet = new DatagramPacket(data, data.length, group, PORT);
                socket.send(packet);
                inputField.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GUIChat().setVisible(true);
        });
    }
}