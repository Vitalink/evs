package org.imec.ivlab.datagenerator.uploader.exception;

public class CallbackException extends Exception {

    public CallbackException(String message) {
        super(message);
    }

    public CallbackException(String message, Throwable e) {
        super(message, e);
    }

    public CallbackException(Throwable e) {
        super(e);
    }

}
