import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class EchoClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 8080;

        try (DatagramSocket socket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in)) {
            
            // Time-out 2 seconds for receiving response5555
            socket.setSoTimeout(2000);
            InetAddress address = InetAddress.getByName(hostname);

            System.out.print("Enter message to send: ");
            String message = scanner.nextLine();
            byte[] buffer = message.getBytes();

            // Sending the message to the server
            DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(request);
            System.out.println("Message sent to " + hostname);

            // Getting the response from the server
            byte[] responseBuffer = new byte[1024];
            DatagramPacket response = new DatagramPacket(responseBuffer, responseBuffer.length);

            try {
                socket.receive(response);
                String echoedMessage = new String(response.getData(), 0, response.getLength());
                System.out.println("Server returned: " + echoedMessage);
            } catch (java.net.SocketTimeoutException e) {
                System.out.println("Error: Timeout. Server did not respond.");
            }

        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}