package org.imec.ivlab.core.model.internal.mapper.medication;

public enum Weekday {

    MONDAY("monday"),
    TUESDAY("tuesday"),
    WEDNESDAY("wednesday"),
    THURSDAY("thursday"),
    FRIDAY("friday"),
    SATURDAY("saturday"),
    SUNDAY("sunday");

    private String value;

    Weekday(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Weekday fromValue(String input) {
        for (Weekday weekday : values()) {
            if (weekday.value.equalsIgnoreCase(input)) {
                return weekday;
            }
        }

        throw new RuntimeException("Weekday is not valid: " + input);
    }

}
