package distributed_a1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private static final String USAGE = "java Client [host] [port]";
    private Socket clientSocket;
    BufferedReader in;
    PrintStream out;

    /**
     * Default constructor.
     * @param host
     * @param port
     */
    public Client(String host, int port) {
        try {
            clientSocket = new Socket(host, port);
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintStream(clientSocket.getOutputStream());
        } catch (Exception e) {
        	System.out.println("Unable to connect to server!\n");
            System.exit(1);
        }
    }

    
    /**
     * Sends a entered line off to the server.
     * @param request
     */
    void writeRequest(String request) {
            // Create output streams & write the request to the server
            out.println(request);
    }

    /**
     * Receives a message from the server.
     * @throws IOException 
     */
    private boolean readAndPrintResponse() throws IOException {
    		if(in.ready()) {
	            String line = in.readLine();
	            if (line.equals("\u0005") )
	            	return false;
	            else
	            	System.out.println(line);
    		}
            return true;
    }

    public static void main(String[] args) throws IOException {
        Client client = null;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }
        
        try {
            client = new Client(
                    args[0],
                    Integer.parseInt(args[1]));
            
            System.out.print(
                      "\nCommands:\n"
                    + "Start <i> <f> - Starts a game with f words of max length i.\n"
                    + "                You will have i x f attempts to guess the phrase!\n"
                    + "add <word>    - Add a word to the list of possible words.\n"
                    + "remove <word> - Remove a word from the list of possible words.\n"
                    + "check <word>  - Check if a word exists in the list of possible words.\n\n");
            
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + args[1] + ".");
            System.exit(1);
        }
        
        boolean client_wait = true;
        
        while(client_wait) {
        	client_wait = client.readAndPrintResponse();
        }
        client_wait = true;

        System.out.print(">>> ");
        Scanner input = new Scanner(System.in);
        String request = input.nextLine();
        
        while (!request.equals("QUIT")) {
            try {
                client.writeRequest(request);
                while(client_wait)
                	client_wait = client.readAndPrintResponse();
                client_wait = true;
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number: " + args[1] + ".");
                System.exit(1);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }

            System.out.print(">>> ");
            request = input.nextLine();
        }
    }
}
