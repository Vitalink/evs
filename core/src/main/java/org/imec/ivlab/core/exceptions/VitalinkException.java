package org.imec.ivlab.core.exceptions;

public class VitalinkException extends Exception {

    public VitalinkException(String message) {
        super(message);
    }

    public VitalinkException(String message, Throwable e) {
        super(message, e);
    }

    public VitalinkException(Throwable e) {
        super(e);
    }

}
