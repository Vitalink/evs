package org.imec.ivlab.ehconnector.hub.exception.curable;

import org.imec.ivlab.core.exceptions.VitalinkException;

public class SocketTimeoutException extends VitalinkException implements Curable {

    public SocketTimeoutException(String message) {
        super(message);
    }

    public SocketTimeoutException(String message, Throwable e) {
        super(message, e);
    }

    public SocketTimeoutException(Throwable e) {
        super(e);
    }

}
