package org.imec.ivlab.core.model.upload;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

@AllArgsConstructor
@Getter
public enum TransactionType {

    MEDICATION_SCHEME("medicationscheme", "medicationscheme", "m"),
    VACCINATION("vaccination", "vaccination", "v"),
    SUMEHR("sumehr", "sumehr", "s"),
    POPULATION_BASED_SCREENING("population-based-screening","populationbasedscreeningscheme", "p"),
    CHILD_PREVENTION("child-prevention","childrecord", "c"),
    DIARY_NOTE("diarynote", "diarynote", "d");

    private String transactionTypeValueForGetLatestUpdate;
    private String transactionTypeValueForGetTransactionList;
    private String oneLetterSummary;

    public static TransactionType fromGetTransactionListValue(String input) {
        for (TransactionType transactionType : values()) {
            if (transactionType.transactionTypeValueForGetTransactionList.equalsIgnoreCase(input)) {
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

    public static String[] getAllLetters() {
        String[] letters = new String[TransactionType.values().length];
        for (int i = 0; i < TransactionType.values().length; i++) {
            letters[i] = TransactionType.values()[i].getOneLetterSummary() + " (for " + TransactionType.values()[i].name() + ")";
        }
        return letters;
    }
}
