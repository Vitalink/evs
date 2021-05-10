package org.imec.ivlab.ehconnector.hub.exception.incurable;

import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;

public class NoDataIsAvailableForProfileOrCriteria extends GatewaySpecificErrorException implements Incurable {

    public static final String errorCode = "202";

    public NoDataIsAvailableForProfileOrCriteria(String message) {
        super(errorCode, message);
    }

    public NoDataIsAvailableForProfileOrCriteria(String message, Throwable e) {
        super(errorCode, message, e);
    }

    public NoDataIsAvailableForProfileOrCriteria(Throwable e) {
        super(e);
    }

}
