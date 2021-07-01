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
import static org.imec.ivlab.viewer.pdf.Translator.formatAsDate;
import static org.imec.ivlab.viewer.pdf.Translator.formatAsDateTime;
import static org.imec.ivlab.viewer.pdf.VaccinationHelper.getMedicinalIntendedCnks;
import static org.imec.ivlab.viewer.pdf.VaccinationHelper.getMedicinalIntendedNames;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDDRUGCNK;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDDRUGCNKschemes;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.util.TextTypeUtil;
import org.imec.ivlab.core.model.internal.parser.vaccination.Vaccination;
import org.imec.ivlab.core.model.internal.parser.vaccination.VaccinationItem;
import org.imec.ivlab.core.model.upload.extractor.VaccinationListExtractor;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
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
            .map(kmehrEntryList -> new VaccinationListExtractor().getKmehrWithReferenceList(kmehrEntryList))
            .map(TestFileConverter::convertToVaccinations)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    public void createPdf(List<Vaccination> vaccinations, String fileLocation) {

        String schemeTitle = "Vaccination List Visualization";

        PdfPTable generalInfoTable = createGeneralInfoTable(schemeTitle);
        PdfPTable detailTable = createDetailTable(vaccinations);

        writeToDocument(fileLocation, generalInfoTable, Collections.singletonList(detailTable));
    }

    protected PdfPTable createGeneralInfoTable(String title) {

        PdfPTable table = new PdfPTable(20);
        table.setWidthPercentage(VaccinationListWriter.TABLE_WIDTH_PERCENTAGE);

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

    protected static Font getValidationAnnotationFont() {
        Font font = new Phrase().getFont();
        font.setSize(8);
        font.setStyle(Font.NORMAL);
        font.setColor(BaseColor.WHITE);
        return font;
    }

}