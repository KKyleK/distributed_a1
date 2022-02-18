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

/**
 * Implementation of the game client.
 */
public class Client {
    private Socket clientSocket;
    BufferedReader in;
    PrintStream out;

    /**
     * Default constructor.
     * @param host The ip
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
     * Sends an input line off to the server.
     * @param request The string to send.
     */
    void writeRequest(String request) {
            // Create output streams & write the request to the server
            out.println(request);
    }

    /**
     * Checks to see if there is a message from the server and prints it out, unless it is an ENQ.
     * @return Returns false if an ENQ signal is recieved from the server.
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
        
        client = new Client("localhost", 5599);
        
        // Starting message
        System.out.print(
                  "\n---------------------------------------------------------------------------\n"
                + "|                 ???        Guess the Phrase!        ???                 |\n"
                + "---------------------------------------------------------------------------\n"
                + "| Main menu commands:                                                     |\n"
                + "| Start <i> <f> - Starts a game with i words to guess.                    |\n"
                + "|                 You will have i x f attempts to guess the phrase!       |\n"
                + "| add <word>    - Add a word to the list of possible words.               |\n"
                + "| remove <word> - Remove a word from the list of possible words.          |\n"
                + "| check <word>  - Check if a word exists in the list of possible words.   |\n"
                + "| Type QUIT to exit the game.                                             |\n"
                + "|-------------------------------------------------------------------------|\n"
                + "| In game controls:                                                       |\n"
                + "| Enter \".\" at any time to forfeit.                                       |\n"
                + "| Enter \"*\" at any time to to start a new game                            |\n"
                + "| Enter ?<word> to check if a word exists                                 |\n"
                + "---------------------------------------------------------------------------\n\n");
         
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
        client.writeRequest(request);   
        System.out.println("\nThanks for playing!\n");
    }
}
