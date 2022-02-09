public class game_logic{

    private ArrayList<String> words;
    private ArrayList<String> words_to_guess;
    private ArrayList<char> letters_guessed;

//public game_logic(int fails); Customize number of fails allowed

public ArrayList<String> read_file(String file){

    Scanner input = new Scanner(new File(read_file));
   
    while(input.hasNext()){
        words.add(input.next());
    }
    return words;
}

//Called when you guess a letter. (Updates the game)
public void guess(String input){

    return;
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
}


public static void main(String[] args) {

 game_logic game = new game_logic();
 game.read_file("words.txt");

 int difficulty = 2; // Number of words to pick
 game.pick_words(difficulty);

 //Begin the game
 BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
 String input = new String();

 system.out.println("Start of game");

 system.out.println("Guess the phrase: " + print_current_words())

 system.out.println("Guess a letter or a phrase :");
 String input = reader.readLine(); //This waits

 if(input.size > 1){ 
    guess_string(input);
   }
   else{ 
    guess_letter(input);
   }
}