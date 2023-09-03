package org.imec.ivlab.core.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Set;

public class XmlModifier {


    Document doc;

    public XmlModifier(String xmlToModify) {
        try {
            doc = prepareDocument(xmlToModify);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void removeNodeNames(Set<String> nodenamesToRemove) {

        NodeList childNodes = doc.getChildNodes();
        for (int i = 0; i< childNodes.getLength(); i++) {

            Node item = childNodes.item(i);
            removeNodesmatchingNames(item, nodenamesToRemove);

        }

    }

    public void removeNodesWithoutContent() throws ParserConfigurationException, IOException, SAXException, TransformerException {

        NodeList childNodes = doc.getChildNodes();
        for (int i = 0; i< childNodes.getLength(); i++) {

            Node item = childNodes.item(i);
            removeEmptyNodes(item);

        }

    }

    public String toXmlString() throws IOException, SAXException, ParserConfigurationException, TransformerException {

        int elementNodes = countNodes(doc, Node.ELEMENT_NODE);

        if (elementNodes < 2) {
            return null;
        } else {
            String formattedString = XmlFormatterUtil.documentToFormattedString(doc, true);
            String formattedStringWithEmptyLinesRemoved = formattedString.replaceAll(System.lineSeparator() + "[\\s\\t]*" + System.lineSeparator(), System.lineSeparator());
            String formattedStringWithNamespacesremoved = formattedStringWithEmptyLinesRemoved.replaceAll("\\s*xmlns(?:\\:ns\\d)?=\\\"[^\\\"]+\\\"", "");
            return formattedStringWithNamespacesremoved;
        }
    }

    public String toXmlStringIncludingRootNode() throws IOException, SAXException, ParserConfigurationException, TransformerException {

        int elementNodes = countNodes(doc, Node.ELEMENT_NODE);

        if (elementNodes < 1) {
            return null;
        } else {
            String formattedString = XmlFormatterUtil.documentToFormattedString(doc, true);
            String formattedStringWithEmptyLinesRemoved = formattedString.replaceAll(System.lineSeparator() + "[\\s\\t]*" + System.lineSeparator(), System.lineSeparator());
            String formattedStringWithNamespacesremoved = formattedStringWithEmptyLinesRemoved.replaceAll("\\s*xmlns(?:\\:ns\\d)?=\\\"[^\\\"]+\\\"", "");
            return formattedStringWithNamespacesremoved;
        }
    }

    private Document prepareDocument(String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringComments(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        OutputStreamWriter errorWriter = new OutputStreamWriter(System.err,
                "UTF-8");
        db.setErrorHandler(new MyErrorHandler(new PrintWriter(errorWriter, true)));

        return db.parse(new InputSource(new StringReader(xml)));
    }

    private static int countNodes(Node n, int nodeType) {

        int nodeCount = 0;

        if (nodeType == n.getNodeType()) {
            nodeCount++;
        }

        if (nodeType == Node.DOCUMENT_TYPE_NODE || nodeType == Node.ELEMENT_NODE) {
            for (Node child = n.getFirstChild(); child != null;
                 child = child.getNextSibling()) {
                nodeCount+= countNodes(child, nodeType);
            }
        }

        return nodeCount;

    }

    private void removeNodesmatchingNames(Node node, Set<String> nodenamesToRemove) {
        if (CollectionsUtil.emptyOrNull(nodenamesToRemove)) {
            return;
        }
        NodeList nodeList = node.getChildNodes();
        for(int i=0; i < nodeList.getLength(); i++){
            Node childNode = nodeList.item(i);
            String nodeName = childNode.getNodeName();
            if (nodenamesToRemove.contains(nodeName)) {
                childNode.getParentNode().removeChild(childNode);
                i--;
            }
            removeNodesmatchingNames(childNode, nodenamesToRemove);
        }
    }

    private void removeEmptyNodes(Node node) {
        NodeList nodeList = node.getChildNodes();
        for(int i=0; i < nodeList.getLength(); i++){
            Node childNode = nodeList.item(i);
            //String nodeName = childNode.getNodeName();
            if(org.apache.commons.lang3.StringUtils.isEmpty(org.apache.commons.lang3.StringUtils.trimToEmpty(childNode.getTextContent()))){
                childNode.getParentNode().removeChild(childNode);
                i--;
            }
            removeEmptyNodes(childNode);
        }
    }

    private class MyErrorHandler implements ErrorHandler {

        private PrintWriter out;

        MyErrorHandler(PrintWriter out) {
            this.out = out;
        }

        private String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();
            if (systemId == null) {
                systemId = "null";
            }

            String info = "URI=" + systemId + " Line=" + spe.getLineNumber() +
                    ": " + spe.getMessage();
            return info;
        }

        public void warning(SAXParseException spe) throws SAXException {
            out.println("Warning: " + getParseExceptionInfo(spe));
        }

        public void error(SAXParseException spe) throws SAXException {
            String message = "Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }

        public void fatalError(SAXParseException spe) throws SAXException {
            String message = "Fatal Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }
    }

}
