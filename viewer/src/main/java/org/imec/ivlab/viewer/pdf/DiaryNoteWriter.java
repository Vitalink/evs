package org.imec.ivlab.viewer.pdf;

import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getCenteredCell;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getDefaultPhrase;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getDefaultPhraseBold;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getFrontPageHeaderPhrase;
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

import be.fgov.ehealth.standards.kmehr.cd.v1.CDLNKvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDMEDIATYPEvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.LnkType;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TextWithLayoutType;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.io.File;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.model.internal.parser.ParsedItem;
import org.imec.ivlab.core.model.internal.parser.common.Header;
import org.imec.ivlab.core.model.internal.parser.diarynote.DiaryNote;
import org.imec.ivlab.core.model.internal.parser.sumehr.HcParty;
import org.imec.ivlab.core.model.upload.diarylist.DiaryNoteList;
import org.imec.ivlab.core.model.upload.diarylist.DiaryNoteListExtractor;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.IOUtils;
import org.imec.ivlab.viewer.converter.TestFileConverter;

public class DiaryNoteWriter extends Writer {

    private static final Set<String> VITALINK_SUPPORTED_CD_DIARYNOTE_VALUES = new HashSet<>(Arrays.asList("diabetes", "nutrition", "movement", "medication", "renalinsufficiency", "woundcare"));
    private static final int TEXT_MESSAGE_MAX_LENGTH = 320;
    private static final String ANNOTATION_TEXT_NOT_SUPPORTED = "Not supported";
    private static final String ANNOTATION_TEXT_TEXT_MESSAGE_TOO_LONG = "Text content exceeds max length of " + TEXT_MESSAGE_MAX_LENGTH + " characters";

    public static void main(String[] args) {

        DiaryNoteWriter diaryNoteWriter = new DiaryNoteWriter();
        Stream.of("diarynote-with-only-text-without-layout", "diarynote-with-only-text-with-layout", "diarynote-example-b-3", "diarynote-example-rsb-recorddatetime-and-redactor-and-pact", "diarynote-with-redactor", "diarynote-example-b-3-with-unsupported-cddiarynote-values")
            .forEach(filename -> diaryNoteWriter.createPdf(readTestFile(filename + ".xml").get(0), filename + ".pdf"));

    }

    private static List<DiaryNote> readTestFile(String filename) {
        File inputFile = IOUtils.getResourceAsFile("/diarynote/" + filename);

        KmehrEntryList kmehrEntryList = KmehrExtractor.getKmehrEntryList(inputFile);
        DiaryNoteList diaryNoteList = null;
        try {
            diaryNoteList = DiaryNoteListExtractor.getDiaryList(kmehrEntryList);
        } catch (TransformationException e) {
            throw new RuntimeException(e);
        }

        return TestFileConverter.convertToDiaryNotes(diaryNoteList);
    }

    public void createPdf(DiaryNote diaryNote, String fileLocation) {

        String schemeTitle = "Diary Note Visualization";

        PdfPTable generalInfoTable = createGeneralInfoTable(schemeTitle, diaryNote.getHeader());
        List<PdfPTable> detailTables = createSumehrDetailTables(diaryNote);

        writeToDocument(fileLocation, generalInfoTable, detailTables);
    }


    private PdfPTable createGeneralInfoTable(String title, Header header) {

        PdfPTable table = new PdfPTable(20);
        table.setWidthPercentage(95);

        // the cell object
        PdfPCell cell;

        // title
        cell = getCenteredCell();
        cell.setPhrase(getFrontPageHeaderPhrase(title));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(20);
        cell.setPaddingBottom(30f);
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
        cell = new PdfPCell(getDefaultPhraseBold(formatAsDateTime(LocalDateTime.of(header.getDate(), header.getTime()))));
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

    private List<PdfPTable> createSumehrDetailTables(DiaryNote diaryNote) {

        List<PdfPTable> tables = new ArrayList<>();

        tables.add(combineTables(null, new ArrayList<>(), toUnparsedContentTables(Collections.singletonList(diaryNote), null)));

        tables.add(combineTables(createTitleTable("Sender"), createHcPartyTables(diaryNote.getHeader().getSender().getHcParties()), toUnparsedContentTables(null, "Sender")));
        tables.add(combineTables(createTitleTable("Recipient"), createHcPartyTables(diaryNote.getHeader().getRecipients().stream().flatMap(recipient -> recipient.getHcParties().stream()).collect(Collectors.toList())), toUnparsedContentTables(null, "Sender")));
        tables.add(combineTables(createTitleTable("Patient"), patientToTable(diaryNote.getTransactionCommon().getPerson()), toUnparsedContentTable(diaryNote.getTransactionCommon().getPerson(), "Patient")));
        tables.add(combineTables(createTitleTable("Author"), createHcPartyTables(diaryNote.getTransactionCommon().getAuthor()), toUnparsedContentTables(diaryNote.getTransactionCommon().getAuthor(), "Author")));
        tables.add(combineTables(createTitleTable("Redactor"), createHcPartyTables(diaryNote.getTransactionCommon().getRedactor()), toUnparsedContentTables(diaryNote.getTransactionCommon().getRedactor(), "Redactor")));
        tables.add(combineTables(createTitleTable("Transaction metadata"), createTransactionMetadata(diaryNote), toUnparsedContentTables(diaryNote.getTransactionCommon().getAuthor(), "Author")));
        tables.add(combineTables(createTitleTable("DiaryNote"), createDiaryNotetables(diaryNote), null));
        return tables;

    }

    private <T extends ParsedItem> List<PdfPTable> toUnparsedContentTable(T parsedItem, String topic) {
        ArrayList<T> list = new ArrayList<>();
        list.add(parsedItem);
        return toUnparsedContentTables(list, topic);
    }

    private List<PdfPTable> createDiaryNotetables(DiaryNote diaryNote) {
        List<PdfPTable> tables = new ArrayList<>();

        tables.addAll(createLnkTable(diaryNote.getLinkTypes()));
        tables.addAll(createTextwithLayoutTable(diaryNote.getTextWithLayoutTypes()));
        tables.addAll(createTextWithoutLayoutTable(diaryNote.getTextTypes()));

        return tables;
    }

    protected PdfPTable createTransactionMetadata(DiaryNote diaryNote) {

        PdfPTable table = initializeDetailTable();
        addRow(table, createDetailHeader("General information"));
        Optional.ofNullable(diaryNote.getCdDiaryValues()).orElse(Collections.emptyList())
            .forEach(cdDiaryValue -> addRow(table, createCdDiaryRow("cd CD-DIARY", cdDiaryValue)));
        Optional.ofNullable(diaryNote.getCdLocalEntries()).orElse(Collections.emptyList())
            .forEach(cdtransaction -> addRow(table, toDetailRowIfHasValue("cd LOCAL - " + cdtransaction.getDN(), cdtransaction.getValue())));
        addRow(table, createDetailRow("Date", formatAsDate(diaryNote.getTransactionCommon().getDate())));
        addRow(table, createDetailRow("Time", formatAsTime(diaryNote.getTransactionCommon().getTime())));
        addRow(table, toDetailRowIfHasValue("Record date time", formatAsDateTime(diaryNote.getTransactionCommon().getRecordDateTime())));
        return table;

    }

    private List<PdfPCell> createCdDiaryRow(String title, String cdDiaryValue) {
        List<PdfPCell> pdfPCells = toDetailRowIfHasValue(title, cdDiaryValue);
        if (CollectionsUtil.size(pdfPCells) == 2 && !isSupportedCdDiaryNoteValue(cdDiaryValue)) {
            annotateCellWithValidationMessage(pdfPCells.get(1), ANNOTATION_TEXT_NOT_SUPPORTED);
        }
        return pdfPCells;
    }

    private void annotateCellWithValidationMessage(PdfPCell pdfPCell, String message) {
        annotateCell(pdfPCell, message, BaseColor.RED, getValidationAnnotationFont());
    }

    private void annotateCell(PdfPCell pdfPCell, String annotationText, BaseColor colour, Font font) {
        if (pdfPCell == null) {
            return;
        }
        Chunk chunkSpace = new Chunk(" ");
        Chunk chunkAnnotation = new Chunk("[" + annotationText + "]").setBackground(colour);
        chunkAnnotation.setFont(font);
        pdfPCell.getPhrase().add(chunkSpace);
        pdfPCell.getPhrase().add(chunkAnnotation);
    }

    protected static Font getValidationAnnotationFont() {
        Font font = new Phrase().getFont();
        font.setSize(8);
        font.setStyle(Font.NORMAL);
        font.setColor(BaseColor.WHITE);
        return font;
    }

    private boolean isSupportedCdDiaryNoteValue(String cdDiaryNoteValue) {
        return cdDiaryNoteValue == null || VITALINK_SUPPORTED_CD_DIARYNOTE_VALUES.contains(StringUtils.lowerCase(cdDiaryNoteValue));
    }

    private boolean isValidTextualMessage(Integer textLength) {
        return textLength <= TEXT_MESSAGE_MAX_LENGTH;
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
        if (CollectionsUtil.size(pdfPCells) == 2 && !isValidTextualMessage(length)) {
            annotateCellWithValidationMessage(pdfPCells.get(1), ANNOTATION_TEXT_TEXT_MESSAGE_TOO_LONG);
        }
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

    private Collection<PdfPTable> createTextwithLayoutTable(List<TextWithLayoutType> textTypes) {
        return Optional.ofNullable(textTypes)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::textWithLayoutToTable)
            .collect(Collectors.toList());
    }

    private Collection<PdfPTable> createHcPartyTables(List<HcParty> hcParties) {
        return Optional.ofNullable(hcParties)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::hcpartyTypeToTable)
            .collect(Collectors.toList());
    }

}