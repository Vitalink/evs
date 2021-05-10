package org.imec.ivlab.core.kmehr.tables.exception;

public class CsvParseException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public CsvParseException(String message) {
        super(message);
    }

    public CsvParseException(String message, Throwable e) {
        super(message, e);
    }

}
