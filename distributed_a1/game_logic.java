package distributed_a1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;






import java.net.*;


public class game_logic {

	private DatagramSocket socket;
    private ArrayList<String> words_to_guess;
    private ArrayList<Character> letters_guessed;
	private BufferedReader in;
    private PrintStream out;

    private int fails; // Number of fails allowed
    private int current_fails = 0;

    private boolean guessed = false;


    public game_logic(BufferedReader in, PrintStream out, ArrayList<String> words, int attempts, DatagramSocket socket) {

    	this.in = in;
    	this.out = out;
    	
    	words_to_guess = words;
    	fails = attempts;
        letters_guessed = new ArrayList<Character>();
        this.socket = socket;
    }

    
    public void check_if_word(String word) {
    
    	String input = "check " + word;
    try {
        byte[] buf = new byte[1000];
        byte[] buf2 = new byte[1000];
        
        buf = input.getBytes();
        InetAddress address = InetAddress.getByName("localhost"); 
        
        
        // send request
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 5550);
        socket.send(packet);
        
        // get response
        packet = new DatagramPacket(buf2, buf2.length);
        socket.receive(packet);
        
        String received = new String(packet.getData(), 0, packet.getLength());
        out.println(received);
        
    }
    catch(Exception e) {
    	return;
    }
	return;
    }
   
     
    
    
    
    
    
    
    /**
     * Sends a signal to the client to wait for input.
     */
    private void request(){
		out.println("\u0005");
    }
    
    /**
     * Reads a number from input.
     * @return
     * @throws IOException 
     */
    private int read_int() throws IOException
    {
    	while(true)
    	{
    		try {
		    	request();
		    	return Integer.parseInt(in.readLine());
		    } catch (NumberFormatException e) {
		        out.println("Input is not a number, try again!");
		    }
    	}
    }
    
    /**
     * Reads a String from input.
     * @return
     * @throws IOException
     */
    private String read_string() throws IOException 
    {
    	request();
    	return in.readLine();
    }


    /**
     * Prints what the player has guessed correctly, or _ for letters that have 
     * not been guessed.
     */
    public void print_current_words() {

        String current_word;
        int num_printed = 0; // Limit the print width to 80

        for (int i = 0; i < words_to_guess.size(); i++) {
            current_word = words_to_guess.get(i);   // A word in the arrayList

            for (int j = 0; j < current_word.length(); j++) {
                if (letters_guessed.contains(current_word.charAt(j))) {
                    out.print(current_word.charAt(j));
                } else {
                    out.print("_");
                }
                out.print(" ");
            }
            
            if(i+1 != words_to_guess.size()) {      //Don't print last 3 spaces.
            out.print("   ");
            }
            
            num_printed += current_word.length() + 3; // For the blank space
            if (num_printed >= 80) {
                out.print('\n');
                num_printed = 0;
            } 
        }
        out.print("C" + current_fails);
        return;
    }

    /**
     * Prompts the user on repeat to guess a letter or the entire phrase.
     * @return: Whether the player successfully guessed every letter or the entire phrase.
     * @throws IOException
     */
    public boolean prompt() throws IOException {

    	boolean success = false;
    	
        while (!guessed && current_fails < fails) {

            out.println("\nGuess a letter or the phrase: ");
            String input = read_string(); // This waits

            if (input.length() > 1) { // Guess was a string!
            	
            	if (input.charAt(0) == '?') { 
            		check_if_word(input.substring(1));
            	}
            	else {
            		
                if (guess_string(input)) {
                    guessed = true;
            
                } else {
                    out.println("That is not the phrase.");
                    current_fails++;
                }
            }
            }
           
            else {   // Guess was a character
            	
            	if(input.charAt(0) == '.') {  //Forfeit.
            		return false; 
            	}
            	
                if (letters_guessed.contains(input.charAt(0))) {
                    out.println("You already guessed that letter, guess again:");
                } else {

                    letters_guessed.add(input.charAt(0));
                    if (guess_letter(input.charAt(0))) {
                    	
                    	
                    	if(all_letters_guessed()) {
                    		guessed = true;
                    		success = true;
                    	}
                    	
                    	else {
                    		
                        out.println("Correct!");
                        success = true;
                    	}
                       
                    	
                    } else {
                        out.println("Incorrect!");
                        current_fails++;
                    }
                }
            }
            if (!guessed) { // Stops it from printing when you win.
                print_current_words();
                
            }
            else {
            	 out.println("Congratulations! You guessed the phrase!");
            	 success = true;
            }
        } 
        if (current_fails >= fails) {
            out.println('\n' +"You lose!");
            success = false;
        }

        return success;
    }
    
    /**
     * 
     * @return: Whether all of the letters making up the words have been guessed.
     */
    public boolean all_letters_guessed() {
    	
    	String current = new String();
    	for (int i = 0; i < words_to_guess.size(); i++) {
    		
    		current = words_to_guess.get(i);
    	
    		for (int j = 0; j< letters_guessed.size(); j++) {
    			
    			if(current.contains(String.valueOf(letters_guessed.get(j)))){  
    				
    				current = current.replaceAll(String.valueOf(letters_guessed.get(j)), "");
    			}
    		}
    			if(!current.equals("")) {
    				return false;
    		}
    	}
   
    	return true;
    }
    
    /**
     * 
     * @param input: The complete phrase typed out.
     * @return: Whether or not the phrase guessed is the actual hidden phrase.
     */
    public boolean guess_string(String input) {

        ArrayList<String> answer = new ArrayList<String>(Arrays.asList(input.split(" ")));

        if (words_to_guess.size() != answer.size()) {
            return false;
        }

        for (int i = 0; i < words_to_guess.size(); i++) {
            if (!answer.get(i).contentEquals(words_to_guess.get(i))) {
                return false;
            }
        }
        return true;
    }
/**
 * 
 * @param input: The letter guessed
 * @return: Whether or not the letter was in at least one of the words.
 */
    public boolean guess_letter(char input) {

        letters_guessed.add(input);
        boolean in_phrase = false;
        String current_word = new String();

        for (int i = 0; i < words_to_guess.size(); i++) {

            current_word = words_to_guess.get(i);

            for (int j = 0; j < current_word.length(); j++) {
                if (current_word.charAt(j) == input) {
                    in_phrase = true;
                }
            }
        }

        return in_phrase;
    }
     
/**
 * 
 * @return If the player won or not.
 */
    public boolean run() {
    	boolean success = false;
	    try {
	        out.println("Phrase to guess: ");
	        print_current_words();
	        success = prompt(); 
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	return false;
	    }
	    return success;
    }
}