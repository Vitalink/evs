package org.imec.ivlab.core.model.patient.model;

public enum Gender {
    MALE("male"), FEMALE("female");

    private String value;

    Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Gender fromValue(String input) {
        for (Gender gender : values()) {
            if (gender.value.equalsIgnoreCase(input)) {
                return gender;
            }
        }

        throw new RuntimeException("Gender is not valid: " + input);
    }


}
