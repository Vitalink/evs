package org.imec.ivlab.ehconnector.hub.logging;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public class CommunicationLoggerConfiguration {

    private static CommunicationLoggerConfiguration communicationLoggerConfiguration;

    private Set<String> communicationActionIgnoreList = new LinkedHashSet<>();

    private CommunicationLoggerConfiguration() {

    }

    public static CommunicationLoggerConfiguration getInstance() {
        if(communicationLoggerConfiguration == null) {
            communicationLoggerConfiguration = new CommunicationLoggerConfiguration();
        }
        return communicationLoggerConfiguration;
    }

    public void addToIgnoreList(String action) {
        communicationActionIgnoreList.add(StringUtils.lowerCase(action));
    }

    public boolean isIgnored(String action) {
        return communicationActionIgnoreList.contains(StringUtils.lowerCase(action));
    }
}
