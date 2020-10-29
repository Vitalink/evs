package org.imec.ivlab.ehconnector.hub.exception.incurable;

import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;

public class InvalidNumberOfDataEntriesAvailableWithinRequestException extends GatewaySpecificErrorException implements Incurable {

    public static final String errorCode = "415";

    public InvalidNumberOfDataEntriesAvailableWithinRequestException(String message) {
        super(errorCode, message);
    }

    public InvalidNumberOfDataEntriesAvailableWithinRequestException(String message, Throwable e) {
        super(errorCode, message, e);
    }

    public InvalidNumberOfDataEntriesAvailableWithinRequestException(Throwable e) {
        super(e);
    }

}
