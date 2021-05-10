package org.imec.ivlab.core.authentication.model;

public class AuthenticationConfig {

    private String name;
    private ConfigDetails configDetails;

    public AuthenticationConfig(String name, ConfigDetails configDetails) {
        this.name = name;
        this.configDetails = configDetails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ConfigDetails getConfigDetails() {
        return configDetails;
    }

    public void setConfigDetails(ConfigDetails configDetails) {
        this.configDetails = configDetails;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "name='" + name + '\'' +
                ", configDetails=" + configDetails +
                '}';
    }
}
