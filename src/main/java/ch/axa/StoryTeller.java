package ch.axa;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StoryTeller {

    private final Console console;
    private final StoryReader2.Game game;
    private String currentRoom;
    private final Set<String> states;  // Hier speichern wir den Spielzustand (States)

    public StoryTeller(Console console, StoryReader2.Game game) {
        this.console = console;
        this.game = game;
        this.currentRoom = game.startRoom();
        this.states = new HashSet<>();  // Anfangs ist kein State gesetzt
    }

    public void start() {
        console.writeLine("Welcome to the tutorial...");

        boolean running = true;
        while (running) {
            // Informationen über den aktuellen Raum ausgeben
            StoryReader2.Game.Room room = game.rooms().get(currentRoom);
            console.writeLine("You are in " + room.name() + ": " + room.description());

            // Eingabe des Spielers abfragen
            console.writeLine("What would you like to do?");
            String input = console.readLine().toLowerCase();

            if (input.equals("quit")) {
                System.out.println("Thank you for playing! Good bye!");
                running = false;
            }

            // Eingabe verarbeiten
            String[] parts = input.split(" ");
            if (parts.length < 2) {
                console.writeLine("Please enter a valid command, e.g., 'go east'.");
                continue;
            }

            String verbKey = parts[0];
            String object = parts[1];

            // Überprüfen, ob das Verb im Raum vorhanden ist
            Map<String, List<StoryReader2.Game.Action>> actionsForVerb = room.verbs().get(verbKey);
            if (actionsForVerb == null) {
                handleUnknownCommand(verbKey);
                continue;
            }

            // Überprüfen, ob das Objekt vorhanden ist
            List<StoryReader2.Game.Action> actions = actionsForVerb.get(object);
            if (actions == null || actions.isEmpty()) {
                handleUnknownObject(verbKey, object);
                continue;
            }

            // Führe die erste passende Aktion aus (ggf. mit ifState-Check)
            StoryReader2.Game.Action action = findValidAction(actions);
            if (action != null) {
                processAction(action);
            } else {
                console.writeLine("You can't do that right now.");
            }
        }
    }

    // Methode zur Verarbeitung einer Aktion
    private void processAction(StoryReader2.Game.Action action) {
        if (action.message() != null) {
            console.writeLine(action.message());
        }
        if (action.room() != null) {
            currentRoom = action.room();
        }
        if (action.addState() != null) {
            // Zustand hinzufügen
            states.add(action.addState());
            console.writeLine("State '" + action.addState() + "' has been added.");
        }
    }

    // Methode zur Suche nach einer gültigen Aktion basierend auf dem ifState
    private StoryReader2.Game.Action findValidAction(List<StoryReader2.Game.Action> actions) {
        for (StoryReader2.Game.Action action : actions) {
            if (action.ifState() == null || states.contains(action.ifState())) {
                return action;  // Gültige Aktion gefunden
            }
        }
        return null;  // Keine passende Aktion gefunden
    }

    // Fehlerbehandlung für unbekannte Verben
    private void handleUnknownCommand(String verb) {
        StoryReader2.Game.Verb verbData = game.verbs().get(verb);
        if (verbData != null && verbData.errors() != null) {
            console.writeLine(verbData.errors().verb());
        } else {
            console.writeLine("I don't understand that command.");
        }
    }

    // Fehlerbehandlung für unbekannte Objekte
    private void handleUnknownObject(String verb, String object) {
        StoryReader2.Game.Verb verbData = game.verbs().get(verb);
        if (verbData != null && verbData.errors() != null) {
            console.writeLine(verbData.errors().object());
        } else {
            console.writeLine("You can't do that with " + object + ".");
        }
    }
}
