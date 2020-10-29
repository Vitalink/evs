/*
 * Copyright (c) eHealth
 */
package be.ehealth.businessconnector.testcommons.testperson.asserter;

import org.junit.Assert;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import be.ehealth.businessconnector.testcommons.testperson.domain.TestPerson;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.session.Session;
import be.ehealth.technicalconnector.utils.SessionUtil;


/**
 * Asserter that verifies if that the TestPerson and the person who instantiate the session are different.
 * 
 * @author EHP
 */
public class TestPersonDifferentFromSession implements TestPersonAsserter {

    /**
     * @see be.ehealth.businessconnector.testcommons.testperson.asserter.TestPersonAsserter#validate(be.ehealth.businessconnector.testcommons.testperson.domain.TestPerson)
     */
    @Override
    public void validate(TestPerson person) {
        if (person.getSsin() == null) {
            return;
        }
        try {
            String ssin = SessionUtil.getNiss();
            if (ssin.equalsIgnoreCase(person.getSsin())) {
                Assert.fail("Invalid test person Reason[config and testperson are the same]");
            }
            Element samlAss = Session.getInstance().getSession().getSAMLToken().getAssertion();
            NodeList attributeStatements = samlAss.getElementsByTagNameNS("urn:oasis:names:tc:SAML:1.0:assertion", "AttributeStatement");
            for (int i = 0; i < attributeStatements.getLength(); i++) {
                Element attributeStatement = (Element) attributeStatements.item(i);
                NodeList attributes = attributeStatement.getElementsByTagNameNS("urn:oasis:names:tc:SAML:1.0:assertion", "Attribute");
                for (int j = 0; j < attributes.getLength(); j++) {
                    Element attr = (Element) attributes.item(j);
                    String attrName = attr.getAttribute("AttributeName");
                    String attrValue = attr.getElementsByTagNameNS("urn:oasis:names:tc:SAML:1.0:assertion", "AttributeValue").item(0).getTextContent();
                    if ("urn:be:fgov:ehealth:1.0:certificateholder:person:ssin".equalsIgnoreCase(attrName) && person.getSsin().equalsIgnoreCase(attrValue)) {
                        Assert.fail("Invalid test person Reason[token contains the same ssin as the testperson]");
                        return;
                    }
                    if ("urn:be:fgov:person:ssin".equalsIgnoreCase(attrName) && person.getSsin().equalsIgnoreCase(attrValue)) {
                        Assert.fail("Invalid test person Reason[token contains the same ssin as the testperson]");
                        return;
                    }
                }
            }
        } catch (TechnicalConnectorException e) {
            Assert.fail("Invalid test person Reason[" + e.getMessage() + "]");
        }
    }
}
