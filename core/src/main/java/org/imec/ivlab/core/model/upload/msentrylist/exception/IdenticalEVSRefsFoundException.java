package org.imec.ivlab.core.model.upload.msentrylist.exception;

public class IdenticalEVSRefsFoundException extends Exception {
    private static final long serialVersionUID = 1L;


    public IdenticalEVSRefsFoundException(String message) {
        super(message);
    }

    public IdenticalEVSRefsFoundException(String message, Throwable e) {
        super(message, e);
    }

}
