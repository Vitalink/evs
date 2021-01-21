package org.imec.ivlab.viewer.pdf;

import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getCenteredCell;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getDefaultPhrase;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getDefaultPhraseBold;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getFrontPageHeaderPhrase;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getHeaderCellLeftAligned;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getLeftAlignedCell;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getMedicationHeaderCell;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getMedicationHeaderPhrase;
import static org.imec.ivlab.viewer.pdf.PdfHelper.writeToDocument;
import static org.imec.ivlab.viewer.pdf.TableHelper.addRow;
import static org.imec.ivlab.viewer.pdf.TableHelper.createDetailHeader;
import static org.imec.ivlab.viewer.pdf.TableHelper.createDetailRow;
import static org.imec.ivlab.viewer.pdf.TableHelper.initializeDetailTable;
import static org.imec.ivlab.viewer.pdf.TableHelper.toDetailRowIfHasValue;
import static org.imec.ivlab.viewer.pdf.Translator.formatAsDate;
import static org.imec.ivlab.viewer.pdf.Translator.formatAsDateTime;
import static org.imec.ivlab.viewer.pdf.Translator.formatAsTime;
import static org.imec.ivlab.viewer.pdf.VaccinationHelper.getMedicinalIntendedCnks;
import static org.imec.ivlab.viewer.pdf.VaccinationHelper.getMedicinalIntendedNames;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDDRUGCNK;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDDRUGCNKschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDINNCLUSTER;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLNKvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDMEDIATYPEvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDUNIT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDUNITschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.LnkType;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TextWithLayoutType;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.util.TextTypeUtil;
import org.imec.ivlab.core.model.internal.parser.vaccination.Vaccination;
import org.imec.ivlab.core.model.internal.parser.vaccination.VaccinationItem;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
import org.imec.ivlab.core.model.upload.vaccinationentry.VaccinationListExtractor;
import org.imec.ivlab.core.util.IOUtils;
import org.imec.ivlab.core.vaccination.VaccinationEnricher;
import org.imec.ivlab.viewer.converter.TestFileConverter;

public class VaccinationListWriter extends Writer {

    public static final int TABLE_WIDTH_PERCENTAGE = 95;

    public static void main(String[] args) {
        VaccinationListWriter vaccinationWriter = new VaccinationListWriter();
        List<String> fileNames =
            Stream
                .of("vaccination-with-medicinal-product", "vaccination-with-cdatc-and-batch", "vaccination-no-quantity", "vaccination-with-substance-product", "vaccination-with-unparsable-content", "vaccination-with-vaccinnetcode")
                .map(name -> name + ".xml")
                .collect(Collectors.toList());
        vaccinationWriter.createPdf(readTestFiles(fileNames), "vaccination-overview.pdf");
    }

    private static List<Vaccination> readTestFiles(List<String> filenames) {
        return filenames
            .stream()
            .map(filename -> "/vaccination/" + filename)
            .map(IOUtils::getResourceAsFile)
            .map(KmehrExtractor::getKmehrEntryList)
            .map(VaccinationListExtractor::getVaccinationList)
            .map(TestFileConverter::convertTVaccinations)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    public void createPdf(List<Vaccination> vaccinations, String fileLocation) {

        String schemeTitle = "Vaccination List Visualization";

        PdfPTable generalInfoTable = createGeneralInfoTable(schemeTitle);
        PdfPTable detailTable = createDetailTable(vaccinations);

        writeToDocument(fileLocation, generalInfoTable, Collections.singletonList(detailTable));
    }


    private PdfPTable createGeneralInfoTable(String title) {

        PdfPTable table = new PdfPTable(20);
        table.setWidthPercentage(TABLE_WIDTH_PERCENTAGE);

        // the cell object
        PdfPCell cell;

        // title
        cell = getCenteredCell();
        cell.setPhrase(getFrontPageHeaderPhrase(title));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(20);
        table.addCell(cell);

        cell = new PdfPCell(getFrontPageHeaderPhrase(" "));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(14);
        table.addCell(cell);

        cell = new PdfPCell(getDefaultPhrase("Afdruk op: "));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(3);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        cell = new PdfPCell(getDefaultPhraseBold(formatAsDateTime(LocalDateTime.now())));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(3);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(getFrontPageHeaderPhrase(" "));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(20);
        table.addCell(cell);

        return table;

    }


    private PdfPTable createDetailTable(List<Vaccination> vaccinations) {

        int numColumns = 45;

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(95);
        table.setHeaderRows(1);

        table.addCell(createHeaderRow("VACCINATIONS", numColumns));
        table.addCell(createVaccinationDetailHeaderRow(numColumns));

        Optional.ofNullable(vaccinations)
                .orElse(Collections.emptyList())
                .stream()
                .flatMap(vaccination -> vaccination.getVaccinationItems().stream())
                .sorted(Comparator.comparing(VaccinationItem::getBeginMoment).reversed())
                .map(entry -> createVaccinationDetailDataRow(entry, numColumns))
                .forEach(table::addCell);

        return table;

    }

    private PdfPTable createHeaderRow(String title, int numColumns) {
        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(TABLE_WIDTH_PERCENTAGE);

        PdfPCell cell = getMedicationHeaderCell();
        cell.setPhrase(getMedicationHeaderPhrase(title));
        cell.setColspan(numColumns);
        table.addCell(cell);

        return table;
    }

    private PdfPTable createVaccinationDetailHeaderRow(int numColumns) {
        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(TABLE_WIDTH_PERCENTAGE);

        table.addCell(createHeaderCell("Application date", 5));
        table.addCell(createHeaderCell("Vaccine code", 10));
        table.addCell(createHeaderCell("Vaccine name", 15));
        table.addCell(createHeaderCell("Protects against", 15));

        return table;
    }

    private PdfPTable createVaccinationDetailDataRow(VaccinationItem vaccinationItem, int numColumns) {
        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(TABLE_WIDTH_PERCENTAGE);

        String code = null;
        String name = null;
        String protectsAgainst = null;

        List<CDDRUGCNK> intendedCnks = getMedicinalIntendedCnks(vaccinationItem).stream().filter(intendedMedicinalCnk -> intendedMedicinalCnk.getS().equals(CDDRUGCNKschemes.CD_DRUG_CNK)).collect(Collectors.toList());
        List<String> intendedNames = getMedicinalIntendedNames(vaccinationItem);
        List<CDCONTENT> contentCodesForAtc = vaccinationItem
            .getCdcontents()
            .stream()
            .filter(cdcontent -> cdcontent.getS().equals(CDCONTENTschemes.CD_ATC))
            .collect(Collectors.toList());
        List<CDCONTENT> contentCodesForVaccinnet = vaccinationItem
            .getCdcontents()
            .stream()
            .filter(cdcontent -> cdcontent
                .getS()
                .equals(CDCONTENTschemes.LOCAL))
            .filter(cdcontent -> cdcontent
                .getSL()
                .equals("VACCINNETCODE"))
            .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(intendedCnks)) {
            code = StringUtils.joinWith(System.lineSeparator(), intendedCnks
                .stream()
                .map(cddrugcnk -> "CNK: " + cddrugcnk.getValue())
                .toArray());
            name = StringUtils.joinWith(System.lineSeparator(), intendedNames.toArray());
            protectsAgainst = StringUtils.join(intendedCnks.stream().map(entry -> VaccinationEnricher.getProtectsAgainstByCnk(entry.getValue())).toArray(), System.lineSeparator());
        } if (CollectionUtils.isNotEmpty(contentCodesForAtc)) {
            code = StringUtils.joinWith(System.lineSeparator(), contentCodesForAtc
                .stream()
                .map(cdcontent -> "ATC: " + cdcontent.getValue())
                .toArray());
            name = TextTypeUtil.toStrings(vaccinationItem.getTextTypes()).stream().collect(Collectors.joining(System.lineSeparator()));
            protectsAgainst = StringUtils.join(contentCodesForAtc.stream().map(entry -> VaccinationEnricher.getProtectsAgainstByAtc(entry.getValue())).toArray(), System.lineSeparator());
        } else if (CollectionUtils.isNotEmpty(contentCodesForVaccinnet)) {
            code = StringUtils.joinWith(System.lineSeparator(), contentCodesForVaccinnet
                .stream()
                .map(cdcontent -> "VACCINNET: " + cdcontent.getValue())
                .toArray());
            name = TextTypeUtil.toStrings(vaccinationItem.getTextTypes()).stream().collect(Collectors.joining(System.lineSeparator()));
            protectsAgainst = StringUtils.join(contentCodesForVaccinnet.stream().map(entry -> VaccinationEnricher.getProtectsAgainstByVaccinnetCode(entry.getValue())).toArray(), System.lineSeparator());
        }

        table.addCell(createContentCell(formatAsDate(vaccinationItem.getBeginMoment()), 5));
        table.addCell(createContentCell(code, 10));
        table.addCell(createContentCell(name, 15));
        table.addCell(createContentCell(protectsAgainst, 15));

        return table;
    }

    private String getCode(CDDRUGCNK cddrugcnk) {
        return StringUtils.joinWith(
            ": ",
            StringUtils.replace(Optional.ofNullable(cddrugcnk).map(CDDRUGCNK::getS).map(CDDRUGCNKschemes::value).orElse(null), "CD-", "")
            ,Optional.ofNullable(cddrugcnk).map(CDDRUGCNK::getValue).orElse(null));
    }

    private PdfPTable createVaccinationDetailsTable(VaccinationItem vaccinationItem) {
        PdfPTable table = initializeDetailTable();
        addRow(table, createDetailHeader("Vaccination details"));

        vaccinationItem.getCdcontents().forEach(cdContent -> addRow(table, createCdContentRow(cdContent)));

        addRow(table, toDetailRowIfHasValue("Application date", formatAsDate(vaccinationItem.getBeginMoment())));

        addRow(table, toDetailRowIfHasValue("Lifecycle", Optional.of(vaccinationItem).map(VaccinationItem::getLifecycle).map(CDLIFECYCLEvalues::value).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Text", StringUtils.joinWith(System.lineSeparator(), TextTypeUtil.toStrings(vaccinationItem.getTextTypes()).toArray())));
        addRow(table, toDetailRowIfHasValue("Quantity", Optional.ofNullable(vaccinationItem.getQuantity()).map(BigDecimal::toString).orElse(null)));
        addRow(table, createQuantityUnitRow(vaccinationItem.getQuantityUnit(), "Unit"));
        addRow(table, toDetailRowIfHasValue("Batch", vaccinationItem.getBatch()));
        return table;
    }

    private PdfPCell createHeaderCell(String content, int colspan) {
        PdfPCell cell = getHeaderCellLeftAligned();
        cell.setPhrase(getMedicationHeaderPhrase(content));
        cell.setColspan(colspan);
        cell.setRowspan(1);
        cell.setBorder(0);
        cell.setIndent(0);
        return cell;
    }

    private PdfPCell createContentCell(String content, int colspan) {
        PdfPCell cell = getLeftAlignedCell();
        cell.setPhrase(getDefaultPhrase(content));
        cell.setColspan(colspan);
        cell.setRowspan(1);
        cell.setBorder(0);
        return cell;
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

    private PdfPTable textWithLayoutToTable(TextWithLayoutType textWithLayoutType) {
        PdfPTable table = initializeDetailTable();

        addRow(table, createDetailHeader("Text with layout"));
        addRow(table, createDetailRow("L", textWithLayoutType.getL()));
        List<String> contents = textWithLayoutType.getContent()
                                                  .stream()
                                                  .map(this::parseTextWithLayoutContent)
                                                  .filter(Objects::nonNull)
                                                  .map(this::removeXmlTags)
                                                  .collect(Collectors.toList());
        int textlength = contents.stream().map(String::length).mapToInt(Integer::intValue).sum();
        addRow(table, createTextLengthDetailRow(textlength));
        addRow(table, createDetailRow("Content value", String.join("", contents)));

        return table;
    }

    private String removeXmlTags(String input) {
        String patternForXmlTags = "<[\\w\\/\\s\\:\\=\\\"\\.]+>";
        Pattern r = Pattern.compile(patternForXmlTags);
        Matcher matcher = r.matcher(input);

        return matcher.replaceAll("");
    }

}