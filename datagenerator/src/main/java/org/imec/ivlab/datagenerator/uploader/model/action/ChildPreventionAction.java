package org.imec.ivlab.datagenerator.uploader.model.action;

public enum ChildPreventionAction implements Action {

    EXPORT("export");

    private String value;

    ChildPreventionAction(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static ChildPreventionAction fromValue(String input) {
        for (ChildPreventionAction action : values()) {
            if (action.value.equalsIgnoreCase(input)) {
                return action;
            }
        }

        throw new RuntimeException("Action is not valid: " + input);
    }

    public static String[] getAllValues() {
        String[] values = new String[ChildPreventionAction.values().length];
        for (int i = 0; i < ChildPreventionAction.values().length; i++) {
            values[i] = ChildPreventionAction.values()[i].getValue();
        }
        return values;
    }

    @Override
    public String getName() {
        return this.name();
    }

}
