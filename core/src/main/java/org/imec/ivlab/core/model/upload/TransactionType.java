package org.imec.ivlab.core.model.upload;


import org.apache.commons.lang.StringUtils;

public enum TransactionType {

    MEDICATION_SCHEME("medicationscheme", "m"),
    VACCINATION("vaccination", "v"),
    SUMEHR("sumehr", "s"),
    POPULATION_BASED_SCREENING("population-based-screening", "p"),
    CHILD_PREVENTION("child-prevention", "c"),
    DIARY_NOTE("diarynote", "d");

    private String value;
    private String oneLetterSummary;

    TransactionType(String value, String oneLetterName) {
        this.value = value;
        this.oneLetterSummary = oneLetterName;
    }

    public String getValue() {
        return value;
    }

    public static TransactionType fromValue(String input) {
        for (TransactionType transactionType : values()) {
            if (transactionType.value.equalsIgnoreCase(input)) {
                return transactionType;
            }
        }

        throw new RuntimeException("TransactionType is not valid: " + input);
    }

    public static TransactionType fromOneLetterName(String input) {
        for (TransactionType transactionType : values()) {
            if (transactionType.oneLetterSummary != null && StringUtils.equalsIgnoreCase(transactionType.oneLetterSummary, input)) {
                return transactionType;
            }
        }

        throw new RuntimeException("Couldn't find a transaction type for letter: " + input);
    }

    public String getOneLetterSummary() {
        return oneLetterSummary;
    }

    public static String[] getAllValues() {
        String[] values = new String[TransactionType.values().length];
        for (int i = 0; i < TransactionType.values().length; i++) {
            values[i] = TransactionType.values()[i].getValue();
        }
        return values;
    }


    public static String[] getAllLetters() {
        String[] letters = new String[TransactionType.values().length];
        for (int i = 0; i < TransactionType.values().length; i++) {
            letters[i] = TransactionType.values()[i].getOneLetterSummary() + " (for " + TransactionType.values()[i].getValue() + ")";
        }
        return letters;
    }
}
