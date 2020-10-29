package org.imec.ivlab.core.exceptions;

public class ExternalConnectionException extends Exception {
    private static final long serialVersionUID = 1L;


    public ExternalConnectionException(String message) {
        super(message);
    }

    public ExternalConnectionException(String message, Throwable e) {
        super(message, e);
    }

    public ExternalConnectionException(Throwable e) {
        super(e);
    }

}
