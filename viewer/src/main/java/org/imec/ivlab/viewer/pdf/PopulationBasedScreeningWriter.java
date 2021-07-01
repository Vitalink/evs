package org.imec.ivlab.viewer.pdf;

import static org.imec.ivlab.viewer.pdf.PdfHelper.writeToDocument;
import static org.imec.ivlab.viewer.pdf.TableHelper.addRow;
import static org.imec.ivlab.viewer.pdf.TableHelper.combineTables;
import static org.imec.ivlab.viewer.pdf.TableHelper.createDetailHeader;
import static org.imec.ivlab.viewer.pdf.TableHelper.createTitleTable;
import static org.imec.ivlab.viewer.pdf.TableHelper.initializeDetailTable;
import static org.imec.ivlab.viewer.pdf.TableHelper.toDetailRowIfHasValue;
import static org.imec.ivlab.viewer.pdf.TableHelper.toUnparsedContentTables;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.imec.ivlab.core.model.internal.parser.childprevention.BooleanItem;
import org.imec.ivlab.core.model.internal.parser.childprevention.DateItem;
import org.imec.ivlab.core.model.internal.parser.childprevention.TextItem;
import org.imec.ivlab.core.model.internal.parser.childprevention.YearItem;
import org.imec.ivlab.core.model.internal.parser.populationbasedscreening.PopulationBasedScreening;
import org.imec.ivlab.core.model.upload.KmehrWithReferenceList;
import org.imec.ivlab.core.model.upload.extractor.PopulationBasedScreeningExtractor;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
import org.imec.ivlab.core.util.IOUtils;
import org.imec.ivlab.viewer.converter.TestFileConverter;

public class PopulationBasedScreeningWriter extends Writer {

    public static void main(String[] args) {

        PopulationBasedScreeningWriter writer = new PopulationBasedScreeningWriter();
        Stream.of(
            "cervicalcancer",
            "coloncancer-all-fields",
            "coloncancer-all-fields-and-unparsed"
        )
              .forEach(filename -> writer.createPdf(readTestFile(filename + ".xml").get(0), filename + ".pdf"));

    }

    private static List<PopulationBasedScreening> readTestFile(String filename)  {
        File inputFile = IOUtils.getResourceAsFile("/populationbasedscreening/" + filename);

        KmehrEntryList kmehrEntryList = KmehrExtractor.getKmehrEntryList(inputFile);
        KmehrWithReferenceList childPreventionList = new PopulationBasedScreeningExtractor().getKmehrWithReferenceList(kmehrEntryList);

        return TestFileConverter.convertToPopulationBasedScreenings(childPreventionList);
    }

    public void createPdf(PopulationBasedScreening populationBasedScreening, String fileLocation) {

        String schemeTitle = "PopulationBasedScreening Visualization";

        PdfPTable generalInfoTable = createGeneralInfoTable(schemeTitle, populationBasedScreening.getHeader());
        List<PdfPTable> detailTables = createDetailTables(populationBasedScreening);

        writeToDocument(fileLocation, generalInfoTable, detailTables);
    }


    private List<PdfPTable> createDetailTables(PopulationBasedScreening populationBasedScreening) {

        List<PdfPTable> tables = new ArrayList<>();

        tables.add(combineTables(null, new ArrayList<>(), toUnparsedContentTables(Collections.singletonList(populationBasedScreening), null)));

        tables.add(combineTables(createTitleTable("Sender"), createHcPartyTables(populationBasedScreening.getHeader().getSender().getHcParties()), toUnparsedContentTables(null, "Sender")));
        tables.add(combineTables(createTitleTable("Recipient"), createHcPartyTables(populationBasedScreening.getHeader().getRecipients().stream().flatMap(recipient -> recipient.getHcParties().stream()).collect(Collectors.toList())), toUnparsedContentTables(null, "Sender")));
        tables.add(combineTables(createTitleTable("Patient"), patientToTable(populationBasedScreening.getTransactionCommon().getPerson()), toUnparsedContentTable(populationBasedScreening.getTransactionCommon().getPerson(), "Patient")));
        tables.add(combineTables(createTitleTable("Author"), createHcPartyTables(populationBasedScreening.getTransactionCommon().getAuthor()), toUnparsedContentTables(populationBasedScreening.getTransactionCommon().getAuthor(), "Author")));
        tables.add(combineTables(createTitleTable("Redactor"), createHcPartyTables(populationBasedScreening.getTransactionCommon().getRedactor()), toUnparsedContentTables(populationBasedScreening.getTransactionCommon().getRedactor(), "Redactor")));
        tables.add(combineTables(createTitleTable("Transaction metadata"), createTransactionMetadata(populationBasedScreening.getTransactionCommon()), toUnparsedContentTables(populationBasedScreening.getTransactionCommon().getAuthor(), "Author")));
        List<PdfPTable> tablesUnparsed = new ArrayList<>();
        tablesUnparsed.addAll(toUnparsedContentTable(populationBasedScreening.getScreeningYear(), "Screening year"));
        tablesUnparsed.addAll(toUnparsedContentTable(populationBasedScreening.getScreeningType(), "Screening type"));
        tablesUnparsed.addAll(toUnparsedContentTable(populationBasedScreening.getInvitationDate(), "Invitation date"));
        tablesUnparsed.addAll(toUnparsedContentTable(populationBasedScreening.getInvitationType(), "Invitation type"));
        tablesUnparsed.addAll(toUnparsedContentTable(populationBasedScreening.getInvitationLocationName(), "Invitation location name"));
        tablesUnparsed.addAll(toUnparsedContentTable(populationBasedScreening.getInvitationLocationAddress(), "Invitation location address"));
        tablesUnparsed.addAll(toUnparsedContentTable(populationBasedScreening.getParticipationDate(), "Participation date"));
        tablesUnparsed.addAll(toUnparsedContentTable(populationBasedScreening.getParticipationLocationName(), "Participation location name"));
        tablesUnparsed.addAll(toUnparsedContentTable(populationBasedScreening.getParticipationLocationAddress(), "Participation location address"));
        tablesUnparsed.addAll(toUnparsedContentTable(populationBasedScreening.getParticipationResult(), "Participation result"));
        tablesUnparsed.addAll(toUnparsedContentTable(populationBasedScreening.getFollowupNeeded(), "Followup needed"));
        tablesUnparsed.addAll(toUnparsedContentTable(populationBasedScreening.getFollowupAdvice(), "Followup advice"));
        tablesUnparsed.addAll(toUnparsedContentTable(populationBasedScreening.getFollowupApproved(), "Followup approved"));
        tablesUnparsed.addAll(toUnparsedContentTable(populationBasedScreening.getNextInvitationIndication(), "Next invitation indication"));

        tables.add(combineTables(createTitleTable("Population Based Screening"), createChildpreventionTables(populationBasedScreening), tablesUnparsed));
        return tables;

    }

    private List<PdfPTable> createChildpreventionTables(PopulationBasedScreening childPrevention) {


        PdfPTable table = initializeDetailTable();
        addRow(table, createDetailHeader("Childprevention details"));

        addRow(table, toDetailRowIfHasValue("Screening type", Optional.ofNullable(childPrevention.getScreeningType()).map(TextItem::getText).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Screening year", Optional.ofNullable(childPrevention.getScreeningYear()).map(YearItem::getYear).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Invitation date", Optional.ofNullable(childPrevention.getInvitationDate()).map(DateItem::getDate).map(Translator::formatAsDate).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Invitation type", Optional.ofNullable(childPrevention.getInvitationType()).map(TextItem::getText).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Invitation location name", Optional.ofNullable(childPrevention.getInvitationLocationName()).map(TextItem::getText).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Invitation location address", Optional.ofNullable(childPrevention.getInvitationLocationAddress()).map(TextItem::getText).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Participation date", Optional.ofNullable(childPrevention.getParticipationDate()).map(DateItem::getDate).map(Translator::formatAsDate).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Participation location name", Optional.ofNullable(childPrevention.getParticipationLocationName()).map(TextItem::getText).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Participation location address", Optional.ofNullable(childPrevention.getParticipationLocationAddress()).map(TextItem::getText).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Participation result", Optional.ofNullable(childPrevention.getParticipationResult()).map(TextItem::getText).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Followup needed", Optional.ofNullable(childPrevention.getFollowupNeeded()).map(BooleanItem::getValue).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Followup advice", Optional.ofNullable(childPrevention.getFollowupAdvice()).map(TextItem::getText).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Followup approved", Optional.ofNullable(childPrevention.getFollowupApproved()).map(BooleanItem::getValue).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Next invitation indication", Optional.ofNullable(childPrevention.getNextInvitationIndication()).map(TextItem::getText).orElse(null)));

        List<PdfPTable> tables = new ArrayList<>();
        tables.add(table);
        return tables;
    }

    protected static Font getValidationAnnotationFont() {
        Font font = new Phrase().getFont();
        font.setSize(8);
        font.setStyle(Font.NORMAL);
        font.setColor(BaseColor.WHITE);
        return font;
    }

}