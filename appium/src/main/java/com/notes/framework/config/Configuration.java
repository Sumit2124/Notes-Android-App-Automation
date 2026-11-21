package com.notes.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private final Properties properties = new Properties();

    public Configuration() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("config.properties was not found on the classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load config.properties", e);
        }
    }

    public String get(String key) {
        return System.getProperty(key, properties.getProperty(key, "")).trim();
    }

    public String required(String key) {
        String value = get(key);
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Missing configuration: " + key);
        }
        return value;
    }

    public Properties getProperties() {
        Properties copy = new Properties();
        copy.putAll(properties);
        return copy;
    }
}
