package org.imec.ivlab.core.util;

import org.apache.log4j.Logger;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XmlDebugUtil {

    private final static Logger LOG = Logger.getLogger(XmlDebugUtil.class);

    public static void logNode(Node n) {

        int type = n.getNodeType();

        switch (type) {
            case Node.ATTRIBUTE_NODE:
                LOG.info("ATTR:");
                printlnCommon(n);
                break;

            case Node.CDATA_SECTION_NODE:
                LOG.info("CDATA:");
                printlnCommon(n);
                break;

            case Node.COMMENT_NODE:
                LOG.info("COMM:");
                printlnCommon(n);
                break;

            case Node.DOCUMENT_FRAGMENT_NODE:
                LOG.info("DOC_FRAG:");
                printlnCommon(n);
                break;

            case Node.DOCUMENT_NODE:
                LOG.info("DOC:");
                printlnCommon(n);
                break;

            case Node.DOCUMENT_TYPE_NODE:
                LOG.info("DOC_TYPE:");
                printlnCommon(n);
                NamedNodeMap nodeMap = ((DocumentType)n).getEntities();
//                indent += 2;
                for (int i = 0; i < nodeMap.getLength(); i++) {
                    Entity entity = (Entity)nodeMap.item(i);
                    logNode(entity);
                }
//                indent -= 2;
                break;

            case Node.ELEMENT_NODE:
                LOG.info("ELEM:");
                printlnCommon(n);

                NamedNodeMap atts = n.getAttributes();
//                indent += 2;
                for (int i = 0; i < atts.getLength(); i++) {
                    Node att = atts.item(i);
                    logNode(att);
                }
//                indent -= 2;
                break;

            case Node.ENTITY_NODE:
                LOG.info("ENT:");
                printlnCommon(n);
                break;

            case Node.ENTITY_REFERENCE_NODE:
                LOG.info("ENT_REF:");
                printlnCommon(n);
                break;

            case Node.NOTATION_NODE:
                LOG.info("NOTATION:");
                printlnCommon(n);
                break;

            case Node.PROCESSING_INSTRUCTION_NODE:
                LOG.info("PROC_INST:");
                printlnCommon(n);
                break;

            case Node.TEXT_NODE:
                LOG.info("TEXT:");
                printlnCommon(n);
                break;

            default:
                LOG.info("UNSUPPORTED NODE: " + type);
                printlnCommon(n);
                break;
        }

        if (n.hasChildNodes()) {
            NodeList childNodes = n.getChildNodes();
            for (int i = 0; i< childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                logNode(item);
            }
        }

    }

    private static void printlnCommon(Node n) {
        LOG.info(" nodeName=\"" + n.getNodeName() + "\"");

        String val = n.getNamespaceURI();
        if (val != null) {
            LOG.info(" uri=\"" + val + "\"");
        }

        val = n.getPrefix();

        if (val != null) {
            LOG.info(" pre=\"" + val + "\"");
        }

        val = n.getLocalName();
        if (val != null) {
            LOG.info(" local=\"" + val + "\"");
        }

        val = n.getNodeValue();
        if (val != null) {
            LOG.info(" nodeValue=");
            if (val.trim().equals("")) {
                // Whitespace
                LOG.info("[WS]");
            }
            else {
                LOG.info("\"" + n.getNodeValue() + "\"");
            }
        }
        LOG.info("");
    }

}
