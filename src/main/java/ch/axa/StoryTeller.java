package ch.axa;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StoryTeller {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    private final Console console;
    private final StoryReader.Game game;
    private String currentRoom;
    private final Set<String> states;

    public StoryTeller(Console console, StoryReader.Game game) {
        this.console = console;
        this.game = game;
        this.currentRoom = game.startRoom();
        this.states = new HashSet<>();
    }

    public void start() {
        console.writeLine("Welcome to the tutorial...");

        boolean running = true;
        while (running) {
            // Informationen über den aktuellen Raum ausgeben
            StoryReader.Game.Room room = game.rooms().get(currentRoom);
            console.writeLine("You are in " + room.name() + ": " + room.description());

            // Eingabe des Spielers abfragen
            console.writeLine("What would you like to do?");
            String input = console.readLine().toLowerCase();

            if (input.equals("quit")) {
                System.out.println("Thank you for playing! Good bye!");
                running = false;
            }


            String[] parts = input.split(" ");

            String verbKey = parts[0];
            String object = null;
            if (parts.length > 1) {
                object = parts[1];
            }

            // Überprüfen, ob das Verb im Raum vorhanden ist
            Map<String, List<StoryReader.Game.Action>> actionsForVerb = room.verbs().get(verbKey);
            if (actionsForVerb == null) {
                handleUnknownCommand(verbKey);
                continue;
            }

            // Überprüfen, ob das Objekt vorhanden ist
            List<StoryReader.Game.Action> actions = actionsForVerb.get(object);
            if (actions == null || actions.isEmpty()) {
                handleUnknownObject(verbKey, object);
                continue;
            }

            // Führe die erste passende Aktion aus (ggf. mit ifState-Check)
            StoryReader.Game.Action action = findValidAction(actions);
            if (action != null) {
                processAction(action);
            } else {
                console.writeLine(ANSI_RED + "You can't do that right now." + ANSI_RESET);
            }
        }
    }

    // Methode zur Verarbeitung einer Aktion
    private void processAction(StoryReader.Game.Action action) {
        if (action.message() != null) {
            console.writeLine(action.message());
        }
        if (action.room() != null) {
            currentRoom = action.room();
        }
        if (action.addState() != null) {
            // Zustand hinzufügen
            states.add(action.addState());
            console.writeLine(ANSI_GREEN + "State '" + action.addState() + "' has been added." + ANSI_RESET);
        }
    }

    // Methode zur Suche nach einer gültigen Aktion basierend auf dem ifState
    private StoryReader.Game.Action findValidAction(List<StoryReader.Game.Action> actions) {
        for (StoryReader.Game.Action action : actions) {
            if (action.ifState() == null || states.contains(action.ifState())) {
                return action;  // Gültige Aktion gefunden
            }
        }
        return null;  // Keine passende Aktion gefunden
    }

    private void handleUnknownCommand(String verb) {
        StoryReader.Game.Verb verbData = game.verbs().get(verb);
        if (verbData != null && verbData.errors() != null) {
            console.writeLine(ANSI_YELLOW + verbData.errors().verb() + ANSI_RESET);
        } else {
            console.writeLine(ANSI_RED + "I don't understand " + verb + "." + ANSI_RESET);
        }
    }

    private void handleUnknownObject(String verb, String object) {
        StoryReader.Game.Verb verbData = game.verbs().get(verb);
        if (verbData != null && verbData.errors() != null) {
            console.writeLine(ANSI_YELLOW + verbData.errors().object() + ANSI_RESET);
        } else {
            console.writeLine(ANSI_RED + "You can't do that with " + object + "." + ANSI_RESET);
        }
    }
}
