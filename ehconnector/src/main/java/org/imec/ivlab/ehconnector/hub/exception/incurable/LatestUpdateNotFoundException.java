package org.imec.ivlab.ehconnector.hub.exception.incurable;

import org.imec.ivlab.core.exceptions.VitalinkException;

public class LatestUpdateNotFoundException extends VitalinkException implements Incurable {
    private static final long serialVersionUID = 1L;


    public LatestUpdateNotFoundException(String message) {
        super(message);
    }

    public LatestUpdateNotFoundException(String message, Throwable e) {
        super(message, e);
    }

}
