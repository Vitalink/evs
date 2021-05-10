package org.imec.ivlab.datagenerator.uploader.model;

public enum VaccinationAction implements Action {

    EXPORT("export");

    private String value;

    VaccinationAction(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static VaccinationAction fromValue(String input) {
        for (VaccinationAction action : values()) {
            if (action.value.equalsIgnoreCase(input)) {
                return action;
            }
        }

        throw new RuntimeException("Action is not valid: " + input);
    }

    public static String[] getAllValues() {
        String[] values = new String[VaccinationAction.values().length];
        for (int i = 0; i < VaccinationAction.values().length; i++) {
            values[i] = VaccinationAction.values()[i].getValue();
        }
        return values;
    }

    @Override
    public String getName() {
        return this.name();
    }

}
