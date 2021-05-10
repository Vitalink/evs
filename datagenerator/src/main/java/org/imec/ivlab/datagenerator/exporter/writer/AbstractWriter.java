package org.imec.ivlab.datagenerator.exporter.writer;

import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.kmehr.model.util.HCPartyUtil;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.DateUtils;
import org.imec.ivlab.core.util.JAXBUtils;
import org.imec.ivlab.core.util.XmlFormatterUtil;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public abstract class AbstractWriter {

    private final static Logger LOG = Logger.getLogger(AbstractWriter.class);


    protected String formatKmehrMessage(Kmehrmessage kmehrmessage) {

        String kmehrMessageString;
        try {
            kmehrMessageString = JAXBUtils.marshal(kmehrmessage);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        try {
            return XmlFormatterUtil.format(kmehrMessageString, false);
        } catch (IOException | SAXException | ParserConfigurationException | TransformerException e) {
            LOG.error("Error when pretty printing vault content. Will leave business data as is", e);
            return kmehrMessageString;
        }

    }

    protected String getLatestUpdateString(TransactionType transactionType) {

        if (transactionType == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        if (transactionType.getDate() != null) {
            String dateString = DateUtils.toLocalDate(transactionType.getDate()).format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
            sb.append(dateString);
            if (transactionType.getTime() != null) {
                String timeString = DateUtils.toLocalTime(transactionType.getTime()).format(java.time.format.DateTimeFormatter.ofPattern("HHmmss"));
                sb.append("_");
                sb.append(timeString);
            }
        }

        if (transactionType.getAuthor() != null && CollectionsUtil.notEmptyOrNull(transactionType.getAuthor().getHcparties())) {

            HcpartyType hcParty =
                Optional.ofNullable(HCPartyUtil.findFirstNonHubHcParty(transactionType.getAuthor().getHcparties()))
                .orElse(HCPartyUtil.findHubHcParty(transactionType.getAuthor().getHcparties()));

            hcParty = Optional.ofNullable(hcParty).orElse(createDummyHcParty());

            String author = org.imec.ivlab.core.util.StringUtils.joinWith("-", hcParty.getFirstname(), hcParty.getFamilyname(), hcParty.getName());
            if (StringUtils.isNotBlank(author)) {
                if (sb.length() > 0) {
                    sb.append("-");
                } else {
                    sb.append("_");
                }
                sb.append(author);
            }
        }

        return sb.toString();

    }

    private HcpartyType createDummyHcParty() {
        HcpartyType hcParty = new HcpartyType();
        hcParty.setFamilyname("Unknown");
        hcParty.setFirstname("Unknown");
        hcParty.setName("Unknown");
        return hcParty;
    }

}
