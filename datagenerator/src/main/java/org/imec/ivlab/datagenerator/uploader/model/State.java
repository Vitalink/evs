package org.imec.ivlab.datagenerator.uploader.model;

public enum State {

    INPUT("input"),
    PROCESSED("processed");

    private String value;

    State(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static State fromValue(String input) {
        for (State state : values()) {
            if (state.value.equalsIgnoreCase(input)) {
                return state;
            }
        }

        throw new RuntimeException("State is not valid: " + input);
    }

}
