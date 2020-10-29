package org.imec.ivlab.datagenerator.uploader.exception;

public class NoQueueingNeededException extends Exception {

    public NoQueueingNeededException(String message) {
        super(message);
    }

    public NoQueueingNeededException(String message, Throwable e) {
        super(message, e);
    }

    public NoQueueingNeededException(Throwable e) {
        super(e);
    }

}
