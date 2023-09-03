package org.imec.ivlab.core.model.internal.parser;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.util.JAXBUtils;
import org.imec.ivlab.core.util.XmlModifier;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Set;

public class ParseHelper {

    //private final static Logger LOG = LogManager.getLogger(ParseHelper.class);

    public static String objectToSimplifiedXml(Object object, String rootelement, Set<String> ignoredNodeNames) {
        if (object == null) {
            return null;
        }
        String objectString = null;
        try {
            objectString = JAXBUtils.marshal(object, rootelement);
            XmlModifier xmlModifier = new XmlModifier(objectString);
            xmlModifier.removeNodeNames(ignoredNodeNames);
            xmlModifier.removeNodesWithoutContent();
            return xmlModifier.toXmlString();
        } catch (ParserConfigurationException | TransformerException | SAXException | IOException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }

}
