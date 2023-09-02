package org.imec.ivlab.viewer.pdf;

import static org.imec.ivlab.viewer.pdf.PdfHelper.writeToDocument;
import static org.imec.ivlab.viewer.pdf.TableHelper.addRow;
import static org.imec.ivlab.viewer.pdf.TableHelper.combineTables;
import static org.imec.ivlab.viewer.pdf.TableHelper.createDetailHeader;
import static org.imec.ivlab.viewer.pdf.TableHelper.createTitleTable;
import static org.imec.ivlab.viewer.pdf.TableHelper.initializeDetailTable;
import static org.imec.ivlab.viewer.pdf.TableHelper.toDetailRowIfHasValue;
import static org.imec.ivlab.viewer.pdf.TableHelper.toUnparsedContentTables;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDUNIT;
import be.fgov.ehealth.standards.kmehr.schema.v1.UnitType;
import com.itextpdf.text.pdf.PdfPTable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.model.internal.parser.childprevention.BooleanItem;
import org.imec.ivlab.core.model.internal.parser.childprevention.ChildPrevention;
import org.imec.ivlab.core.model.internal.parser.childprevention.DurationItem;
import org.imec.ivlab.core.model.internal.parser.childprevention.TextItem;
import org.imec.ivlab.core.model.upload.KmehrWithReferenceList;
import org.imec.ivlab.core.model.upload.extractor.ChildPreventionExtractor;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
import org.imec.ivlab.core.util.IOUtils;
import org.imec.ivlab.viewer.converter.TestFileConverter;

public class ChildPreventionWriter extends Writer {

    public static void main(String[] args) {

        ChildPreventionWriter writer = new ChildPreventionWriter();
        Stream.of(
            "childprevention-only-pregnancy",
            "childprevention-all-types",
            "childprevention-all-types-and-unparsed"
        )
              .forEach(filename -> writer.createPdf(readTestFile(filename + ".xml").get(0), filename + ".pdf"));

    }

    private static List<ChildPrevention> readTestFile(String filename)  {
        File inputFile = IOUtils.getResourceAsFile("/childprevention/" + filename);

        KmehrEntryList kmehrEntryList = KmehrExtractor.getKmehrEntryList(inputFile);
        KmehrWithReferenceList childPreventionList = new ChildPreventionExtractor().getKmehrWithReferenceList(kmehrEntryList);

        return TestFileConverter.convertToChildPreventions(childPreventionList);
    }

    public void createPdf(ChildPrevention childPrevention, String fileLocation) {

        String schemeTitle = "ChildPrevention Visualization";

        PdfPTable generalInfoTable = createGeneralInfoTable(schemeTitle, childPrevention.getHeader());
        List<PdfPTable> detailTables = createDetailTables(childPrevention);

        writeToDocument(fileLocation, generalInfoTable, detailTables);
    }

    private List<PdfPTable> createDetailTables(ChildPrevention childPrevention) {

        List<PdfPTable> tables = new ArrayList<>();

        tables.add(combineTables(null, new ArrayList<>(), toUnparsedContentTables(Collections.singletonList(childPrevention), null)));

        tables.add(combineTables(createTitleTable("Sender"), createHcPartyTables(childPrevention.getHeader().getSender().getHcParties()), toUnparsedContentTables(null, "Sender")));
        tables.add(combineTables(createTitleTable("Recipient"), createHcPartyTables(childPrevention.getHeader().getRecipients().stream().flatMap(recipient -> recipient.getHcParties().stream()).collect(Collectors.toList())), toUnparsedContentTables(null, "Sender")));
        tables.add(combineTables(createTitleTable("Patient"), patientToTable(childPrevention.getTransactionCommon().getPerson()), toUnparsedContentTable(childPrevention.getTransactionCommon().getPerson(), "Patient")));
        tables.add(combineTables(createTitleTable("Author"), createHcPartyTables(childPrevention.getTransactionCommon().getAuthor()), toUnparsedContentTables(childPrevention.getTransactionCommon().getAuthor(), "Author")));
        tables.add(combineTables(createTitleTable("Redactor"), createHcPartyTables(childPrevention.getTransactionCommon().getRedactor()), toUnparsedContentTables(childPrevention.getTransactionCommon().getRedactor(), "Redactor")));
        tables.add(combineTables(createTitleTable("Transaction metadata"), createTransactionMetadata(childPrevention.getTransactionCommon()), toUnparsedContentTables(childPrevention.getTransactionCommon().getAuthor(), "Author")));
        List<PdfPTable> tablesUnparsed = new ArrayList<>();
        tablesUnparsed.addAll(toUnparsedContentTable(childPrevention.getResultHearingScreeningLeft(), "ResultHearingSreeningLeft"));
        tablesUnparsed.addAll(toUnparsedContentTable(childPrevention.getResultHearingScreeningRight(), "ResultHearingSreeningRight"));
        tablesUnparsed.addAll(toUnparsedContentTable(childPrevention.getBacterialMeningitis(), "BacterialMeningitis"));
        tablesUnparsed.addAll(toUnparsedContentTable(childPrevention.getPregnancyDuration(), "PregnancyDuration"));
        tablesUnparsed.addAll(toUnparsedContentTable(childPrevention.getRefusalHearingScreening(), "RefusalHearingScreening"));
        tablesUnparsed.addAll(toUnparsedContentTable(childPrevention.getSevereHeadTrauma(), "SevereHeadTrauma"));
        tablesUnparsed.addAll(toUnparsedContentTable(childPrevention.getPregnancyCMVInfection(), "PregnancyCMVInfection"));
        tables.add(combineTables(createTitleTable("Childprevention"), createChildpreventionTables(childPrevention), tablesUnparsed));
        return tables;

    }

    private List<PdfPTable> createChildpreventionTables(ChildPrevention childPrevention) {


        PdfPTable table = initializeDetailTable();
        addRow(table, createDetailHeader("Childprevention details"));

        addRow(table, toDetailRowIfHasValue("Pregnancy duration in weeks", parseDuration(childPrevention.getPregnancyDuration())));
        addRow(table, toDetailRowIfHasValue("Refusal hearing screening", Optional.ofNullable(childPrevention.getRefusalHearingScreening()).map(BooleanItem::getValue).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Result neonatal hearing screening left", Optional.ofNullable(childPrevention.getResultHearingScreeningLeft()).map(TextItem::getText).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Result neonatal hearing screening right", Optional.ofNullable(childPrevention.getResultHearingScreeningRight()).map(TextItem::getText).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Pregnancy CMV infection", Optional.ofNullable(childPrevention.getPregnancyCMVInfection()).map(BooleanItem::getValue).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Bacterial meningitis", Optional.ofNullable(childPrevention.getBacterialMeningitis()).map(BooleanItem::getValue).orElse(null)));
        addRow(table, toDetailRowIfHasValue("Severe head trauma", Optional.ofNullable(childPrevention.getSevereHeadTrauma()).map(BooleanItem::getValue).orElse(null)));

        List<PdfPTable> tables = new ArrayList<>();
        tables.add(table);
        return tables;
    }

    private String parseDuration(DurationItem pregnancyDuration) {
        if (pregnancyDuration == null || (pregnancyDuration.getUnit() == null && pregnancyDuration.getValue() == null)) {
            return null;
        } else {
            return StringUtils.joinWith(" ", pregnancyDuration.getValue(), Optional.ofNullable(pregnancyDuration.getUnit()).map(UnitType::getCd).map(CDUNIT::getValue).orElse(null));
        }
    }

    @Override
    protected boolean isSupported(CDTRANSACTION cdtransaction) {
        return true;
    }
}