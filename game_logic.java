public class game_logic{

    private ArrayList<String> words;
    private ArrayList<String> words_to_guess;
    private ArrayList<char> letters_guessed;

    private int fails = 0;
    private boolean guessed = false;

//public game_logic(int fails); Customize number of fails allowed

public ArrayList<String> read_file(String file){

    Scanner input = new Scanner(new File(read_file));
   
    while(input.hasNext()){
        words.add(input.next());
    }
    return words;
}


//Can make this based on number of words too, right now picks three words.
public void pick_words(int difficulty) {
    ArrayList<String> possible_words = new ArrayList<String>();
    for (int i = 0; i < words.size(); i++) {
        if (words.get(i).length() == difficulty) {
            possible_words.add(words.get(i));
        }
    }
    possible_words.shuffle();
    words_to_guess = possible_words.subList(0, 3); //First three words.
    return;
}
//Prints the stuff you have guessed so far.
public void print_current_words(){

    String current_word;

    for (int i = 0; i < words_to_guess.size(); i++){
        current_word = words_to_guess.get(i);
        for (int j = 0; j < current_word.length(); j++){
            if (letters_guessed.contains(current_word.charAt(j))) {
                System.out.print(current_word.charAt(j));
            }
            else {
                System.out.print("_");
            }
        }

    }

//Core logic loop.
public void prompt() {

    while(!guessed || fails < 3) {

        system.out.println("Guess a letter or the phrase :");
        String input = reader.readLine(); //This waits
       
        if(input.size > 1){ 
             if(guess_string(input)){
                 guessed = true;
                 System.out.println("Congradulations! You guessed the phrase!");
             }
             else{
                System.out.println("That is not the phrase.");
                 fails++;
             }
        }

        else{ 
            if(letters_guessed.contains(input)){
                System.out.println("You already guessed that letter, guess again:")
            }
             else {
           if(guess_letter(input)){
                System.out.println("Correct!");
            }
            else {
                System.out.println("Incorrect!");
                fails++;
            }
           }
          }
          print_current_words();
    }
    if(fails >= 3){
        System.out.println("You lose!");
    }

    return;
}


public boolean guess_letter(char input){

    letters_guessed.add(input);
    boolean in_phrase = false;
    String current_word = new String();

    for (int i = 0; i < words_to_guess.size(); i++){

        current_word = words_to_guess.get(i);

        for (int j = 0; j < current_word.length(); j++){
            if (current_word.charAt(j) == input) {
                in_phrase = true;
            }
        }
    }

    return in_phrase;
}

}


public static void main(String[] args) {

 game_logic game = new game_logic();
 game.read_file("words.txt");

 int difficulty = 2; // Number of words to pick
 game.pick_words(difficulty);

 //Begin the game
 system.out.println("Start of game");
 system.out.println("Phrase to guess: " + print_current_words());


game.prompt();  //This holds the game logic

 
}