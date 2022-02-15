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
	    
	    ClientHandler(Socket socket) {
	        this.clientSocket = socket;
	    }
	    
    	public void run() {
    		while (true) {
    			try {
    				//Socket clientSocket = serverSocket.accept();
    				System.out.println("Client Connected");
    				PrintStream out = new PrintStream(clientSocket.getOutputStream());
    				BufferedReader in = new BufferedReader(
    						new InputStreamReader(clientSocket.getInputStream()));
    				
    		        game_logic game = new game_logic(in, out);
    		        game.run();
    		        
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
