package island.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ConfigLoader {
    public static GlobalConfig loadGlobalConfig(String filename) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(filename), GlobalConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot load config file!");
        }
    }
}
