import java.io.BufferedReader;
import java.io.IOException;
import java.net.*;


public class UDPMicroservice_Client {
	private static final String USAGE = 
	"java UDPMicroservice_Client [host] [port] [request]";

	public static void main(String[] args) throws IOException {
        if (args.length != 3) {
			System.out.println(USAGE);
			System.exit(1);
		}

        try {
        	String host = args[0];
        	int port = Integer.parseInt(args[1]);
        	String request = args[2];
        
			System.out.println("\nSending the request: " 
					+ request + " to the server!" );
   
	        // get a datagram socket
	        DatagramSocket socket = new DatagramSocket();
	
	        // send request
	        byte[] buf = new byte[1000];
			byte[] buf2 = new byte[1000];
	        buf = request.getBytes();
	        
	        InetAddress address = InetAddress.getByName(host);
	        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
	        socket.send(packet);
	    
	        // get response
	        packet = new DatagramPacket(buf2, buf2.length);
	        socket.receive(packet);
	
		    // display response
	        String received = new String(packet.getData(), 0, packet.getLength());
	        System.out.println("Server Response: " + received);
	    
	        socket.close();
        } catch (NumberFormatException e) {
			System.err.println("Invalid port number: " + args[1] + ".");
			System.exit(1);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
