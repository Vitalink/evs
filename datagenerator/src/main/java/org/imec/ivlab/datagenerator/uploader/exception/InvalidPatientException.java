package org.imec.ivlab.datagenerator.uploader.exception;

public class InvalidPatientException extends Exception {

    public InvalidPatientException(String message) {
        super(message);
    }

    public InvalidPatientException(String message, Throwable e) {
        super(message, e);
    }

    public InvalidPatientException(Throwable e) {
        super(e);
    }

}
