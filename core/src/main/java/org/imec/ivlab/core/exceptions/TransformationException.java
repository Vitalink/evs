package org.imec.ivlab.core.exceptions;

public class TransformationException extends Exception {
    private static final long serialVersionUID = 1L;


    public TransformationException(String message) {
        super(message);
    }

    public TransformationException(String message, Throwable e) {
        super(message, e);
    }

}
