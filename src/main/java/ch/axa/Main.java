package ch.axa;

public class Main {

    public static void main(String[] args) {
        // Initialisiere die Konsole
        Console console = new Console();

        // Lese die YAML-Datei ein
        StoryReader2 storyReader = new StoryReader2();
        StoryReader2.Game game = storyReader.readYaml();

        // Initialisiere den StoryTeller mit der Konsole und dem geladenen Spiel
        StoryTeller storyTeller = new StoryTeller(console, game);

        // Starte das Spiel
        storyTeller.start();

        // Schließe die Konsole nach Spielende
        console.close();
    }
}

