package org.imec.ivlab.core.exceptions;

public class ResourceException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public ResourceException(String message) {
        super(message);
    }

    public ResourceException(String message, Throwable e) {
        super(message, e);
    }

}
