package org.imec.ivlab.ehconnector.hub.exception.incurable;

import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;

public class NoConsentGivenException extends GatewaySpecificErrorException implements Incurable {

    public static final String errorCode = "365";

    public NoConsentGivenException(String message) {
        super(errorCode, message);
    }

    public NoConsentGivenException(String message, Throwable e) {
        super(errorCode, message, e);
    }

    public NoConsentGivenException(Throwable e) {
        super(e);
    }

}
