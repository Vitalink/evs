/*
 * Copyright (c) eHealth
 */
package org.imec.ivlab.ehconnector.business.medicationscheme.hubhelper;

import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;

import static be.ehealth.businessconnector.hubv3.service.ServiceFactory.PROP_ENDPOINT_INTRAHUB;
import static be.ehealth.businessconnector.hubv3.service.ServiceFactory.PROP_HUBAPPID;
import static be.ehealth.businessconnector.hubv3.service.ServiceFactory.PROP_HUBID;

/**
 * @author EHP
 *
 */
public class HubConfigVitalink {

    public static void initConfig() throws TechnicalConnectorException {
        ConfigFactory.getConfigValidator().getConfig().setProperty(PROP_ENDPOINT_INTRAHUB, "https://vitalink-acpt.ehealth.fgov.be/vpmg/vitalink-gateway/IntraHubService");
        ConfigFactory.getConfigValidator().getConfig().setProperty(PROP_HUBID, "1990001916");
        ConfigFactory.getConfigValidator().getConfig().setProperty(PROP_HUBAPPID, "VITALINKGATEWAY");
    }
}
