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
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.model.internal.parser.ParsedItem;
import org.imec.ivlab.core.model.internal.parser.common.Header;
import org.imec.ivlab.core.model.internal.parser.diarynote.DiaryNote;
import org.imec.ivlab.core.model.internal.parser.sumehr.HcParty;
import org.imec.ivlab.core.model.upload.diarylist.DiaryNoteList;
import org.imec.ivlab.core.model.upload.diarylist.DiaryNoteListExtractor;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
import org.imec.ivlab.core.util.IOUtils;
import org.imec.ivlab.viewer.converter.TestFileConverter;

public class DiaryNoteWriter extends Writer {


    public static void main(String[] args) {

        DiaryNoteWriter diaryNoteWriter = new DiaryNoteWriter();
        Stream.of("diarynote-with-only-text-without-layout", "diarynote-with-only-text-with-layout", "diarynote-example-bert-3", "diarynote-example-rsb-recorddatetime-and-redactor")
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
        tables.add(combineTables(createTitleTable("Patient"), personToTable(diaryNote.getTransactionCommon().getPerson()), toUnparsedContentTable(diaryNote.getTransactionCommon().getPerson(), "Patient")));
        tables.add(combineTables(createTitleTable("Author"), createHcPartyTables(diaryNote.getTransactionCommon().getAuthors()), toUnparsedContentTables(diaryNote.getTransactionCommon().getAuthors(), "Author")));
        tables.add(combineTables(createTitleTable("Transaction metadata"), createTransactionMetadata(diaryNote), toUnparsedContentTables(diaryNote.getTransactionCommon().getAuthors(), "Author")));
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

        tables.addAll(createTextwithLayoutTable(diaryNote.getTextWithLayoutTypes()));
        tables.addAll(createTextWithoutLayoutTable(diaryNote.getTextTypes()));
        tables.addAll(createLnkTable(diaryNote.getLinkTypes()));

        return tables;
    }

    protected PdfPTable createTransactionMetadata(DiaryNote diaryNote) {

        PdfPTable table = initializeDetailTable();
        addRow(table, createDetailHeader("General information"));
        addRow(table, createDetailRow("CD-DIARY values", String.join(", ", diaryNote.getCdDiaryValues())));
        addRow(table, createDetailRow("Date", formatAsDate(diaryNote.getTransactionCommon().getDate())));
        addRow(table, createDetailRow("Time", formatAsTime(diaryNote.getTransactionCommon().getTime())));
        return table;

    }

    private PdfPTable lnkToTable(LnkType lnkType) {
        PdfPTable table = initializeDetailTable();

        addRow(table, createDetailHeader("Link"));
        addRow(table, createDetailRow("Type", Optional.ofNullable(lnkType.getTYPE()).map(CDLNKvalues::value).orElse(null)));
        addRow(table, createDetailRow("Mediatype", Optional.ofNullable(lnkType.getMEDIATYPE()).map(CDMEDIATYPEvalues::value).orElse(null)));
        addRow(table, createDetailRow("Size", lnkType.getSIZE()));
        addRow(table, createDetailRow("Url", lnkType.getURL()));

        return table;
    }

    private PdfPTable textWithoutLayoutToTable(TextType textType) {
        PdfPTable table = initializeDetailTable();

        addRow(table, createDetailHeader("Text without layout"));
        addRow(table, createDetailRow("L", textType.getL()));
        addRow(table, createDetailRow("Value", textType.getValue()));

        return table;
    }

    private PdfPTable textWithLayoutToTable(TextWithLayoutType textWithLayoutType) {
        PdfPTable table = initializeDetailTable();

        addRow(table, createDetailHeader("Text with layout"));
        addRow(table, createDetailRow("L", textWithLayoutType.getL()));
        List<String> contents = textWithLayoutType.getContent()
            .stream()
            .map(this::parseTextWithLayoutContent)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        addRow(table, createDetailRow("Content length", String.valueOf(contents.stream().map(String::length).mapToInt(Integer::intValue).sum())));
        addRow(table, createDetailRow("Content value", String.join("", contents)));

        return table;
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