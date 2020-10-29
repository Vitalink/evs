package org.imec.ivlab.ehconnector.hub.exception.incurable;

import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;

public class NoNodeFoundMatchingTheURI extends GatewaySpecificErrorException implements Incurable {

    public static final String errorCode = "406";

    public NoNodeFoundMatchingTheURI(String message) {
        super(errorCode, message);
    }

    public NoNodeFoundMatchingTheURI(String message, Throwable e) {
        super(errorCode, message, e);
    }

    public NoNodeFoundMatchingTheURI(Throwable e) {
        super(e);
    }

}
