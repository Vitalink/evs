package org.imec.ivlab.core.model.upload.msentrylist.exception;

public class MissingEVSRefException extends Exception {
    private static final long serialVersionUID = 1L;


    public MissingEVSRefException(String message) {
        super(message);
    }

    public MissingEVSRefException(String message, Throwable e) {
        super(message, e);
    }

}
