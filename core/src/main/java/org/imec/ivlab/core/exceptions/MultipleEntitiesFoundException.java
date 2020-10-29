package org.imec.ivlab.core.exceptions;

public class MultipleEntitiesFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public MultipleEntitiesFoundException(String message) {
        super(message);
    }

    public MultipleEntitiesFoundException(String message, Throwable e) {
        super(message, e);
    }

}
