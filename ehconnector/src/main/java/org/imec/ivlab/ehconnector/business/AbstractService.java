package org.imec.ivlab.ehconnector.business;

import org.imec.ivlab.core.authentication.model.AuthenticationConfig;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.ehconnector.hub.session.SessionManager;

public class AbstractService {

    public void authenticate(AuthenticationConfig authenticationConfig) throws VitalinkException {
        try {
            SessionManager.connectWith(authenticationConfig);
        } catch (Exception e) {
            throw new VitalinkException(e);
        }
    }


}
