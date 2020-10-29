package org.imec.ivlab.datagenerator.uploader.exception;

public class ScannerException extends Exception {

    public ScannerException(String message) {
        super(message);
    }

    public ScannerException(String message, Throwable e) {
        super(message, e);
    }

    public ScannerException(Throwable e) {
        super(e);
    }

}
