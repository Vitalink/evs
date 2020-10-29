package org.imec.ivlab.validator.exceptions;

public class ValidatorXsdException extends Exception {
    private static final long serialVersionUID = 1L;


    public ValidatorXsdException(String message) {
        super(message);
    }

    public ValidatorXsdException(String message, Throwable e) {
        super(message, e);
    }

}
