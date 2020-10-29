package org.imec.ivlab.datagenerator.uploader.exception;

public class UploaderException extends Exception {

    public UploaderException(String message) {
        super(message);
    }

    public UploaderException(String message, Throwable e) {
        super(message, e);
    }

    public UploaderException(Throwable e) {
        super(e);
    }

}
