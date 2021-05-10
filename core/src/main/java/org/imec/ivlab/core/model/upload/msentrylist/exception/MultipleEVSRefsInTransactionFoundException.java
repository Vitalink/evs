package org.imec.ivlab.core.model.upload.msentrylist.exception;

public class MultipleEVSRefsInTransactionFoundException extends Exception {
    private static final long serialVersionUID = 1L;


    public MultipleEVSRefsInTransactionFoundException(String message) {
        super(message);
    }

    public MultipleEVSRefsInTransactionFoundException(String message, Throwable e) {
        super(message, e);
    }

}
