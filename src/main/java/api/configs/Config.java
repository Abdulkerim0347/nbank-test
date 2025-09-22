package api.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Config INSTANCE = new Config();
    private final Properties properties = new Properties();

    private Config() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("'config.properties' not found in resources");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load 'config.properties'");
        }
    }

    public static String getProperty(String key) {
        // ПРИОРИТЕТ 1 - это системное свойство baseApiUrl =..
        String systemValue = System.getProperty(key);

        if (systemValue != null) {
            return systemValue;
        }

        // ПРИОРИТЕТ 2 - это переменная окружения baseApiUrl - BASEAPIURL
        // admin.username -> ADMIN_USERNAME
        String envKey = key.toUpperCase().replace('.', '_');

        String envValue = System.getenv(envKey);
        if (envValue != null) {
            return envValue;
        }

        // ПРИОРИТЕТ 3 - это config.properties
        return INSTANCE.properties.getProperty(key);
    }
}
