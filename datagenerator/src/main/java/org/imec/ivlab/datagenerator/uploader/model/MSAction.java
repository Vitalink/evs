package org.imec.ivlab.datagenerator.uploader.model;

public enum MSAction implements Action{

    ADD("add"),
    REPLACE("replace"),
    GENERATE_REF("generateREF"),
    REMOVE_REF("removeREF"),
    EXPORT("export"),
    UPDATE_SCHEME_REF("updateschemeREF");

    private String value;

    MSAction(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static MSAction fromValue(String input) {
        for (MSAction action : values()) {
            if (action.value.equalsIgnoreCase(input)) {
                return action;
            }
        }

        throw new RuntimeException("Action is not valid: " + input);
    }



    public static String[] getAllValues() {
        String[] values = new String[MSAction.values().length];
        for (int i = 0; i < MSAction.values().length; i++) {
            values[i] = MSAction.values()[i].getValue();
        }
        return values;
    }

    @Override
    public String getName() {
        return this.name();
    }


}
