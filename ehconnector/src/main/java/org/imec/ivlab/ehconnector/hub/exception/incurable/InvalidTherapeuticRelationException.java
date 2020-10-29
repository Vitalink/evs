package org.imec.ivlab.ehconnector.hub.exception.incurable;

import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;

public class InvalidTherapeuticRelationException extends GatewaySpecificErrorException implements Incurable {

    public static final String errorCode = "360";

    public InvalidTherapeuticRelationException(String message) {
        super(errorCode, message);
    }

    public InvalidTherapeuticRelationException(String message, Throwable e) {
        super(errorCode, message, e);
    }

    public InvalidTherapeuticRelationException(Throwable e) {
        super(e);
    }

}
