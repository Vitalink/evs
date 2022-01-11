package org.imec.ivlab.validator.validators.xsd;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.util.JAXBUtils;
import org.imec.ivlab.validator.exceptions.ValidatorXsdException;
import org.imec.ivlab.validator.validators.xsd.handler.XsdValidationResult;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class XsdValidator {
    private static final Logger LOG = LogManager.getLogger(XsdValidator.class);
    private static final String KMEHR_XSD_LOCATION = "kmehr/1_34/ehealth-kmehr/XSD/kmehr_elements-1_34.xsd";

    private List<String> ignoreList;

    public XsdValidator() {
    }

    public XsdValidator(List<String> ignoreList) {
        this.ignoreList = ignoreList;
    }

    public XsdValidationResult validate(Kmehrmessage kmehrmessage) {

        XsdValidationResult errorValidationHandler = new XsdValidationResult(ignoreList);

        try {

            String kmehrmessageString = JAXBUtils.marshal(kmehrmessage);

            DocumentBuilderFactory e = DocumentBuilderFactory.newInstance();
            e.setNamespaceAware(true);
            DocumentBuilder parser = e.newDocumentBuilder();
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            URL resource = XsdValidator.class.getClassLoader().getResource(KMEHR_XSD_LOCATION);
            if (resource == null) {
                throw new ValidatorXsdException("Cannot find the xsd at: " + KMEHR_XSD_LOCATION);
            }

            StreamSource streamSource = new StreamSource(resource.toString());
            Schema schema = factory.newSchema(streamSource);

            Validator validator = schema.newValidator();

            validator.setErrorHandler(errorValidationHandler);

            DOMSource domsrc = new DOMSource(parser.parse(new ByteArrayInputStream(kmehrmessageString.getBytes(StandardCharsets.UTF_8))));
            validator.validate(domsrc);

            return errorValidationHandler;

        } catch (Exception e) {
            errorValidationHandler.fatalAnyError(e);
            return errorValidationHandler;
        }
    }

}
