package org.imec.ivlab.ehconnector.hub.config;

import be.ehealth.technicalconnector.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.ehconnector.hub.HubService;


public class ConfigurationManager {

    private final static Logger LOG = LogManager.getLogger(HubService.class);


    public static void setProperty(String key, String value) {

        LOG.debug("Setting property '" + key + "' to value'" + value + "'");
        ConfigFactory.getConfigValidator().setProperty(key, value);

    }

}
