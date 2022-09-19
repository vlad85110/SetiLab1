package app.parser;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class ConfigParser {
    private final HashMap<String, String> config;

    public ConfigParser() throws IOException {
        Properties properties = new Properties();
        FileReader reader;

        reader = new FileReader("src/main/resources/configs/mainConfig.properties");
        properties.load(reader);

        config = new HashMap<>();
        for (var i : properties.stringPropertyNames()) {
            config.put(i, properties.getProperty(i));
        }
    }

    public HashMap<String, String> getConfig() {
        return config;
    }
}
