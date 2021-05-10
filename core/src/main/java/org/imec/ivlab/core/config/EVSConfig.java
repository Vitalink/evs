package org.imec.ivlab.core.config;

import java.util.Properties;


public class EVSConfig {

    private static EVSConfig instance;
    private Properties properties = new Properties();

    public static EVSConfig getInstance() {
        if (instance == null) {
            instance = new EVSConfig();
        }
        return instance;
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public String getProperty(String key) {
        if (isPropertyDefined(key)) {
            return properties.getProperty(key);
        }
        throw new RuntimeException("Property with key " + key + " was not found");
    }

    public String getPropertyOrNull(String key) {
        if (isPropertyDefined(key)) {
            return properties.getProperty(key);
        } else {
            return null;
        }
    }

    public boolean isPropertyDefined(String key) {
        return properties.containsKey(key);
    }

}
