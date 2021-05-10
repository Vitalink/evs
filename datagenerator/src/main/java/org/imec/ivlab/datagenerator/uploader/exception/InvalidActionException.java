package org.imec.ivlab.datagenerator.uploader.exception;

public class InvalidActionException extends Exception {

    public InvalidActionException(String message) {
        super(message);
    }

    public InvalidActionException(String message, Throwable e) {
        super(message, e);
    }

    public InvalidActionException(Throwable e) {
        super(e);
    }

}
