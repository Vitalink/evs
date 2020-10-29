package org.imec.ivlab.core.exceptions;

public class RemoteVersionCheckFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public RemoteVersionCheckFailedException(String message) {
        super(message);
    }

    public RemoteVersionCheckFailedException(String message, Throwable e) {
        super(message, e);
    }

    public RemoteVersionCheckFailedException(Throwable e) {
        super(e);
    }

}
