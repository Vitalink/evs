package org.imec.ivlab.ehconnector.hub.session;

import org.apache.log4j.Logger;
import org.imec.ivlab.core.authentication.model.Certificate;
import org.imec.ivlab.core.authentication.model.Type;
import org.imec.ivlab.core.exceptions.DataNotFoundException;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.ehconnector.hub.exception.incurable.InvalidConfigurationException;

import java.util.ArrayList;
import java.util.List;

public class CertificateManager {

    private final static Logger log = Logger.getLogger(CertificateManager.class);


    public static Certificate getCertificate(List<Certificate> certificates, Type type) throws InvalidConfigurationException {

        try {
            return getCertificateForType(certificates, type);
        } catch (DataNotFoundException e) {
            if (Type.HOLDEROFKEY.equals(type) || Type.ENCRYPTION.equals(type)) {
                log.info("No certificate defined of type " + type + ". Returning default certificate of type " + Type.IDENTIFICATION);
                return getCertificateForType(certificates, Type.IDENTIFICATION);
            }
            throw e;
        }

    }

    private static Certificate getCertificateForType(List<Certificate> certificates, Type type) throws InvalidConfigurationException {
        List<Certificate> certificateList = new ArrayList<>();
        if (!CollectionsUtil.emptyOrNull(certificates)) {
            for (Certificate certificate : certificates) {
                if (type.equals(certificate.getType())) {
                    certificateList.add(certificate);
                }
            }
        }
        if (certificateList.size() == 0) {
            throw new DataNotFoundException("No certificate defined for certificate type: " + type.getValue());
        }

        if (certificateList.size() > 1) {
            throw new InvalidConfigurationException("Multiple certificate configuration entries found for type: " + type.getValue());
        }

        Certificate certificate = certificateList.get(0);
        return certificate;
    }

}
