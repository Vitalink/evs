package org.imec.ivlab.core.exceptions;

public class DataNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException(String message, Throwable e) {
        super(message, e);
    }

}
