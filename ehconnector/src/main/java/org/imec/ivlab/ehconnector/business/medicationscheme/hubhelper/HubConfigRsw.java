package org.imec.ivlab.ehconnector.business.medicationscheme.hubhelper;

import static be.ehealth.businessconnector.hubv3.service.ServiceFactory.PROP_ENDPOINT_INTRAHUB;
import static be.ehealth.businessconnector.hubv3.service.ServiceFactory.PROP_HUBAPPID;
import static be.ehealth.businessconnector.hubv3.service.ServiceFactory.PROP_HUBID;

import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;

public class HubConfigRsw {

    public static void initConfig() throws TechnicalConnectorException {
        ConfigFactory.getConfigValidator().getConfig().setProperty(PROP_ENDPOINT_INTRAHUB, "https://acchub.reseausantewallon.be/HubServices/IntraHub/V3/IntraHub.asmx");
        ConfigFactory.getConfigValidator().getConfig().setProperty(PROP_HUBID, "1990000035");
        ConfigFactory.getConfigValidator().getConfig().setProperty(PROP_HUBAPPID, "RSW");
    }

}
