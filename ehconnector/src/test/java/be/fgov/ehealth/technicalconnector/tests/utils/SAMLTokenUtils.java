/*
 * Copyright (c) eHealth
 */
package be.fgov.ehealth.technicalconnector.tests.utils;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;


/**
 * Utility class to extract information of a SAMLToken
 * 
 * @author EH065
 * 
 */
public class SAMLTokenUtils {

    private static final String ASSERTION_NAMESPACE = "urn:oasis:names:tc:SAML:1.0:assertion";

    private static final String SAML_ATTRIBUTE_NAMESPACE = "AttributeNamespace";

    private static final String SAML_ATTRIBUTE_NAME = "AttributeName";

    private static final String ATTR_NAMESPACE = "urn:be:fgov:certified-namespace:ehealth";    

    private static final String SAML_ATTRIBUTE = "Attribute";


    /**
     * @param token
     * @return
     * @throws TechnicalConnectorException
     */
    public static List<String> getAttributeValue(Element token, String attributeNameSuffix) throws TechnicalConnectorException {
        NodeList attributes = extractAttributes(token);

        List<String> result = new ArrayList<String>();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node node = attributes.item(i);
                String attributeName = node.getAttributes().getNamedItem(SAML_ATTRIBUTE_NAME).getTextContent();
                String attributeNamespace = node.getAttributes().getNamedItem(SAML_ATTRIBUTE_NAMESPACE).getTextContent();
                if (attributeName.contains(attributeNameSuffix) && attributeNamespace.equals(ATTR_NAMESPACE)) {
                    if (node.hasChildNodes()) {
                        NodeList attributeValueNodeList = node.getChildNodes();
                        for (int index = 0; index < attributeValueNodeList.getLength(); index++) {
                            result.add(attributeValueNodeList.item(index).getTextContent().trim());
                        }
                    } else {
                        List<String> arrayList = new ArrayList<String>();
                        arrayList.add(node.getTextContent().trim());
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param token
     * @return
     */
    private static NodeList extractAttributes(Element element) {
        NodeList attributes = element.getElementsByTagName(SAML_ATTRIBUTE);
        if (attributes.getLength() == 0) {
            attributes = element.getElementsByTagNameNS(ASSERTION_NAMESPACE, SAML_ATTRIBUTE);
            if (attributes.getLength() == 0) {
                return null;
            }
        }
        return attributes;

    }
}
