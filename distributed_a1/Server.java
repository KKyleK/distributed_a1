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
 * Implementation of the game server. 
 */
public class Server {

	private static final String BAD_ARG = "Error on launch.\n";
	private ServerSocket serverSocket;
	
	public static int totalScore;
	
	
	
	public Server(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		totalScore = 0;
	}
	
	/**
	 * Handles new client.
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
	        this.in =  new BufferedReader(
	        		   new InputStreamReader(clientSocket.getInputStream()));
	        this.out = new PrintStream(clientSocket.getOutputStream());
	        
	        this.socket = new DatagramSocket();
	        this.host = "localhost";
	        this.UDPport = 5550;
	        this.address = InetAddress.getByName(host);
	    }
	    
	    /**
	     * Prompts user for a command in game and waits for response.
	     * @return a string of representing the command requested by the client
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
	     * Starts a guessing game using
	     * @param words is an array of randomly generated words from the word list. 
	     */
	    private void start_game(ArrayList<String> words, int attempts) {
	    	boolean success;
	        game_logic game = new game_logic(in, out, words, attempts, socket);
	        success = game.run();
	        
	        int score;
	        if(success == true) {
	        	score = 1;
	        }
	        else {
	        	score = -1;
	        }
	        
	        doCritSect(score);
	        
	        out.println("Global score: " + totalScore);
            return;    
	    }
	    
	    
	    /**
	     * increments or decrements the servers score counter based on wins/losses of
	     * all clients. 
	     * @param score is the result of the game. 1 for a win, -1 for a loss.
	     */
	    synchronized private void doCritSect(int score)
	    {
	    	if(totalScore<=0 && score == -1) {
	    		
	    	}
	    	else {
	    		totalScore += score;
	    	}
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
				
				// Loops for command inputs.
				boolean flag = true;
				while(flag) {
    				String args = parse_args();
    				String[] cmd = args.split(" ", 3);
    				
    				// Handles different commands
    				switch(cmd[0]) {
    				    // Start game
    				    case "start":
    				        
    				        if (cmd.length != 3) {
    				            retry();
    				            break;
    				        }
    				        
    				        try {
    				            int i = Integer.parseInt(cmd[1]); // number of words
                                int f = Integer.parseInt(cmd[2]); // attempt factor
                                int attempts = i * f;
                                ArrayList<String> words = new ArrayList<String>();
                                
                                // retrieve i words from word store
                                for(int w = 0; w < i; w++) {
                                    words.add(wordstore_request("random"));
                                }
                                
                                start_game(words, attempts);
                                out.println("You are back at the main menu, select option: ");
                                out.println("| Start <i> <f> - Starts a game with i words to guess.                    |\n"
                                       + "|                 You will have i x f attempts to guess the phrase!       |\n"
                                       + "| add <word>    - Add a word to the list of possible words.               |\n"
                                       + "| remove <word> - Remove a word from the list of possible words.          |\n"
                                       + "| check <word>  - Check if a word exists in the list of possible words.   |\n"
                                       + "| Type QUIT to exit the game.                                             |\n");
                            } catch(NumberFormatException e) {
                                retry();
                            }
    				        
    				        break;
    				    
    				    // add word to word store
    				    case "add":
    				        out.println(wordstore_request(args));
    				        
    				        break;
    				    
    				    // remove word from word store    
    				    case "remove":
    				        out.println(wordstore_request(args));
    				        break;
    				    
    				    // check if word exists in word store
    				    case "check":
    				        out.println(wordstore_request(args));
    				        break;
    				        
    				    // quit game
    				    case "QUIT":
    				        flag = false;
    				        break;
    				    
    				    // no command, retry
    				    default:
    				        retry();
    				}
    				
				}
    		    
				// Close client connection
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
		int port = 5599;
		ServerSocket server = null;
		
		// Open connection for clients
		try {
			port = 5599;
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
