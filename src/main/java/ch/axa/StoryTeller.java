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
    private int countSteps = 0;

    public StoryTeller(Console console, StoryReader.Game game) {
        this.console = console;
        this.game = game;
        this.currentRoom = game.startRoom();
        this.states = new HashSet<>();
    }

    public void start() {
        console.writeLine("Welcome to the tutorial...");

        while (true) {
            StoryReader.Game.Room room = game.rooms().get(currentRoom);
            console.writeLine("You are in " + room.name() + ": " + room.description());


            if (room.name().equals("A hidden room")) {
                console.writeLine(ANSI_GREEN + "You Made It in " + countSteps + " steps." + ANSI_RESET);
                console.writeLine(ANSI_GREEN + "Type 'quit' to exit or 'restart' for a new Game!" + ANSI_RESET);
            }

            console.writeLine("What would you like to do?");
            String input = console.readLine().toLowerCase();

            countSteps++;

            if (input.equals("quit") || input.equals("exit")) {
                console.writeLine("Thank you for playing! Good bye!");
                System.exit(0);
            }

            if (input.equals("restart")) {
                Main main = new Main();
                main.startGame();
            }

            String[] parts = input.split(" ");

            String verbKey = findVerb(parts[0]);
            if (verbKey == null) {
                console.writeLine(ANSI_RED + "Unknown command: " + parts[0] + ANSI_RESET);
                continue;
            }

            String object = null;
            if (parts.length > 1) {
                object = parts[1];
            }

            Map<String, List<StoryReader.Game.Action>> actionsForVerb = room.verbs().get(verbKey);
            if (actionsForVerb == null) {
                handleUnknownCommand(verbKey);
                continue;
            }

            List<StoryReader.Game.Action> actions = actionsForVerb.get(object);
            if (actions == null || actions.isEmpty()) {
                handleUnknownObject(verbKey, object);
                continue;
            }

            StoryReader.Game.Action action = findValidAction(actions);
            if (action != null) {
                processAction(action);
            } else {
                console.writeLine(ANSI_RED + "You can't do that right now." + ANSI_RESET);
            }
        }
    }

    private void processAction(StoryReader.Game.Action action) {
        if (action.message() != null) {
            console.writeLine(action.message());
        }
        if (action.room() != null) {
            currentRoom = action.room();
        }
        if (action.addState() != null) {
            states.add(action.addState());
            console.writeLine(ANSI_GREEN + "State '" + action.addState() + "' has been added." + ANSI_RESET);
        }
    }

    private String findVerb(String inputVerb) {
        if (game.verbs().containsKey(inputVerb)) {
            return inputVerb;
        }
        for (Map.Entry<String, StoryReader.Game.Verb> entry : game.verbs().entrySet()) {
            StoryReader.Game.Verb verb = entry.getValue();
            if (verb.synonyms() != null && verb.synonyms().contains(inputVerb)) {
                return entry.getKey();  // Gib das Hauptverb zurück
            }
        }
        return null;
    }

    private StoryReader.Game.Action findValidAction(List<StoryReader.Game.Action> actions) {
        for (StoryReader.Game.Action action : actions) {
            if (action.ifState() == null || states.contains(action.ifState())) {
                return action;  // Gültige Aktion gefunden
            }
        }
        return null;
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
