package org.imec.ivlab.ehconnector.hub.exception.incurable;

import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;

public class RemoveNotAllowedIfDataEntryAlreadyDeleted extends GatewaySpecificErrorException implements Incurable {

    public static final String errorCode = "464";

    public RemoveNotAllowedIfDataEntryAlreadyDeleted(String message) {
        super(errorCode, message);
    }

    public RemoveNotAllowedIfDataEntryAlreadyDeleted(String message, Throwable e) {
        super(errorCode, message, e);
    }

    public RemoveNotAllowedIfDataEntryAlreadyDeleted(Throwable e) {
        super(e);
    }

}
