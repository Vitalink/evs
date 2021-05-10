package org.imec.ivlab.ehconnector.hub.exception.incurable;

import org.imec.ivlab.core.exceptions.VitalinkException;

public class TransactionNotFoundException extends VitalinkException implements Incurable {
    private static final long serialVersionUID = 1L;


    public TransactionNotFoundException(String message) {
        super(message);
    }

    public TransactionNotFoundException(String message, Throwable e) {
        super(message, e);
    }

}
