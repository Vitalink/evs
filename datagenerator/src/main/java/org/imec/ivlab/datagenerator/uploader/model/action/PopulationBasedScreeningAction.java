package org.imec.ivlab.datagenerator.uploader.model.action;

public enum PopulationBasedScreeningAction implements Action {

    EXPORT("export");

    private String value;

    PopulationBasedScreeningAction(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static PopulationBasedScreeningAction fromValue(String input) {
        for (PopulationBasedScreeningAction action : values()) {
            if (action.value.equalsIgnoreCase(input)) {
                return action;
            }
        }

        throw new RuntimeException("Action is not valid: " + input);
    }

    public static String[] getAllValues() {
        String[] values = new String[PopulationBasedScreeningAction.values().length];
        for (int i = 0; i < PopulationBasedScreeningAction.values().length; i++) {
            values[i] = PopulationBasedScreeningAction.values()[i].getValue();
        }
        return values;
    }

    @Override
    public String getName() {
        return this.name();
    }

}
