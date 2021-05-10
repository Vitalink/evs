package org.imec.ivlab.core.exceptions;

public class LocalVersionCheckFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public LocalVersionCheckFailedException(String message) {
        super(message);
    }

    public LocalVersionCheckFailedException(String message, Throwable e) {
        super(message, e);
    }

    public LocalVersionCheckFailedException(Throwable e) {
        super(e);
    }

}
