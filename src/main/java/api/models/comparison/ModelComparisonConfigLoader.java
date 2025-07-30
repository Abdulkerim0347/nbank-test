package api.models.comparison;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ModelComparisonConfigLoader {

    private final Map<String, ComparisonRule> rules = new HashMap<>();

    public ModelComparisonConfigLoader(String configFile) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                throw new IllegalArgumentException("Config file not found: " + configFile);
            }
            Properties props = new Properties();
            props.load(input);
            for (String key : props.stringPropertyNames()) {
                String[] target = props.getProperty(key).split(":");
                if (target.length != 2) continue;

                String responseClassName = target[0].trim();
                List<String> fields = Arrays.asList(target[1].split(","));

                rules.put(key.trim(), new ComparisonRule(responseClassName, fields));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DTO comparison config", e);
        }
    }

    public ComparisonRule getRuleFor(Class<?> requestClass) {
        return rules.get(requestClass.getSimpleName());
    }

    public static class ComparisonRule {
        private final String responseClassSimpleName;
        private final Map<String, String> fieldMappings;

        public ComparisonRule(String responseClassSimpleName, List<String> fieldPairs) {
            this.responseClassSimpleName = responseClassSimpleName;
            this.fieldMappings = new HashMap<>();

            for (String pair : fieldPairs) {
                String[] parts = pair.split("=");
                if (parts.length == 2) {
                    fieldMappings.put(parts[0].trim(), parts[1].trim());
                } else {
                    // fallback: same field name if mapping not explicitly given
                    fieldMappings.put(pair.trim(), pair.trim());
                }
            }
        }

        public String getResponseClassSimpleName() {
            return responseClassSimpleName;
        }

        public Map<String, String> getFieldMappings() {
            return fieldMappings;
        }
    }

}