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
	    
	    ClientHandler(Socket socket) throws IOException {
	        this.clientSocket = socket;
	        this.in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
	        this.out = new PrintStream(clientSocket.getOutputStream());
	    }
	    
	    private String[] parse_cmd() throws IOException {
	        while(true) {
	            try {
	                request();
	                String[] cmd = in.readLine().split(" ", 3);
	                return cmd;
	            } catch (IOException e) {
	                out.println("Input was not a valid command. Try again.");
	            }
	            
	        }
	        
	    }

	    // COMMAND HANDLING 
	    
	    private void start_game() {
	        game_logic game = new game_logic(in, out);
            game.run();
	    }
	    
	    private void add_word() {
	        
	    }
	    
	    private void remove_word() {
	        
	    }
	    
	    private void check_word() {
	        
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
    				String[] cmd = parse_cmd();
    				if (cmd.length == 3) {
    				    try {
                            int i = Integer.parseInt(cmd[1]);
                            int f = Integer.parseInt(cmd[2]);
                            start_game();
                        } catch(NumberFormatException e) {
                            retry();
                        }
    				    
    				} else if(cmd.length == 2) {
        				switch(cmd[0]) {
        				    
        				    case "add":
        				        // microservice
        				        add_word();
        				        break;
        				        
        				    case "remove":
        				        // microservice
        				        remove_word();
        				        break;
        				        
        				    case "check":
        				        // microservice
        				        check_word();
        				        break;
        				        
        				    default:
        				        retry();
        				}
        				
        			} else {
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
		ServerSocket server = null;

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
