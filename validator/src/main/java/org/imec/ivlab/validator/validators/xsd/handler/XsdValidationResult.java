package org.imec.ivlab.validator.validators.xsd.handler;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.validators.model.Level;
import org.imec.ivlab.validator.validators.xsd.model.XsdFailure;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import java.util.ArrayList;
import java.util.List;

public class XsdValidationResult implements ErrorHandler {

    private static Logger LOG = Logger.getLogger(XsdValidationResult.class);

    private List<XsdFailure> failedList = new ArrayList<>();

    private List<String> xsdErrorMessagesToIgnoreList = new ArrayList<>();

    public XsdValidationResult() {
    }

    public XsdValidationResult(List<String> xsdErrorMessagesToIgnoreList) {
        this.xsdErrorMessagesToIgnoreList = xsdErrorMessagesToIgnoreList;
    }

    public void warning(SAXParseException exception) {
        String msg = this.toString(exception);
        addIfNotIgnored(Level.WARNING, msg);
    }

    public void error(SAXParseException exception) {
        String msg = this.toString(exception);
        addIfNotIgnored(Level.ERROR, msg);
    }

    public void fatalError(SAXParseException exception) {
        fatalAnyError(exception);
    }

    public void fatalAnyError(Exception exception) {
        String msg = this.toString(exception);
        addIfNotIgnored(Level.FATAL, msg);
    }

    private void addIfNotIgnored(Level level, String message) {

        if (CollectionsUtil.notEmptyOrNull(xsdErrorMessagesToIgnoreList)) {
            for (String stringToIgnore : xsdErrorMessagesToIgnoreList) {
                if (StringUtils.isNotEmpty(stringToIgnore) && StringUtils.containsIgnoreCase(message, stringToIgnore)) {
                    LOG.info("Ignoring xsd failure because it was specified in the xsdErrorMessagesToIgnoreList. Ignored rule result: " + message);
                    return;
                }
            }
        }

        failedList.add(new XsdFailure(level, message));

    }

    private String toString(Exception exception) {
        return exception.getMessage();
    }

    public List<XsdFailure> getFailedList() {
        return failedList;
    }

    public List<XsdFailure> getFailedListByRuleLevel(Level... level) {

        List<XsdFailure> matchingResults = new ArrayList<>();

        for (XsdFailure failure : failedList) {
            if (ArrayUtils.indexOf(level, failure.getLevel()) >= 0) {
                matchingResults.add(failure);
            }
        }

        return matchingResults;

    }

}