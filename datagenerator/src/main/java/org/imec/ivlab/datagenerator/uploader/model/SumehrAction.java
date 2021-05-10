package org.imec.ivlab.datagenerator.uploader.model;

public enum SumehrAction implements Action {

    ADD("add"),
    EMPTY("empty"),
    REPLACE("replace"),
    GENERATE_REF("generateREF"),
    REMOVE_REF("removeREF"),
    UPDATE_REF("updateREF"),
    EXPORT("export");

    private String value;

    SumehrAction(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static SumehrAction fromValue(String input) {
        for (SumehrAction action : values()) {
            if (action.value.equalsIgnoreCase(input)) {
                return action;
            }
        }

        throw new RuntimeException("Action is not valid: " + input);
    }

    public static String[] getAllValues() {
        String[] values = new String[SumehrAction.values().length];
        for (int i = 0; i < SumehrAction.values().length; i++) {
            values[i] = SumehrAction.values()[i].getValue();
        }
        return values;
    }

    @Override
    public String getName() {
        return this.name();
    }

}
