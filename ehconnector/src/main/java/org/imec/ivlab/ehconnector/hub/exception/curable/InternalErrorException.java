package org.imec.ivlab.ehconnector.hub.exception.curable;

import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;

public class InternalErrorException extends GatewaySpecificErrorException implements Curable {

    public static final String errorCode = "500";

    public InternalErrorException(String message) {
        super(errorCode, message);
    }

    public InternalErrorException(String message, Throwable e) {
        super(errorCode, message, e);
    }

    public InternalErrorException(Throwable e) {
        super(e);
    }

}
