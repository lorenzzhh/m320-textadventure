package ch.axa;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.util.List;
import java.util.Map;

public class StoryReader {

    public record Game(String startroom, String description,
                       @JsonAnySetter Map<String, Room> rooms) {}


    public record Room(String name, String description,
                       @JsonAnySetter Map<String, Verb> verbs) {}

    public record Verb(Noun noun) {}

    public record Noun(String message, Room room) {
        public Noun(String message) {
            this(message, null);
        }

        public Noun(Room room) {
            this(null, room);
        }
    }


    public static void main(String[] args) {
        StoryReader storyReader = new StoryReader();
        storyReader.readYaml();
    }

    public void readYaml() {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();

            // Hier geben wir an, dass wir eine Liste von Room-Objekten erwarten
            Game game = mapper.readValue(new File("src/main/resources/tutorial.yaml"), Game.class);

            game.rooms.forEach((key, value) -> {
                System.out.println(value.name);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
