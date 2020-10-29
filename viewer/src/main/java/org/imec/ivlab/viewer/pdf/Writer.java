package org.imec.ivlab.viewer.pdf;

import static org.imec.ivlab.viewer.pdf.TableHelper.addRow;
import static org.imec.ivlab.viewer.pdf.TableHelper.createDetailHeader;
import static org.imec.ivlab.viewer.pdf.TableHelper.initializeDetailTable;
import static org.imec.ivlab.viewer.pdf.TableHelper.toDetailRowIfHasValue;
import static org.imec.ivlab.viewer.pdf.TableHelper.toDetailRowsIfHasValue;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDADDRESS;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTELECOM;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY;
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENT;
import be.fgov.ehealth.standards.kmehr.schema.v1.AddressType;
import be.fgov.ehealth.standards.kmehr.schema.v1.CountryType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TelecomType;
import com.itextpdf.text.pdf.PdfPTable;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.model.internal.parser.sumehr.AbstractPerson;
import org.imec.ivlab.core.model.internal.parser.sumehr.HcParty;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.StringUtils;
import org.imec.ivlab.core.util.XmlFormatterUtil;
import org.imec.ivlab.core.util.XmlModifier;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class Writer {

    private final static Logger LOG = Logger.getLogger(Writer.class);

    protected static Set<String> idHcPartiesToIgnore = new HashSet<>();
    static {
        idHcPartiesToIgnore.add("LOCAL");
    }

    private static Set<String> idPatientsToIgnore = new HashSet<>();
    static {
        idPatientsToIgnore.add("LOCAL");
    }

    private static Set<String> cdHcPartiesToIgnore = new HashSet<>();
    static {
        cdHcPartiesToIgnore.add("LOCAL");
    }

    private static HashMap<String, String> idHcPartyTranslations = new HashMap<>();
    static {
        idHcPartyTranslations.put("ID-HCPARTY", "RIZIV");
        idHcPartyTranslations.put("ID-ENCRYPTION-ACTOR", "Encryption actor ID");
    }

    private static HashMap<String, String> cdHcPartyTranslations = new HashMap<>();
    static {
        cdHcPartyTranslations.put("CD-HCPARTY", "Role");
        cdHcPartyTranslations.put("CD-ENCRYPTION-ACTOR", "Encryption actor code");
    }

    protected PdfPTable hcpartyTypeToTable(HcParty hcParty) {
        PdfPTable table = initializeDetailTable();

        String title = StringUtils.joinWith(" ", hcParty.getFirstname(), hcParty.getFamilyname(), hcParty.getName());
        addRow(table, createDetailHeader(title));

        addRow(table, toDetailRowsIfHasValue(getHcPartyIdentifiers(hcParty.getIds())));
        addRow(table, toDetailRowsIfHasValue(getHcPartyCodes(hcParty.getCds())));
        addRow(table, toDetailRowIfHasValue("First name", hcParty.getFirstname()));
        addRow(table, toDetailRowIfHasValue("Family name", hcParty.getFamilyname()));
        addRow(table, toDetailRowIfHasValue("Name", hcParty.getName()));
        addRow(table, toDetailRowsIfHasValue(getTelecoms(hcParty.getTelecoms())));
        addRow(table, toDetailRowsIfHasValue(getAddresses(hcParty.getAddresses())));

        return table;
    }

    protected PdfPTable personToTable(AbstractPerson person) {

        PdfPTable table = initializeDetailTable();
        addRow(table, createDetailHeader(StringUtils.joinFields(StringUtils.joinWith(" ", person.getFirstnames().toArray()), person.getFamilyname(), " ")));
        addRow(table, toDetailRowsIfHasValue(getPatientIdentifiers(person.getIds())));
        if (CollectionsUtil.notEmptyOrNull(person.getFirstnames())) {
            for (String firstName : person.getFirstnames()) {
                addRow(table, toDetailRowIfHasValue("First name", firstName));
            }
        }
        addRow(table, toDetailRowIfHasValue("Family name", person.getFamilyname()));
        addRow(table, toDetailRowIfHasValue("Birthdate", person.getBirthdate()));
        addRow(table, toDetailRowIfHasValue("Deathdate", person.getDeathdate()));
        addRow(table, toDetailRowIfHasValue("Usual language", person.getUsuallanguage()));
        if (person.getSex() != null) {
            addRow(table, toDetailRowIfHasValue("Sex", person.getSex().value()));
        }
        addRow(table, toDetailRowsIfHasValue(getTelecoms(person.getTelecoms())));
        addRow(table, toDetailRowsIfHasValue(getAddresses(person.getAddresses())));
        return table;

    }

    protected List<Pair> getAddresses(List<AddressType> addressTypes) {
        List<Pair> existingRows = new ArrayList<>();


        if (CollectionsUtil.emptyOrNull(addressTypes)) {
            return null;
        }

        for (AddressType addressType : addressTypes) {
            String key = org.apache.commons.lang3.StringUtils.join("Address: ", StringUtils.joinWith(" - ", collectCDAddresses(addressType.getCds()).toArray()));
            String streetAndNumber = StringUtils.joinWith(" ", addressType.getStreet(), addressType.getHousenumber());
            String zipAndCity = StringUtils.joinWith(" ", addressType.getZip(), addressType.getCity());
            String districtAndCountry = StringUtils.joinWith(" ", addressType.getDistrict(), getCountryString(addressType.getCountry()));
            String nis = addressType.getNis() == null ? null : "nis: " + addressType.getNis();
            existingRows.add(Pair.of(key, StringUtils.joinWith(System.lineSeparator(), streetAndNumber, zipAndCity, districtAndCountry, nis)));
        }

        return existingRows;
    }

    private List<Pair> getPatientIdentifiers(List<IDPATIENT> idpatients) {
        List<Pair> existingRows = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(idpatients)) {
            return null;
        }

        for (IDPATIENT idpatient : idpatients) {
            if (idpatient.getS() != null && idPatientsToIgnore.contains(idpatient.getS().value())) {
                continue;
            }
            existingRows.add(Pair.of(idpatient.getS().value(), idpatient.getValue()));
        }

        return existingRows;

    }

    protected List<Pair> getTelecoms(List<TelecomType> telecomTypes) {
        List<Pair> existingRows = new ArrayList<>();


        if (CollectionsUtil.emptyOrNull(telecomTypes)) {
            return null;
        }

        for (TelecomType telecomType : telecomTypes) {
            existingRows.add(Pair.of(org.apache.commons.lang3.StringUtils.join("Contact: ", StringUtils.joinWith(" - ", collectTelecoms(telecomType.getCds()).toArray())), telecomType.getTelecomnumber()));
        }

        return existingRows;

    }

    protected List<Pair> getHcPartyIdentifiers(List<IDHCPARTY> idhcparties) {
        List<Pair> existingRows = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(idhcparties)) {
            return null;
        }

        for (IDHCPARTY idhcparty : idhcparties) {
            if (idhcparty.getS() != null && idHcPartiesToIgnore.contains(idhcparty.getS().value())) {
                continue;
            }
            existingRows.add(Pair.of(translateIdHcparty(idhcparty), idhcparty.getValue()));
        }

        return existingRows;

    }

    protected List<Pair> getHcPartyCodes(List<CDHCPARTY> cdhcparties) {
        List<Pair> existingRows = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(cdhcparties)) {
            return null;
        }

        for (CDHCPARTY cdhcparty : cdhcparties) {
            if (cdhcparty.getS() != null && cdHcPartiesToIgnore.contains(cdhcparty.getS().value())) {
                continue;
            }
            existingRows.add(Pair.of(translateCdHcParty(cdhcparty), cdhcparty.getValue()));
        }

        return existingRows;

    }

    private String translateIdHcparty(IDHCPARTY idhcparty) {

        if (idHcPartyTranslations.containsKey(idhcparty.getS().value())) {
            return idHcPartyTranslations.get(idhcparty.getS().value());
        }

        return idhcparty.getS().value();
    }


    private String translateCdHcParty(CDHCPARTY cdhcparty) {

        if (cdHcPartyTranslations.containsKey(cdhcparty.getS().value())) {
            return cdHcPartyTranslations.get(cdhcparty.getS().value());
        }

        return cdhcparty.getS().value();
    }

    private String getCountryString(CountryType countryType) {
        if (countryType == null || countryType.getCd() == null) {
            return null;
        }
        return countryType.getCd().getValue();
    }

    private Set<String> collectCDAddresses(List<CDADDRESS> cdaddresses) {
        return Optional.ofNullable(cdaddresses)
            .orElse(Collections.emptyList())
            .stream()
            .map(CDADDRESS::getValue)
            .collect(Collectors.toSet());
    }

    private Set<String> collectTelecoms(List<CDTELECOM> cdtelecoms) {
        return Optional.ofNullable(cdtelecoms)
            .orElse(Collections.emptyList())
            .stream()
            .map(CDTELECOM::getValue)
            .collect(Collectors.toSet());
    }

    protected String parseTextWithLayoutContent(Object content) {
        if (content instanceof ElementNSImpl) {
            String codeString = kmehrNodeToString((ElementNSImpl) content);
            return org.apache.commons.lang3.StringUtils.trim(codeString);
        } else {
            String contentString = org.apache.commons.lang3.StringUtils.trimToEmpty(content.toString());
            if (contentString.isEmpty()) {
                return null;
            } else {
                return contentString;
            }
        }
    }

    private String kmehrNodeToString(ElementNSImpl content) {
        ElementNSImpl item = content;
        Document ownerDocument = item.getOwnerDocument();
        try {
            String xml = XmlFormatterUtil.documentToFormattedString(ownerDocument, true);
            XmlModifier xmlModifier = new XmlModifier(xml);
            xml = xmlModifier.toXmlStringIncludingRootNode();
            return xml;
        } catch (TransformerException | IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
