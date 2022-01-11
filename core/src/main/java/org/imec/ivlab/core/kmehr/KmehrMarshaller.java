package org.imec.ivlab.core.kmehr;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import javax.xml.bind.JAXBException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
import org.imec.ivlab.core.util.JAXBUtils;

public class KmehrMarshaller {

    private static Logger log = LogManager.getLogger(KmehrExtractor.class);
    private static final String KMEHRMESSAGE_TAG_WITH_DEFAULT_NAMESPACES = "<kmehrmessage xmlns=\"http://www.ehealth.fgov.be/standards/kmehr/schema/v1\" xmlns:ns2=\"http://www.w3.org/2001/04/xmlenc#\" xmlns:ns3=\"http://www.w3.org/2000/09/xmldsig#\">";

    public static Kmehrmessage fromString(String kmehrContent) {

        try {
            kmehrContent = resetNamespaces(kmehrContent);

            return JAXBUtils.unmarshal(Kmehrmessage.class, kmehrContent);
        } catch (TransformationException e) {
            log.error("Error while transforming kmehr string to Kmehrmessage.  ", e);
            throw new RuntimeException("Failed to transform kmehr string into Kmehrmessage.", e);
        }
    }

    public static String toString(Kmehrmessage Kmehrmessage) throws JAXBException {
        return JAXBUtils.marshal(Kmehrmessage, "kmehrmessage");
    }

    public static String resetNamespaces(String kmehrContent) {
        kmehrContent = removeNamespaceUsage(kmehrContent);
        kmehrContent = setDefaultNamespace(kmehrContent);
        return kmehrContent;
    }

    private static String removeNamespaceUsage(String kmehrContent) {
        return StringUtils.replacePattern(kmehrContent, "(<|<\\/)(ns\\d*:)", "$1");
    }

    private static String setDefaultNamespace(String kmehrContent) {
        return StringUtils.replacePattern(kmehrContent, "<kmehrmessage(?:[^>]*)>", KMEHRMESSAGE_TAG_WITH_DEFAULT_NAMESPACES);
    }

}
