package org.imec.ivlab.datagenerator.uploader.dateshift;

public enum ShiftAction {

    SHIFT_AND_TAG("shift_and_tag"),
    TAG_AND_NO_SHIFT("tag_and_no_shift"),
    NO_TAG_AND_NO_SHIFT("no_tag_and_no_shift");

    private String value;

    ShiftAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ShiftAction fromValue(String input) {
        for (ShiftAction shiftAction : values()) {
            if (shiftAction.value.equalsIgnoreCase(input)) {
                return shiftAction;
            }
        }

        throw new RuntimeException("ShiftAction is not valid: " + input);
    }

}
