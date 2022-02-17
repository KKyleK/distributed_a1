package distributed_a1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.*;
import java.net.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of a Basic Server Program.
 * 
 * @author Donald
 *
 */
public class Server {

	private static final String BAD_ARG = "Please specify a port number.\n";
	private ServerSocket serverSocket;

	public Server(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

	/**
	 * Deals with the client.
	 */
	private static class ClientHandler implements Runnable {
	    private Socket clientSocket;
	    private BufferedReader in;
	    private PrintStream out;
	    
	    private DatagramSocket socket;
	    private String host;
	    private int UDPport;
	    private InetAddress address;
	    
	    ClientHandler(Socket socket) throws IOException {
	        this.clientSocket = socket;
	        this.in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
	        this.out = new PrintStream(clientSocket.getOutputStream());
	        
	        this.socket = new DatagramSocket();
	        this.host = "localhost";
	        this.UDPport = 5550;
	        this.address = InetAddress.getByName(host);
	        
	    }
	    
	    /**
	     * 
	     * @return
	     * @throws IOException
	     */
	    private String parse_args() throws IOException {
	        while(true) {
	            try {
	                request();
	                String args = in.readLine();
	                return args;
	            } catch (IOException e) {
	                retry();
	            }
	            
	        }
	        
	    }


	    /**
	     * Starts a guessing game
	     */
	    private void start_game() {
	        game_logic game = new game_logic(in, out);
            game.run();
	    }
	  
	    /**
	     * Makes the desired request to the word store
	     * @param cmd
	     * @return
	     */
	    private String wordstore_request(String cmd) {
	        try {
	            byte[] buf = new byte[1000];
                byte[] buf2 = new byte[1000];
                buf = cmd.getBytes();
                
                // send request
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, UDPport);
                socket.send(packet);
                
                // get response
                packet = new DatagramPacket(buf2, buf2.length);
                socket.receive(packet);
                
                String received = new String(packet.getData(), 0, packet.getLength());
                return received;
	        } catch (Exception e) {
	            System.err.println(e.getMessage());
                System.exit(1);
	        }
            return null;
	    }
	    
	    
	    /**
	     * Sends a signal to the client to wait for input.
	     */
	    private void request(){
	        out.println("\u0005");
	    }
	    
	    // Print a error message prior to re-prompting for a command.
	    private void retry() {
            out.println("Invalid command. Please try again");
        }
	    
	    /**
	     * Reads a number from input.
	     * @return
	     * @throws IOException 
	     */
    	public void run() {
			try {
				System.out.println("Client Connected");
				System.out.println();
				
				boolean flag = true;
				while(flag) {
    				String args = parse_args();
    				String[] cmd = args.split(" ", 3);
    				switch(cmd[0]) {
    				    case "start":
    				        
    				        if (cmd.length != 3) {
    				            retry();
    				            break;
    				        }
    				        
    				        try {
                                int i = Integer.parseInt(cmd[1]);
                                int f = Integer.parseInt(cmd[2]);
                                int attempts = i * f;
                                ArrayList<String> words = new ArrayList<String>();
                                
                                // retrieve i words from wordstore
                                for(int w = 0; w < i; w++) {
                                    words.add(wordstore_request("random"));
                                }
                                System.out.println(words);
                                
                                //start_game(words, attempts);
                            } catch(NumberFormatException e) {
                                retry();
                            }
    				        
    				        break;
    				        
    				    case "add":
    				        System.out.println(wordstore_request(args));
    				        
    				        break;
    				        
    				    case "remove":
    				        System.out.println(wordstore_request(args));
    				        break;
    				        
    				    case "check":
    				        System.out.println(wordstore_request(args));
    				        break;
    				        
    				    default:
    				        retry();
    				}
				}
    		       
		        out.println("Game over, Ending Connection");
				clientSocket.close();
		        System.out.println("Client Disconnected");
			} catch (SocketException e) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
	}
	

	
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println(BAD_ARG);
			System.exit(1);
		}
		int port = 0;
		String host = "localhost";
		ServerSocket server = null;
		
		// Connect to word store
		try {
		    DatagramSocket socket = new DatagramSocket();
		    InetAddress address = InetAddress.getByName(host);
		} catch(Exception e) {
		    System.err.println(e.getMessage());
		    System.exit(1);
		}
		
		// Open connection for clients
		try {
			port = Integer.parseInt(args[0]);
			server = new ServerSocket(port);
			System.out.println("The server is running...");
            ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5); // Handle up to 5 clients
            while (true) {
                fixedThreadPool.execute(new ClientHandler(server.accept()));
            }
		} catch (IOException e) {
			System.out.println("Unable to establish server on given port\n");
		}
	}
}
