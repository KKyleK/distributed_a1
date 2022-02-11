import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;

public class game_logic {

    private ArrayList<String> words;
    private ArrayList<String> words_to_guess;
    private ArrayList<Character> letters_guessed;

    private int fails; // Number of fails allowed
    private int current_fails = 0;

    private boolean guessed = false;

    private int num_words_to_guess;

    public game_logic() {

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
                    System.out.print(current_word.charAt(j));
                } else {
                    System.out.print("_");
                }
                System.out.print(" ");
            }
            System.out.print("   ");
            num_printed += current_word.length() + 3; // For the blank space
            if (num_printed >= 80) {
                System.out.print('\n');
                num_printed = 0;
            }
        }
        return;
    }

    // Core logic loop.
    public void prompt() {

        Scanner reader = new Scanner(System.in);

        while (!guessed && current_fails < fails) {

            System.out.print("\nGuess a letter or the phrase: ");
            String input = reader.nextLine(); // This waits

            if (input.length() > 1) { // Guess was a string!
                if (guess_string(input)) {
                    guessed = true;
                    System.out.println("Congratulations! You guessed the phrase!");
                } else {
                    System.out.println("That is not the phrase.");
                    current_fails++;
                }
            }
            // Guess was a character
            else {
                if (letters_guessed.contains(input.charAt(0))) {
                    System.out.println("You already guessed that letter, guess again:");
                } else {

                    letters_guessed.add(input.charAt(0));
                    if (guess_letter(input.charAt(0))) {
                        System.out.println("Correct!");
                    } else {
                        System.out.println("Incorrect!");
                        current_fails++;
                    }
                }
            }
            if (!guessed) { // Stops it from printing when you win.
                print_current_words();
            }
        }
        if (current_fails >= fails) {
            System.out.println("You lose!");
        }

        reader.close();

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

        Scanner reader = new Scanner(System.in);

        if (words.isEmpty()) { // Might already have been read in.
            try {
                read_file("words.txt");
            } catch (FileNotFoundException e) {
                System.out.println("Could not find input file words.txt");
                System.exit(0);
            }
        }

        int difficulty; // Number of words to pick
        boolean success; // If there were enough words of the length requested.
        try {
            do {
                System.out.println("Enter how long you want the words to be: ");
                difficulty = reader.nextInt();

                System.out.println("How many words would you like to guess?");
                num_words_to_guess = reader.nextInt();

                success = pick_words(difficulty);
                if (!success) {
                    System.out.println("There are not enough words of that length.");
                }

            } while (!success);

            System.out.println("How many lives would you like?");
            fails = reader.nextInt();

        } catch (InputMismatchException e) {
            System.out.println("Input is not a number");
            reader.close();
            return;
        }

        // Begin the game
        System.out.println("Phrase to guess: ");
        print_current_words();
        prompt(); // This holds the game logic
        reader.close();

        return;
    }

    public static void main(String[] args) {

        game_logic game = new game_logic(); // This is number of fails
        game.run();
    }

}
