package org.imec.ivlab.ehconnector.hub.exception.incurable;

import org.imec.ivlab.core.exceptions.VitalinkException;

public class InvalidConfigurationException extends VitalinkException implements Incurable {
    private static final long serialVersionUID = 1L;


    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(String message, Throwable e) {
        super(message, e);
    }

}
