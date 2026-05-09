package task2;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.io.IOException;

public class ConsoleChat {
    // Multicast address and port for the chat
    private static final String MULTICAST_ADDRESS = "224.0.0.1";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            MulticastSocket socket = new MulticastSocket(PORT);
            
            // Add the socket to the multicast group
            socket.joinGroup(group); 

            // Create a thread to listen for incoming messages
            Thread listener = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    while (true) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Listening thread closed.");
                }
            });
            listener.start();

            System.out.println("You have joined the chat. Type a message (or 'exit' to quit):");
            
            // Main loop to read user input and send messages
            while (true) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) {
                    // Notify others about the exit
                    String exitMsg = name + " has left the chat.";
                    socket.send(new DatagramPacket(exitMsg.getBytes(), exitMsg.getBytes().length, group, PORT));
                    
                    socket.leaveGroup(group);
                    socket.close();
                    System.exit(0);
                }
                
                String message = name + ": " + input;
                byte[] data = message.getBytes("UTF-8");
                DatagramPacket packet = new DatagramPacket(data, data.length, group, PORT);
                socket.send(packet);
            }
        } catch (IOException e) {
            System.err.println("Network error: " + e.getMessage());
        }
    }
}