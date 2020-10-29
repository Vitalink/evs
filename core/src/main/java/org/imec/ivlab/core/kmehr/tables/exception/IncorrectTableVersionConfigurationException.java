package org.imec.ivlab.core.kmehr.tables.exception;

public class IncorrectTableVersionConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public IncorrectTableVersionConfigurationException(String message) {
        super(message);
    }

    public IncorrectTableVersionConfigurationException(String message, Throwable e) {
        super(message, e);
    }

}
