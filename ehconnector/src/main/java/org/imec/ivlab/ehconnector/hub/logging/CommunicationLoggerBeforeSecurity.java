package org.imec.ivlab.ehconnector.hub.logging;

import static org.imec.ivlab.ehconnector.hub.logging.Kind.WITHOUT_SECURITY;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.handler.AbstractSOAPHandler;
import be.ehealth.technicalconnector.utils.ConnectorXmlUtils;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.imec.ivlab.core.config.EVSConfig;
import org.imec.ivlab.core.config.EVSProperties;
import org.imec.ivlab.core.model.hub.LogCommunicationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunicationLoggerBeforeSecurity extends AbstractSOAPHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CommunicationLoggerBeforeSecurity.class);

    public boolean handleOutbound(SOAPMessageContext context) {
        if (getLogCommunicationType().equals(LogCommunicationType.WITHOUT_SECURITY) || getLogCommunicationType().equals(LogCommunicationType.ALL)) {
            logMessage(getMessageText(context.getMessage()), getAction(context.getMessage()), WITHOUT_SECURITY);
        }
        return true;
    }

    public boolean handleInbound(SOAPMessageContext context) {
        if (getLogCommunicationType().equals(LogCommunicationType.WITHOUT_SECURITY) || getLogCommunicationType().equals(LogCommunicationType.ALL)) {
            logMessage(getMessageText(context.getMessage()), getAction(context.getMessage()), WITHOUT_SECURITY);
        }
        return true;
    }

    private static String getAction(SOAPMessage msg) {
        String action = null;
        try {
            action = msg
                .getSOAPPart().getEnvelope().getBody().getFirstChild().getLocalName();
        } catch (SOAPException e) {
            action = "unknown";
        }
        return action;
    }

    private static String getMessageText(SOAPMessage msg) {
        String messageText;
        try {
            messageText = ConnectorXmlUtils.format(ConnectorXmlUtils.toString(msg
                .getSOAPPart().getEnvelope()));
        } catch (TechnicalConnectorException | SOAPException e) {
            LOG.error("Failed to log outgoing message", e);
            messageText = ExceptionUtils.getStackTrace(e);
        }
        return messageText;
    }

    private void logMessage(String messageText, String action, Kind kind) {
        CommunicationLoggerConfiguration instance = CommunicationLoggerConfiguration.getInstance();
        if (!instance.isIgnored(action)) {
            MessageWriter.logMessage(messageText, action, kind);
        }
    }

    @Override
    public boolean handleFault(SOAPMessageContext c) {
        this.handleMessage(c);
        return true;
    }

    private LogCommunicationType getLogCommunicationType() {
        return  LogCommunicationType.fromValue(StringUtils.lowerCase(EVSConfig
            .getInstance().getProperty(EVSProperties.LOG_COMMUNICATION_TYPE)));
    }
}
