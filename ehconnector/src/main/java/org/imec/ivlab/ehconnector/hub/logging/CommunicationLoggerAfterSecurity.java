package org.imec.ivlab.ehconnector.hub.logging;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.handler.AbstractSOAPHandler;
import be.ehealth.technicalconnector.utils.ConnectorXmlUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class CommunicationLoggerAfterSecurity extends AbstractSOAPHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CommunicationLoggerAfterSecurity.class);


    public boolean handleOutbound(SOAPMessageContext context) {

        return true;
    }

    public boolean handleInbound(SOAPMessageContext context) {

        SOAPMessage msg = context.getMessage();
        String messageText;
        try {
            messageText = ConnectorXmlUtils.format(ConnectorXmlUtils.toString(msg.getSOAPPart().getEnvelope()));
        } catch (TechnicalConnectorException | SOAPException e) {
            LOG.error("Failed to log incoming message", e);
            messageText = ExceptionUtils.getStackTrace(e);
        }

        String action = null;
        try {
            action = msg.getSOAPPart().getEnvelope().getBody().getFirstChild().getLocalName();
        } catch (SOAPException e) {
            action = "unknown";
        }

        logMessage(messageText, action);

        return true;

    }

    private void logMessage(String messageText, String action) {
        CommunicationLoggerConfiguration instance = CommunicationLoggerConfiguration.getInstance();
        if (!instance.isIgnored(action)) {
            MessageWriter.logMessage(messageText, action);
        }
    }

    public boolean handleFault(SOAPMessageContext c) {
        this.handleMessage(c);
        return true;
    }
}
