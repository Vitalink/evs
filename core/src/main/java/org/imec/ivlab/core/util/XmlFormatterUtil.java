package org.imec.ivlab.core.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlFormatterUtil {

    private final static Logger LOG = LogManager.getLogger(XmlFormatterUtil.class);


    /**
     * An XML formatting utility that formats an xml string which spaces in tags instead of replacing them with an empty tag
     * @param xml
     * @param omitXmlDeclaration
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public static String format(String xml, Boolean omitXmlDeclaration) throws IOException, SAXException, ParserConfigurationException, TransformerException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
        Document document = db.parse(new InputSource(new StringReader(xml)));

        return documentToFormattedString(document, omitXmlDeclaration);

    }

    public static String documentToFormattedString(Document document, boolean omitXmlDeclaration) throws TransformerException {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitXmlDeclaration ? "yes" : "no");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        return writer.toString();
    }


}
