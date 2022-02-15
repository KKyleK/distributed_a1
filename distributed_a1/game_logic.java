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

public class game_logic {

    private ArrayList<String> words;
    private ArrayList<String> words_to_guess;
    private ArrayList<Character> letters_guessed;
	private BufferedReader in;
    private PrintStream out;

    private int fails; // Number of fails allowed
    private int current_fails = 0;

    private boolean guessed = false;

    private int num_words_to_guess;

    public game_logic(BufferedReader in, PrintStream out) {

    	this.in = in;
    	this.out = out;
        words = new ArrayList<String>();
        words_to_guess = new ArrayList<String>();
        letters_guessed = new ArrayList<Character>();
    }

    /**
     * 
     * @param file: The file to read from.
     * @return Fills the arraylist words with words from the file.
     */
    public ArrayList<String> read_file(String file) throws FileNotFoundException {

        Scanner input = new Scanner(new File(file));

        while (input.hasNext()) {
            words.add(input.next().toLowerCase()); // No capital letters.
        }
        Collections.shuffle(words);
        return words;
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
     * 
     * @param difficulty: The length of words you want to deal with.
     * @return Fills the arraylist words_to_guess with words of the given length.
     *         Returns true if the arraylist was
     *         filled to difficulty level, false if not.
     */
    public boolean pick_words(int difficulty) {

        ArrayList<String> possible_words = words;
        Collections.shuffle(possible_words);

        int words_choosen = 0;
        int current_word = 0;

        boolean success = false;

        while (words_choosen < num_words_to_guess && current_word < possible_words.size()) {
            if (possible_words.get(current_word).length() == difficulty) {
                words_to_guess.add(possible_words.get(current_word));
                words_choosen++;
            }
            current_word++;
        }

        if (words_choosen == num_words_to_guess) {
            success = true;
        }

        return success;
    }

    // Prints the stuff you have guessed so far or _ for letters not guessed.
    public void print_current_words() {

        String current_word;
        int num_printed = 0; // Limit the print width to 80

        for (int i = 0; i < words_to_guess.size(); i++) {
            current_word = words_to_guess.get(i); // A word in the arrayList

            for (int j = 0; j < current_word.length(); j++) {
                if (letters_guessed.contains(current_word.charAt(j))) {
                    out.print(current_word.charAt(j));
                } else {
                    out.print("_");
                }
                out.print(" ");
            }
            out.print("   ");
            num_printed += current_word.length() + 3; // For the blank space
            if (num_printed >= 80) {
                out.print('\n');
                num_printed = 0;
            }
        }
        return;
    }

    // Core logic loop.
    public void prompt() throws IOException {

        while (!guessed && current_fails < fails) {

            out.println("\nGuess a letter or the phrase: ");
            String input = read_string(); // This waits

            if (input.length() > 1) { // Guess was a string!
                if (guess_string(input)) {
                    guessed = true;
                    out.println("Congratulations! You guessed the phrase!");
                } else {
                    out.println("That is not the phrase.");
                    current_fails++;
                }
            }
            // Guess was a character
            else {
                if (letters_guessed.contains(input.charAt(0))) {
                    out.println("You already guessed that letter, guess again:");
                } else {

                    letters_guessed.add(input.charAt(0));
                    if (guess_letter(input.charAt(0))) {
                        out.println("Correct!");
                    } else {
                        out.println("Incorrect!");
                        current_fails++;
                    }
                }
            }
            if (!guessed) { // Stops it from printing when you win.
                print_current_words();
            }
        }
        if (current_fails >= fails) {
            out.println("You lose!");
        }

        return;
    }

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
    

    public void run() {

        if (words.isEmpty()) { // Might already have been read in.
            try {
                read_file("words.txt");
            } catch (FileNotFoundException e) {
                out.println("Could not find input file words.txt");
                System.exit(0);
            }
        }

        int difficulty; // Number of words to pick
        boolean success; // If there were enough words of the length requested.
        try {
            do {
                out.println("Enter how long you want the words to be: ");
                difficulty = read_int();

                out.println("How many words would you like to guess?");
                num_words_to_guess = read_int();

                success = pick_words(difficulty);
                if (!success) {
                    out.println("There are not enough words of that length.");
                }

            } while (!success);

          out.println("How many lives would you like?");
            fails = read_int();

        } catch (InputMismatchException e) {
            out.println("Input is not a number");
            return;
        } catch (IOException e) {
        	e.printStackTrace();
        	return;
        }

        // Begin the game
	    try {
	        out.println("Phrase to guess: ");
	        print_current_words();
	        prompt(); // This holds the game logic
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	return;
	    }
    }
}
