package org.imec.ivlab.core.model.upload.msentrylist.exception;

public class NoMatchingEvsRefException extends Exception {

    public NoMatchingEvsRefException(String message) {
        super(message);
    }

    public NoMatchingEvsRefException(String message, Throwable e) {
        super(message, e);
    }

    public NoMatchingEvsRefException(Throwable e) {
        super(e);
    }

}
