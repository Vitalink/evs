package org.imec.ivlab.viewer.pdf;

import static org.imec.ivlab.viewer.pdf.PdfHelper.writeToDocument;
import static org.imec.ivlab.viewer.pdf.TableHelper.addRow;
import static org.imec.ivlab.viewer.pdf.TableHelper.combineTables;
import static org.imec.ivlab.viewer.pdf.TableHelper.createDetailHeader;
import static org.imec.ivlab.viewer.pdf.TableHelper.createDetailRow;
import static org.imec.ivlab.viewer.pdf.TableHelper.createTitleTable;
import static org.imec.ivlab.viewer.pdf.TableHelper.initializeDetailTable;
import static org.imec.ivlab.viewer.pdf.TableHelper.toDetailRowIfHasValue;
import static org.imec.ivlab.viewer.pdf.TableHelper.toUnparsedContentTables;
import static org.imec.ivlab.viewer.pdf.Translator.formatAsDate;
import static org.imec.ivlab.viewer.pdf.Translator.formatAsDateTime;
import static org.imec.ivlab.viewer.pdf.Translator.formatAsTime;
import static org.imec.ivlab.viewer.pdf.VaccinationHelper.getMedicinalDeliveredCnks;
import static org.imec.ivlab.viewer.pdf.VaccinationHelper.getMedicinalDeliveredNames;
import static org.imec.ivlab.viewer.pdf.VaccinationHelper.getMedicinalIntendedCnks;
import static org.imec.ivlab.viewer.pdf.VaccinationHelper.getMedicinalIntendedNames;
import static org.imec.ivlab.viewer.pdf.VaccinationHelper.getSubstanceDeliveredCnks;
import static org.imec.ivlab.viewer.pdf.VaccinationHelper.getSubstanceDeliveredNames;
import static org.imec.ivlab.viewer.pdf.VaccinationHelper.getSubstanceIntendedCnks;
import static org.imec.ivlab.viewer.pdf.VaccinationHelper.getSubstanceIntendedNames;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDDRUGCNK;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDINNCLUSTER;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLNKvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDMEDIATYPEvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDUNIT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDUNITschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.LnkType;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.util.TextTypeUtil;
import org.imec.ivlab.core.model.internal.parser.ParsedItem;
import org.imec.ivlab.core.model.internal.parser.common.Header;
import org.imec.ivlab.core.model.internal.parser.sumehr.HcParty;
import org.imec.ivlab.core.model.internal.parser.vaccination.EncounterLocation;
import org.imec.ivlab.core.model.internal.parser.vaccination.Vaccination;
import org.imec.ivlab.core.model.internal.parser.vaccination.VaccinationItem;
import org.imec.ivlab.core.model.upload.KmehrWithReferenceList;
import org.imec.ivlab.core.model.upload.extractor.VaccinationListExtractor;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
import org.imec.ivlab.core.util.IOUtils;
import org.imec.ivlab.viewer.converter.TestFileConverter;

public class VaccinationWriter extends Writer {

    public static void main(String[] args) {

        VaccinationWriter vaccinationWriter = new VaccinationWriter();
        Stream.of("vaccination-with-medicinal-product", "vaccination-with-cdatc-and-batch", "vaccination-no-quantity", "vaccination-with-substance-product", "vaccination-with-unparsable-content", "vaccination-with-encounterlocation")
              .forEach(filename -> vaccinationWriter.createPdf(readTestFile(filename + ".xml").get(0), filename + ".pdf"));

    }

    private static List<Vaccination> readTestFile(String filename) {
        File inputFile = IOUtils.getResourceAsFile("/vaccination/" + filename);

        KmehrEntryList kmehrEntryList = KmehrExtractor.getKmehrEntryList(inputFile);
        KmehrWithReferenceList diaryNoteList = new VaccinationListExtractor().getKmehrWithReferenceList(kmehrEntryList);

        return TestFileConverter.convertToVaccinations(diaryNoteList);
    }

    public void createPdf(Vaccination vaccination, String fileLocation) {

        String schemeTitle = "Vaccination Visualization";

        PdfPTable generalInfoTable = createGeneralInfoTable(schemeTitle, vaccination.getHeader());
        List<PdfPTable> detailTables = createDetailTables(vaccination);

        writeToDocument(fileLocation, generalInfoTable, detailTables);
    }

    private List<PdfPTable> createDetailTables(Vaccination vaccination) {

        List<PdfPTable> tables = new ArrayList<>();

        tables.add(combineTables(null, new ArrayList<>(), toUnparsedContentTables(Collections.singletonList(vaccination), null)));

        tables.add(combineTables(createTitleTable("Sender"), createHcPartyTables(vaccination.getHeader().getSender().getHcParties()), toUnparsedContentTables(null, "Sender")));
        tables.add(combineTables(createTitleTable("Recipient"), createHcPartyTables(vaccination.getHeader().getRecipients().stream().flatMap(recipient -> recipient.getHcParties().stream()).collect(Collectors.toList())), toUnparsedContentTables(null, "Sender")));
        tables.add(combineTables(createTitleTable("Patient"), patientToTable(vaccination.getTransactionCommon().getPerson()), toUnparsedContentTable(vaccination.getTransactionCommon().getPerson(), "Patient")));
        tables.add(combineTables(createTitleTable("Author"), createHcPartyTables(vaccination.getTransactionCommon().getAuthor()), toUnparsedContentTables(vaccination.getTransactionCommon().getAuthor(), "Author")));
        tables.add(combineTables(createTitleTable("Redactor"), createHcPartyTables(vaccination.getTransactionCommon().getRedactor()), toUnparsedContentTables(vaccination.getTransactionCommon().getRedactor(), "Redactor")));
        tables.add(combineTables(createTitleTable("Transaction metadata"), createTransactionMetadata(vaccination), toUnparsedContentTables(vaccination.getTransactionCommon().getAuthor(), "Author")));
        List<PdfPTable> unparsedVaccinationTables = vaccination
            .getVaccinationItems()
            .stream()
            .findFirst()
            .map(vaccinationItem -> toUnparsedContentTable(vaccinationItem, "Vaccination"))
            .orElse(Collections.emptyList());
        tables.add(combineTables(createTitleTable("Transaction details"), createVaccinationTables(vaccination), unparsedVaccinationTables));
        return tables;

    }

    private List<PdfPTable> createVaccinationTables(Vaccination vaccination) {
        List<PdfPTable> tables = new ArrayList<>();

        tables.addAll(createLnkTable(vaccination.getLinkTypes()));
        tables.addAll(createTextWithoutLayoutTable(vaccination.getTextTypes()));
        vaccination.getVaccinationItems().forEach(vaccinationItem -> tables.add(createVaccinationDetailsTable(vaccinationItem)));
        vaccination.getEncounterLocations().forEach(encounterLocation -> tables.add(createEncounterLocationTable(encounterLocation)));

        return tables;
    }

    private PdfPTable createEncounterLocationTable(EncounterLocation encounterLocation) {
        PdfPTable table = initializeDetailTable();

        addRow(table, createDetailHeader("Encounter location"));

        addRow(table, toDetailRowIfHasValue("Text", StringUtils.joinWith(System.lineSeparator(), TextTypeUtil.toStrings(encounterLocation.getTextTypes()).toArray())));

        encounterLocation.getAuthors().forEach(hcParty -> addHcPartyDetailRows(hcParty, table));

        return table;
    }

    private PdfPTable createVaccinationDetailsTable(VaccinationItem vaccinationItem) {
        PdfPTable table = initializeDetailTable();
        addRow(table, createDetailHeader("Vaccination"));

        List<CDDRUGCNK> intendedMedicinalCnks = getMedicinalIntendedCnks(vaccinationItem);
        List<String> intendedMedicinalNames = getMedicinalIntendedNames(vaccinationItem);
        List<CDDRUGCNK> deliveredMedicinalCnks = getMedicinalDeliveredCnks(vaccinationItem);
        List<String> deliveredMedicinalNames = getMedicinalDeliveredNames(vaccinationItem);

        intendedMedicinalCnks.forEach(cnk -> addRow(table, createCnkRow(cnk, "Intended")));
        deliveredMedicinalCnks.forEach(cnk -> addRow(table, createCnkRow(cnk, "Delivered")));
        intendedMedicinalNames.forEach(name -> addRow(table, createProductNameRow(name, "Intended name")));
        deliveredMedicinalNames.forEach(name -> addRow(table, createProductNameRow(name, "Delivered name")));

        List<CDINNCLUSTER> intendedSubstanceCnks = getSubstanceIntendedCnks(vaccinationItem);
        List<String> intendedSubstanceNames = getSubstanceIntendedNames(vaccinationItem);
        List<CDDRUGCNK> deliveredSubstanceCnks = getSubstanceDeliveredCnks(vaccinationItem);
        List<String> deliveredSubstanceNames = getSubstanceDeliveredNames(vaccinationItem);

        intendedSubstanceCnks.forEach(innCluster -> addRow(table, createMedicinalProductInnClusterRow(innCluster, "Intended")));
        deliveredSubstanceCnks.forEach(cnk -> addRow(table, createCnkRow(cnk, "Delivered")));
        intendedSubstanceNames.forEach(name -> addRow(table, createProductNameRow(name, "Intended name")));
        deliveredSubstanceNames.forEach(name -> addRow(table, createProductNameRow(name, "Delivered name")));

        vaccinationItem.getCdcontents().forEach(cdContent -> addRow(table, createCdContentRow(cdContent)));

        addRow(table, toDetailRowIfHasValue("Lifecycle", Optional.of(vaccinationItem).map(VaccinationItem::getLifecycle).map(CDLIFECYCLEvalues::value).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Text", StringUtils.joinWith(System.lineSeparator(), TextTypeUtil.toStrings(vaccinationItem.getTextTypes()).toArray())));
        addRow(table, toDetailRowIfHasValue("Application date", formatAsDate(vaccinationItem.getBeginMoment())));
        addRow(table, toDetailRowIfHasValue("Quantity", Optional.ofNullable(vaccinationItem.getQuantity()).map(BigDecimal::toString).orElse(null)));
        addRow(table, createQuantityUnitRow(vaccinationItem.getQuantityUnit(), "Unit"));
        addRow(table, toDetailRowIfHasValue("Batch", vaccinationItem.getBatch()));
        return table;
    }

    private List<PdfPCell> createCdContentRow(CDCONTENT cdcontent) {
        String key = Optional.ofNullable(cdcontent.getS()).map(CDCONTENTschemes::value).orElse(null);
        String value = cdcontent.getValue();
        return toDetailRowIfHasValue(key, value);
    }

    private List<PdfPCell> createQuantityUnitRow(CDUNIT cdunit, String titlePrefix) {
        Optional<CDUNIT> maybeUnit = Optional.ofNullable(cdunit);
        String key = StringUtils.joinWith(" ", titlePrefix, maybeUnit.map(CDUNIT::getS).map(CDUNITschemes::value).orElse(null));
        String value = maybeUnit.map(CDUNIT::getValue).orElse(null);
        return toDetailRowIfHasValue(key, value);
    }

    private List<PdfPCell> createProductNameRow(String productName, String title) {
        String key = title;
        String value = productName;
        return toDetailRowIfHasValue(key, value);
    }

    private List<PdfPCell> createMedicinalProductInnClusterRow(CDINNCLUSTER innCluster, String titlePrefix) {
        String key = StringUtils.joinWith(" ", titlePrefix, Optional.ofNullable(innCluster.getS()).orElse(null));
        String value = innCluster.getValue();
        return toDetailRowIfHasValue(key, value);
    }

    private List<PdfPCell> createCnkRow(CDDRUGCNK cddrugcnk, String titlePrefix) {
        String key = StringUtils.joinWith(" ", titlePrefix, Optional.ofNullable(cddrugcnk.getS()).orElse(null));
        String value = cddrugcnk.getValue();
        return toDetailRowIfHasValue(key, value);
    }

    protected PdfPTable createTransactionMetadata(Vaccination vaccination) {

        PdfPTable table = initializeDetailTable();
        addRow(table, createDetailHeader("General information"));
        addRow(table, createDetailRow("Date", formatAsDate(vaccination.getTransactionCommon().getDate())));
        addRow(table, createDetailRow("Time", formatAsTime(vaccination.getTransactionCommon().getTime())));
        addRow(table, toDetailRowIfHasValue("Record date time", formatAsDateTime(vaccination.getTransactionCommon().getRecordDateTime())));
        return table;

    }

    protected static Font getValidationAnnotationFont() {
        Font font = new Phrase().getFont();
        font.setSize(8);
        font.setStyle(Font.NORMAL);
        font.setColor(BaseColor.WHITE);
        return font;
    }

    private PdfPTable lnkToTable(LnkType lnkType) {
        PdfPTable table = initializeDetailTable();

        addRow(table, createDetailHeader("Link"));
        addRow(table, toDetailRowIfHasValue("Type", Optional.ofNullable(lnkType.getTYPE()).map(CDLNKvalues::value).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Mediatype", Optional.ofNullable(lnkType.getMEDIATYPE()).map(CDMEDIATYPEvalues::value).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Size", lnkType.getSIZE()));
        addRow(table, toDetailRowIfHasValue("Url", lnkType.getURL()));
        addRow(table, toDetailRowIfHasValue("Image", lnkType.getValue()));

        return table;
    }

    private PdfPTable textWithoutLayoutToTable(TextType textType) {
        PdfPTable table = initializeDetailTable();

        addRow(table, createDetailHeader("Text without layout"));
        addRow(table, createDetailRow("L", textType.getL()));
        addRow(table, createTextLengthDetailRow(StringUtils.length(textType.getValue())));
        addRow(table, createDetailRow("Content value", textType.getValue()));

        return table;
    }

    private List<PdfPCell> createTextLengthDetailRow(Integer length) {
        List<PdfPCell> pdfPCells = toDetailRowIfHasValue("Content length", length);
        return pdfPCells;
    }

    private Collection<PdfPTable> createLnkTable(List<LnkType> lnkTypes) {
        return Optional.ofNullable(lnkTypes)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(this::lnkToTable)
                       .collect(Collectors.toList());
    }

    private Collection<PdfPTable> createTextWithoutLayoutTable(List<TextType> textTypes) {
        return Optional.ofNullable(textTypes)
                       .orElse(Collections.emptyList())
                       .stream()
                       .map(this::textWithoutLayoutToTable)
                       .collect(Collectors.toList());
    }

}