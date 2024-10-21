package ch.axa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

public class StoryReader {

    public record Game(String startRoom, String description,
                Map<String, Room> rooms, Map<String, Verb> verbs) {

        record Room(String name, String description,
                    Map<String, Map<String, List<Action>>> verbs) {
        }

        record Verb(List<String> synonyms, Errors errors) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        record Action(String room, String message, String ifState, String addState) {
        }

        record Errors(String verb, String object) {
        }
    }

    public static void main(String[] args) {
        StoryReader storyReader = new StoryReader();
        storyReader.readYaml();
    }

    public Game readYaml() {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();

            return mapper.readValue(new File("src/main/resources/tutorial.yaml"), Game.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
