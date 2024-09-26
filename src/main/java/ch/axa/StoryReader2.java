package ch.axa;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StoryReader2 {

    record Game(String startRoom, String description,
                Map<String, Room> rooms, Map<String, Verb> verbs) {


        record Room(String name, String description,
                    Map<String, Map<String, List<Action>>> verbs) {
        }

        record Verb(List<String> synonyms, Errors errors) {
            public Verb(Errors errors) {
                this(new ArrayList<String>(), errors);
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        record Action(String room, String message, String ifState, String addState) {
        }

        record Errors(String verb, String object){}

    }


    public static void main(String[] args) {
        StoryReader2 storyReader = new StoryReader2();
        storyReader.readYaml();
    }

    public void readYaml() {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();

            // Hier geben wir an, dass wir eine Liste von Room-Objekten erwarten
            Game game = mapper.readValue(new File("src/main/resources/tutorial.yaml"), Game.class);

            game.rooms.forEach((key, value) -> {
                System.out.println(value);
            });

            game.verbs.forEach((key, value) -> {
                System.out.println(value);
            });

            System.out.println(game.startRoom);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
