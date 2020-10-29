package org.imec.ivlab.ehconnector.hub.exception.incurable;

import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;

public class SubjectWithSSINUnknownException extends GatewaySpecificErrorException implements Incurable {

    public static final String errorCode = "400";

    public SubjectWithSSINUnknownException(String message) {
        super(errorCode, message);
    }

    public SubjectWithSSINUnknownException(String message, Throwable e) {
        super(errorCode, message, e);
    }

    public SubjectWithSSINUnknownException(Throwable e) {
        super(e);
    }

}
