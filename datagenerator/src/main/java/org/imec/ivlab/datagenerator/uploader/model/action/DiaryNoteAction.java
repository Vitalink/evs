package org.imec.ivlab.datagenerator.uploader.model.action;

public enum DiaryNoteAction implements Action {

    ADD("add"),
    GENERATE_REF("generateREF"),
    UPDATE_REF("updateREF"),
    EXPORT("export");

    private String value;

    DiaryNoteAction(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static DiaryNoteAction fromValue(String input) {
        for (DiaryNoteAction action : values()) {
            if (action.value.equalsIgnoreCase(input)) {
                return action;
            }
        }

        throw new RuntimeException("Action is not valid: " + input);
    }

    public static String[] getAllValues() {
        String[] values = new String[DiaryNoteAction.values().length];
        for (int i = 0; i < DiaryNoteAction.values().length; i++) {
            values[i] = DiaryNoteAction.values()[i].getValue();
        }
        return values;
    }

    @Override
    public String getName() {
        return this.name();
    }

}
