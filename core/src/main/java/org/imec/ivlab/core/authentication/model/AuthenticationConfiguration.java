package org.imec.ivlab.core.authentication.model;

public class AuthenticationConfiguration {

    private String name;
    private ConfigDetails authenticationConfiguration;

    public AuthenticationConfiguration(String name, ConfigDetails authenticationConfiguration) {
        this.name = name;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ConfigDetails getAuthenticationConfiguration() {
        return authenticationConfiguration;
    }

    public void setAuthenticationConfiguration(ConfigDetails authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "name='" + name + '\'' +
                ", authenticationConfiguration=" + authenticationConfiguration +
                '}';
    }
}
