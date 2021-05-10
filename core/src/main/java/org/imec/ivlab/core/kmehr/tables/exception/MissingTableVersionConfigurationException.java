package org.imec.ivlab.core.kmehr.tables.exception;

public class MissingTableVersionConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public MissingTableVersionConfigurationException(String message) {
        super(message);
    }

    public MissingTableVersionConfigurationException(String message, Throwable e) {
        super(message, e);
    }

}
