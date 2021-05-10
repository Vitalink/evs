package org.imec.ivlab.core.kmehr.modifier.exception;

public class InvalidKmehrException extends Exception {

    public InvalidKmehrException(String message) {
        super(message);
    }

    public InvalidKmehrException(String message, Throwable e) {
        super(message, e);
    }

    public InvalidKmehrException(Throwable e) {
        super(e);
    }

}
