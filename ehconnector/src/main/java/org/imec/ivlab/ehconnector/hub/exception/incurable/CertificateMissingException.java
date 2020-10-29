package org.imec.ivlab.ehconnector.hub.exception.incurable;

import org.imec.ivlab.core.exceptions.VitalinkException;

public class CertificateMissingException extends VitalinkException implements Incurable {
    private static final long serialVersionUID = 1L;


    public CertificateMissingException(String message) {
        super(message);
    }

    public CertificateMissingException(String message, Throwable e) {
        super(message, e);
    }

}
