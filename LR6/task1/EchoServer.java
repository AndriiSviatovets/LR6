import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class EchoServer {
    public static void main(String[] args) {
        int port = 8080;
        
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Server launched on port " + port + ". Waiting for packets...");

            byte[] buffer = new byte[1024];

            while (true) {
                // Create a packet to receive incoming data
                DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
                
                // Wait for incoming packet
                socket.receive(incomingPacket);
                
                String receivedText = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
                System.out.println("Received from client: [" + receivedText + "]");

                // Send the same data back to the client
                DatagramPacket outgoingPacket = new DatagramPacket(
                    incomingPacket.getData(),
                    incomingPacket.getLength(),
                    incomingPacket.getAddress(),
                    incomingPacket.getPort()
                );
                
                socket.send(outgoingPacket);
                System.out.println("Echo response sent.");
            }
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}