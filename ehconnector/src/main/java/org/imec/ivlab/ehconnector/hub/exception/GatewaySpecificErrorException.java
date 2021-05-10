package org.imec.ivlab.ehconnector.hub.exception;

public abstract class GatewaySpecificErrorException extends Exception {

    public GatewaySpecificErrorException(String code, String message) {
        super(message);
    }

    public GatewaySpecificErrorException(String code, String message, Throwable e) {
        super(message, e);
    }

    public GatewaySpecificErrorException(Throwable e) {
        super(e);
    }

}
