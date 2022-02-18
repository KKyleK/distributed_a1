package distributed_a1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

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
	public void handleClient() {
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				PrintStream out = new PrintStream(clientSocket.getOutputStream());
				BufferedReader in = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));
				String inputLine = in.readLine();
				String outputLine = serverLogic(inputLine);
				while (inputLine != null) {
					out.println(outputLine); // Instantly sends it. Can I pipe system.out into this?
					inputLine = in.readLine();
					outputLine = serverLogic(inputLine);
				}
				clientSocket.close();
			} catch (SocketException e) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public String serverLogic(String input) {

		game_logic game = new game_logic(); // This is number of fails
		game.run();
		// Presumably where we handle all input from the client.
		// For now, returns the input string.

		return input;
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println(BAD_ARG);
			System.exit(1);
		}
		int port = 0;
		Server server = null;

		try {
			port = Integer.parseInt(args[0]);
			server = new Server(port);
		} catch (IOException e) {
			System.out.println("Unable to establish server on given port\n");
		}
		server.handleClient();
	}

}
