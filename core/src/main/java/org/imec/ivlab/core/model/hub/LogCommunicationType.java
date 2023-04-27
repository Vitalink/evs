package org.imec.ivlab.core.model.hub;

public enum LogCommunicationType {

    WITHOUT_SECURITY, WITH_SECURITY, ALL;

    public static LogCommunicationType fromValue(String input) {
        for (LogCommunicationType logCommunicationType : values()) {
            if (logCommunicationType.name().equalsIgnoreCase(input)) {
                return logCommunicationType;
            }
        }

        throw new IllegalArgumentException("Invalid LogCommunicationType value: " + input);

    }

}
