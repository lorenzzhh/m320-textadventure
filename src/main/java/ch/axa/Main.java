package ch.axa;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        main.startGame();
    }

    public void startGame(){
        Console console = new Console();

        StoryReader storyReader = new StoryReader();
        StoryReader.Game game = storyReader.readYaml();

        StoryTeller storyTeller = new StoryTeller(console, game);

        storyTeller.start();

        console.close();
    }
}

