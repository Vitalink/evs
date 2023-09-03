package org.imec.ivlab.viewer.pdf;

import static org.imec.ivlab.core.util.StringUtils.joinFields;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getCenteredCell;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getDefaultPhrase;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getDefaultPhraseBold;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getFrontPageHeaderPhrase;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getMedicationHeaderCell;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getMedicationHeaderPhrase;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getMedicationObsoletePhrase;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getMedicationSubHeaderCell;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getMedicationSubHeaderPhrase;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getObsoleteMedicationCellNotObsolete;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getObsoleteMedicationCellObsolete;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getQuantityPhrase;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getQuantityWithValueCell;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getSuspensionHeaderCell;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getSuspensionHeaderPhrase;
import static org.imec.ivlab.viewer.pdf.PdfHelper.writeToDocument;
import static org.imec.ivlab.viewer.pdf.TakeTimeManager.MAX_NUMBER_OF_STANDALONE_TAKING_TIMES;
import static org.imec.ivlab.viewer.pdf.Translator.durationToString;
import static org.imec.ivlab.viewer.pdf.Translator.formatAsDate;
import static org.imec.ivlab.viewer.pdf.Translator.formatAsDateTime;
import static org.imec.ivlab.viewer.pdf.Translator.formatAsTime;
import static org.imec.ivlab.viewer.pdf.Translator.toCommentHeaderAndValueChunk;
import static org.imec.ivlab.viewer.pdf.Translator.toHCPartyChunks;
import static org.imec.ivlab.viewer.pdf.Translator.toLocalIdChunks;
import static org.imec.ivlab.viewer.pdf.Translator.translateAdministrationUnit;
import static org.imec.ivlab.viewer.pdf.Translator.translateDayperiod;
import static org.imec.ivlab.viewer.pdf.Translator.translateFrequency;
import static org.imec.ivlab.viewer.pdf.Translator.translateLifecycle;
import static org.imec.ivlab.viewer.pdf.Translator.translateQuantity;
import static org.imec.ivlab.viewer.pdf.Translator.translateRegimenRepetition;
import static org.imec.ivlab.viewer.pdf.Translator.translateRoute;
import static org.imec.ivlab.viewer.pdf.Translator.translateTemporality;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTEMPORALITYvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.RangeChecker;
import org.imec.ivlab.core.data.PatientKey;
import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.kmehr.model.FrequencyCode;
import org.imec.ivlab.core.kmehr.model.localid.LocalId;
import org.imec.ivlab.core.model.internal.mapper.medication.AbstractScheme;
import org.imec.ivlab.core.model.internal.mapper.medication.DailyScheme;
import org.imec.ivlab.core.model.internal.mapper.medication.Dayperiod;
import org.imec.ivlab.core.model.internal.mapper.medication.Duration;
import org.imec.ivlab.core.model.internal.mapper.medication.GlobalScheme;
import org.imec.ivlab.core.model.internal.mapper.medication.MedicationEntry;
import org.imec.ivlab.core.model.internal.mapper.medication.Posology;
import org.imec.ivlab.core.model.internal.mapper.medication.Regimen;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenDayperiod;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenEntry;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenTime;
import org.imec.ivlab.core.model.internal.mapper.medication.Suspension;
import org.imec.ivlab.core.model.patient.PatientReader;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.core.model.upload.msentrylist.MedicationSchemeExtractor;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.IOUtils;
import org.imec.ivlab.core.util.compare.NumberAwareStringComparator;
import org.imec.ivlab.viewer.converter.TestFileConverter;
import org.imec.ivlab.viewer.converter.exceptions.SchemaConversionException;

public class MSWriter extends Writer {

    private final static Logger LOG = LogManager.getLogger(MSWriter.class);

    private static PdfPTable table;
    private static TakeTimeManager takeTimeManager;
    private static DayperiodTakeManager dayperiodTakeManager;
    private static RangeChecker rangeChecker;

    private static final int MAX_LENGTH_TEXT_FIElDS = 1000;
    public static final String TOO_LARGE_TEXT = "[TOO LARGE]";
    private static float REMARKS_CONTENT_LEADING = 1.35f;

    public static void main(String[] args)
            throws SchemaConversionException, TransformationException {
        new MSWriter().createPdf(getTestScheme(), "global-medication-scheme.pdf");
    }

    public MSWriter() {
        rangeChecker = new RangeChecker();
    }

    private static AbstractScheme getTestScheme() throws TransformationException {
        File inputFile = IOUtils.getResourceAsFile("/medicationscheme/multiple-medication-entries.xml");

        KmehrEntryList kmehrEntryList = KmehrExtractor.getKmehrEntryList(inputFile);
        MSEntryList msEntryList = MedicationSchemeExtractor.getMedicationSchemeEntries(kmehrEntryList);

        List<MedicationEntry> medicationEntries = TestFileConverter.convertToMedicationEntries(msEntryList);

        GlobalScheme scheme = new GlobalScheme();
        scheme.setMedicationEntries(medicationEntries);

        Patient patient = PatientReader.loadPatientByKey(PatientKey.PATIENT_EXAMPLE.getValue());
        scheme.setPatient(patient);

        scheme.setVersion("313");

        List<HcpartyType> authors = new ArrayList<>();
        HcpartyType author = new HcpartyType();
        author.setFirstname("Jane");
        author.setFamilyname("DOE");
        authors.add(author);
        scheme.setAuthors(authors);

        return scheme;
    }

    /**
     * Creates a PDF
     * @param    fileLocation the name of the PDF file that will be created.
     * @throws    DocumentException 
     * @throws    IOException
     */
    public void createPdf(AbstractScheme scheme, String fileLocation) throws SchemaConversionException {

        String schemeTitle;
        if (scheme instanceof DailyScheme) {
            DailyScheme dailyScheme = (DailyScheme) scheme;
            DateTimeFormatter pt =  DateTimeFormat.forPattern("EEEE dd MMMM yyyy");
            schemeTitle = "Medicatie dagschema voor " + (dailyScheme.getSchemeDate() != null ? pt.print(dailyScheme.getSchemeDate()) : "");
        } else if (scheme instanceof GlobalScheme) {
            schemeTitle = "Medicatie overzichtschema";
        } else {
            throw new RuntimeException("Scheme type not supported yet for following class: " + scheme.getClass().getName());
        }

        PdfPTable generalInfoTable = createGeneralInfoTable(scheme, schemeTitle);
        List<PdfPTable> detailTables = createMedicationTables(scheme);

        writeToDocument(fileLocation, generalInfoTable, detailTables);
    }

    public static PdfPTable createGeneralInfoTable(AbstractScheme scheme, String title) {

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

        // general info
        cell = new PdfPCell(getDefaultPhrase("Patiënt"));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(8);
        table.addCell(cell);
        cell = new PdfPCell(getDefaultPhrase("Laatst gewijzigd door: "));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setColspan(3);
        table.addCell(cell);
        cell = new PdfPCell(getDefaultPhraseBold(formatAuthors(scheme.getAuthors())));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(3);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        cell = new PdfPCell(getDefaultPhrase("Versie: "));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setColspan(3);
        table.addCell(cell);
        cell = new PdfPCell(getDefaultPhraseBold(Optional.ofNullable(scheme.getVersion()).orElse("0")));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setColspan(3);
        table.addCell(cell);

        cell = new PdfPCell(getFrontPageHeaderPhrase(scheme.getPatient().getFirstName()));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(8);
        table.addCell(cell);
        cell = new PdfPCell(getDefaultPhrase("Laatst gewijzigd op: "));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(3);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        cell = new PdfPCell(getDefaultPhraseBold(StringUtils.joinWith(" ", formatAsDate(scheme.getLastModifiedDate()), formatAsTime(scheme.getLastModifiedTime()))));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setColspan(3);
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

        cell = new PdfPCell(getFrontPageHeaderPhrase(scheme.getPatient().getLastName()));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(8);
        table.addCell(cell);
        cell = new PdfPCell(getFrontPageHeaderPhrase(""));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(6);
        table.addCell(cell);
        cell = new PdfPCell(getDefaultPhrase("# MSE transacties: "));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(3);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        table.addCell(cell);
        cell = new PdfPCell(getDefaultPhraseBold(String.valueOf(scheme.getMedicationCount())));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(3);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        table.addCell(cell);

        cell = new PdfPCell(getDefaultPhrase(scheme.getPatient().getId()));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(8);
        table.addCell(cell);
        cell = new PdfPCell(getFrontPageHeaderPhrase(""));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(6);
        table.addCell(cell);
        cell = new PdfPCell(getDefaultPhrase("# TS transacties: "));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(3);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(cell);
        cell = new PdfPCell(getDefaultPhraseBold(String.valueOf(scheme.getSuspensionsCount())));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(3);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(cell);

        cell = new PdfPCell(getFrontPageHeaderPhrase(" "));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(20);
        table.addCell(cell);

        return table;

    }

    private static String formatAuthors(List<HcpartyType> authors) {
        if (CollectionsUtil.emptyOrNull(authors)) {
            return "";
        }

        ArrayList<String> authorStrings = new ArrayList<>();
        for (HcpartyType author : authors) {
            authorStrings.add(org.imec.ivlab.core.util.StringUtils.joinWith(" ", author.getFirstname(), author.getFamilyname(), author.getName()));
        }

        return StringUtils.joinWith(System.lineSeparator(), authorStrings.toArray());

    }

    private static List<RegimenEntry> collectRegimentEntries(List<MedicationEntry> medicationEntries) {

        List<RegimenEntry> regimenEntries = new ArrayList<>();

        if (medicationEntries == null) {
            return regimenEntries;
        }

        for (MedicationEntry medicationEntry : medicationEntries) {
            if (medicationEntry.getPosologyOrRegimen() instanceof Regimen) {
                regimenEntries.addAll(((Regimen) medicationEntry.getPosologyOrRegimen()).getEntries());
            }
        }

        return regimenEntries;

    }

    private static TreeMap<CDTEMPORALITYvalues, List<MedicationEntry>> groupMedicationEntriesByTemporality(List<MedicationEntry> medicationEntries) {

        List<CDTEMPORALITYvalues> definedOrder = Arrays.asList(CDTEMPORALITYvalues.CHRONIC, CDTEMPORALITYvalues.ACUTE, CDTEMPORALITYvalues.ONESHOT, null);

        Comparator<CDTEMPORALITYvalues> comparator = new Comparator<CDTEMPORALITYvalues>() {


            @Override
            public int compare(CDTEMPORALITYvalues o1, CDTEMPORALITYvalues o2) {

                return Integer.valueOf(definedOrder.indexOf(o1)).compareTo(definedOrder.indexOf(o2));
            }
        };

        TreeMap<CDTEMPORALITYvalues, List<MedicationEntry>> map = new TreeMap<>(comparator);


        if (medicationEntries == null) {
            return map;
        }

        for (MedicationEntry medicationEntry : medicationEntries) {
            if (map.containsKey(medicationEntry.getTemporality())) {
                map.get(medicationEntry.getTemporality()).add(medicationEntry);
            } else {
                map.put(medicationEntry.getTemporality(), new ArrayList<MedicationEntry>());
                map.get(medicationEntry.getTemporality()).add(medicationEntry);
            }
        }

        return map;

    }

    private static int getNumberOfColumns(boolean globalScheme) {
        if (globalScheme) {
            return 49;
        } else {
            return 48;
        }
    }

    private static PdfPTable createMedicationTable(List<MedicationEntry> medicationEntries, String medicationGroupName, boolean isGlobalScheme) {

        int headerRows = 2;

        table = new PdfPTable(getNumberOfColumns(isGlobalScheme));
        table.setWidthPercentage(95);
        table.setHeaderRows(headerRows);

        takeTimeManager = new TakeTimeManager(collectRegimentEntries(medicationEntries));
        dayperiodTakeManager = new DayperiodTakeManager();

        createMedicationHeaderRow(medicationGroupName, isGlobalScheme);
        createMedicationSubHeaderRow(takeTimeManager.getTakeTimes(), isGlobalScheme);

        if (medicationEntries != null) {

            for (MedicationEntry medicationEntry : medicationEntries) {

                createMedicationRow(medicationEntry, isGlobalScheme);

            }

        }

        return table;

    }

    public static List<PdfPTable> createMedicationTables(AbstractScheme scheme) {

        boolean globalScheme = false;
        if (scheme instanceof GlobalScheme) {
            globalScheme = true;
        }

        List<PdfPTable> tables = new ArrayList<>();

        TreeMap<CDTEMPORALITYvalues, List<MedicationEntry>> medicationEntriesByTemporality = groupMedicationEntriesByTemporality(scheme.getMedicationEntries()  );

        for (CDTEMPORALITYvalues cdtemporalitYvalues : medicationEntriesByTemporality.keySet()) {
            String medicationGroupName = "Overige";
            if (cdtemporalitYvalues != null) {
                medicationGroupName = translateTemporality(cdtemporalitYvalues);
            }
            PdfPTable medicationTable = createMedicationTable(medicationEntriesByTemporality.get(cdtemporalitYvalues), medicationGroupName, globalScheme);
            tables.add(medicationTable);
        }

        return tables;

    }

    private static void createMedicationHeaderRow(String medicationGroup, boolean isGlobalScheme) {

        PdfPCell cell = getMedicationHeaderCell();
        cell.setPaddingTop(6f);
        cell.setPaddingBottom(6f);
        cell.setPaddingLeft(6f);
        cell.setPaddingRight(6f);

        cell.setPhrase(getMedicationHeaderPhrase(medicationGroup));
        if (isGlobalScheme) {
            cell.setColspan(17);
        } else {
            cell.setColspan(16);
        }
        table.addCell(cell);

        cell.setPhrase(getMedicationHeaderPhrase(""));
        cell.setColspan(2);
        table.addCell(cell);

        cell.setPhrase(getMedicationHeaderPhrase("Ontbijt"));
        cell.setColspan(6);
        table.addCell(cell);

        cell.setPhrase(getMedicationHeaderPhrase("Middagmaal"));
        cell.setColspan(6);
        table.addCell(cell);

        cell.setPhrase(getMedicationHeaderPhrase("Avondmaal"));
        cell.setColspan(6);
        table.addCell(cell);

        cell.setPhrase(getMedicationHeaderPhrase(""));
        cell.setColspan(2);
        table.addCell(cell);

        cell.setPhrase(getMedicationHeaderPhrase(""));
        cell.setColspan(2);
        table.addCell(cell);

        cell.setPhrase(getMedicationHeaderPhrase(""));
        cell.setColspan(2);
        table.addCell(cell);

        cell.setPhrase(getMedicationHeaderPhrase(""));
        cell.setColspan(2);
        table.addCell(cell);

        cell.setPhrase(getMedicationHeaderPhrase(""));
        cell.setColspan(4);
        table.addCell(cell);

    }

    private static void createMedicationSubHeaderRow(Set<String> takeTimes, boolean isGlobalScheme) {

        PdfPCell cell = getMedicationSubHeaderCell();
        if (isGlobalScheme) {
            cell.setPhrase(getMedicationSubHeaderPhrase(" "));
            cell.setColspan(1);
            table.addCell(cell);
        }
        cell.setPhrase(getMedicationSubHeaderPhrase("Geneesmiddel"));
        cell.setColspan(4);
        table.addCell(cell);
        cell.setPhrase(getMedicationSubHeaderPhrase("Freq."));
        cell.setColspan(2);
        table.addCell(cell);
        cell.setPhrase(getMedicationSubHeaderPhrase("Begin"));
        cell.setColspan(3);
        table.addCell(cell);
        cell.setPhrase(getMedicationSubHeaderPhrase("Eind"));
        cell.setColspan(3);
        table.addCell(cell);
        cell.setPhrase(getMedicationSubHeaderPhrase("Inname/Eenheid"));
        cell.setColspan(4);
        table.addCell(cell);
        cell.setPhrase(getMedicationSubHeaderPhrase("Ochtend"));
        cell.setColspan(2);
        table.addCell(cell);

        cell.setPhrase(getMedicationSubHeaderPhrase("Voor"));
        cell.setColspan(2);
        table.addCell(cell);
        cell.setPhrase(getMedicationSubHeaderPhrase("Tijdens"));
        cell.setColspan(2);
        table.addCell(cell);
        cell.setPhrase(getMedicationSubHeaderPhrase("Na"));
        cell.setColspan(2);
        table.addCell(cell);

        cell.setPhrase(getMedicationSubHeaderPhrase("Voor"));
        cell.setColspan(2);
        table.addCell(cell);
        cell.setPhrase(getMedicationSubHeaderPhrase("Tijdens"));
        cell.setColspan(2);
        table.addCell(cell);
        cell.setPhrase(getMedicationSubHeaderPhrase("Na"));
        cell.setColspan(2);
        table.addCell(cell);

        cell.setPhrase(getMedicationSubHeaderPhrase("Voor"));
        cell.setColspan(2);
        table.addCell(cell);
        cell.setPhrase(getMedicationSubHeaderPhrase("Tijdens"));
        cell.setColspan(2);
        table.addCell(cell);
        cell.setPhrase(getMedicationSubHeaderPhrase("Na"));
        cell.setColspan(2);
        table.addCell(cell);
        cell.setPhrase(getMedicationSubHeaderPhrase("Slaap"));
        cell.setColspan(2);
        table.addCell(cell);

        if (CollectionsUtil.emptyOrNull(takeTimes)) {
            cell.setPhrase(getMedicationSubHeaderPhrase(""));
            cell.setColspan(6);
            table.addCell(cell);
        } else {
            Set<String> standaloneTakeTimes = takeTimeManager.getStandaloneTakeTimes();
            for (String takeTime : standaloneTakeTimes) {
                cell.setPhrase(getMedicationSubHeaderPhrase(takeTime));
                cell.setColspan(2);
                table.addCell(cell);
            }
            if (standaloneTakeTimes.size() < MAX_NUMBER_OF_STANDALONE_TAKING_TIMES) {
                for (int columnNumber = standaloneTakeTimes.size(); columnNumber < MAX_NUMBER_OF_STANDALONE_TAKING_TIMES; columnNumber++) {
                    cell.setPhrase(getDefaultPhrase(""));
                    cell.setColspan(2);
                    table.addCell(cell);
                }
            }

        }


        cell.setPhrase(getMedicationSubHeaderPhrase("Opmerkingen"));
        cell.setColspan(4);
        table.addCell(cell);

    }

    private static String getMedicationName(MedicationEntry medicationEntry) {
        if (medicationEntry.getIdentifier() != null && medicationEntry.getIdentifier().getName() != null) {
            return adaptLengthIfNecessary(medicationEntry.getIdentifier().getName());
        } else {
            return "";
        }
    }

    private static Phrase getMedicationNamePhrase(MedicationEntry medicationEntry) {
        Phrase phrase = getDefaultPhrase("");
        phrase.add(getMedicationName(medicationEntry));
        phrase.add(System.lineSeparator());
        phrase.add(System.lineSeparator());
        phrase.addAll(toLocalIdChunks(medicationEntry.getLocalId()));
        phrase.add(System.lineSeparator());
        phrase.add(System.lineSeparator());
        phrase.add(getDefaultPhrase(combineDateAndTime(medicationEntry.getCreatedDate(), medicationEntry.getCreatedTime())));
        phrase.add(System.lineSeparator());
        phrase.addAll(toHCPartyChunks(medicationEntry.getAuthors()));
        return phrase;
    }

    private static void createMedicationRow(MedicationEntry medicationEntry, boolean isGlobalScheme) {

        LOG.debug("Creating row for medication with instruction: " + medicationEntry.getInstructionForPatient());

        PdfPTable medicationEntryTable = new PdfPTable(getNumberOfColumns(isGlobalScheme));
        medicationEntryTable.setWidthPercentage(100);

        int suspensionsCount = 0;
        int suspensionTable = 0;
        if (medicationEntry.getSuspensions() != null) {
            suspensionsCount = medicationEntry.getSuspensions().size();
            if (medicationEntry.getSuspensions().size() > 0) {
                suspensionTable = 1;
            }
        }


        PdfPCell cellVerticalObsolete = null;
        if (isGlobalScheme) {
            if (rangeChecker.isObsolete(LocalDate.now(), medicationEntry)) {
                cellVerticalObsolete = getObsoleteMedicationCellObsolete();
                Phrase defaultPhrase = getMedicationObsoletePhrase("obsolete");
                cellVerticalObsolete.setPhrase(defaultPhrase);
            } else {
                cellVerticalObsolete = getObsoleteMedicationCellNotObsolete();
            }
            cellVerticalObsolete.setColspan(1);
        }


        if (medicationEntry.getPosologyOrRegimen() instanceof Posology) {

            if (isGlobalScheme) {
                if (cellVerticalObsolete != null) {
                    cellVerticalObsolete.setRowspan(1 + suspensionTable);
                }
                medicationEntryTable.addCell(cellVerticalObsolete);
            }

            Posology posology = (Posology) medicationEntry.getPosologyOrRegimen();

            PdfPCell cell = getCenteredCell();
            cell.setPhrase(getMedicationNamePhrase(medicationEntry));
            cell.setColspan(4);
            cell.setRowspan(1 + suspensionTable);
            medicationEntryTable.addCell(cell);

            cell.setPhrase(getDefaultPhrase(translateFrequency(medicationEntry.getFrequencyCode()) + translateRegimenRepetition(null, medicationEntry.getFrequencyCode())));
            cell.setColspan(2);
            cell.setRowspan(1);
            medicationEntryTable.addCell(cell);

            cell.setPhrase(getDefaultPhrase(combineStartDateAndCondition(medicationEntry.getBeginDate(), medicationEntry.getBeginCondition())));
            cell.setColspan(3);
            cell.setRowspan(1);
            medicationEntryTable.addCell(cell);

            cell.setPhrase(getDefaultPhrase(combineEndDateAndConditionAndDuration(medicationEntry.getEndDate(), medicationEntry.getEndCondition(), medicationEntry.getDuration(), medicationEntry.getBeginDate())));
            cell.setColspan(3);
            cell.setRowspan(1);
            medicationEntryTable.addCell(cell);

            cell.setPhrase(getDefaultPhrase(translateRoute(medicationEntry.getRoute())));
            cell.setColspan(4);
            cell.setRowspan(1);
            medicationEntryTable.addCell(cell);

            cell.setPhrase(getDefaultPhrase(posology.getText()));
            cell.setColspan(28);
            cell.setRowspan(1);
            medicationEntryTable.addCell(cell);

            Paragraph remarksParagraph = new Paragraph(getRemarksPhrase(medicationEntry));
            cell.setPhrase(remarksParagraph);
            cell.setLeading(0, REMARKS_CONTENT_LEADING);
            cell.setColspan(4);
            cell.setRowspan(1 + suspensionTable);
            medicationEntryTable.addCell(cell);

        } else if (medicationEntry.getPosologyOrRegimen() instanceof Regimen) {


            Regimen regimen = (Regimen) medicationEntry.getPosologyOrRegimen();

            List<List<RegimenEntry>> groupedRegimenentries = groupByDayperiodOrTime(regimen.getEntries());

            PdfPCell cell = getCenteredCell();
            cell.setPhrase(getMedicationNamePhrase(medicationEntry));
            cell.setColspan(4);

            if (isGlobalScheme) {
                if (cellVerticalObsolete != null) {
                    cellVerticalObsolete.setRowspan(groupedRegimenentries.size() + suspensionTable);
                }
                medicationEntryTable.addCell(cellVerticalObsolete);
            }

            cell.setRowspan(groupedRegimenentries.size() + suspensionTable);
            medicationEntryTable.addCell(cell);

            if (CollectionsUtil.notEmptyOrNull(groupedRegimenentries)) {

                int regimenIndex = 0;

                for (List<RegimenEntry> similarEntries : groupedRegimenentries) {

                    createMedicationSubRowsPart1(medicationEntryTable, medicationEntry.getFrequencyCode(), similarEntries.get(0));
                    createMedicationSubRowsPart2(medicationEntryTable, medicationEntry, regimen.getAdministrationUnit());
                    createMedicationSubRowsPart3(medicationEntryTable, similarEntries);

                    if (regimenIndex == 0) {
                        Paragraph remarksParagraph = new Paragraph(getRemarksPhrase(medicationEntry));
                        cell.setPhrase(remarksParagraph);
                        cell.setLeading(0, REMARKS_CONTENT_LEADING);
                        cell.setColspan(4);
                        cell.setRowspan(groupedRegimenentries.size()  + suspensionTable);
                        medicationEntryTable.addCell(cell);
                    }

                    regimenIndex++;

                }

            } else {
                createMedicationSubRowsPart1(medicationEntryTable, medicationEntry.getFrequencyCode(), null);
                createMedicationSubRowsPart2(medicationEntryTable, medicationEntry, regimen.getAdministrationUnit());
                createMedicationSubRowsPart3(medicationEntryTable, null);

                Paragraph remarksParagraph = new Paragraph(getRemarksPhrase(medicationEntry));
                cell.setPhrase(remarksParagraph);
                cell.setLeading(0, REMARKS_CONTENT_LEADING);
                cell.setColspan(4);
                cell.setRowspan(groupedRegimenentries.size() + suspensionTable);
                medicationEntryTable.addCell(cell);
            }

        } else {

            if (isGlobalScheme) {
                if (cellVerticalObsolete != null) {
                    cellVerticalObsolete.setRowspan(1 + suspensionTable);
                }
                medicationEntryTable.addCell(cellVerticalObsolete);
            }

            PdfPCell cell = getCenteredCell();
            cell.setPhrase(getMedicationNamePhrase(medicationEntry));
            cell.setColspan(4);
            cell.setRowspan(1 + suspensionTable);
            medicationEntryTable.addCell(cell);

            cell.setPhrase(getDefaultPhrase(translateFrequency(medicationEntry.getFrequencyCode()) + translateRegimenRepetition(null, medicationEntry.getFrequencyCode())));
            cell.setColspan(2);
            cell.setRowspan(1);
            medicationEntryTable.addCell(cell);

            cell.setPhrase(getDefaultPhrase(combineStartDateAndCondition(medicationEntry.getBeginDate(), medicationEntry.getBeginCondition())));
            cell.setColspan(3);
            cell.setRowspan(1);
            medicationEntryTable.addCell(cell);

            cell.setPhrase(getDefaultPhrase(combineEndDateAndConditionAndDuration(medicationEntry.getEndDate(), medicationEntry.getEndCondition(), medicationEntry.getDuration(), medicationEntry.getBeginDate())));
            cell.setColspan(3);
            cell.setRowspan(1);
            medicationEntryTable.addCell(cell);

            cell.setPhrase(getDefaultPhrase(translateRoute(medicationEntry.getRoute())));
            cell.setColspan(4);
            cell.setRowspan(1);
            medicationEntryTable.addCell(cell);

            cell.setPhrase(getDefaultPhrase("Geen posologie of regime gedefinieerd"));
            cell.setColspan(28);
            cell.setRowspan(1);
            medicationEntryTable.addCell(cell);

            Paragraph remarksParagraph = new Paragraph(getRemarksPhrase(medicationEntry));
            cell.setPhrase(remarksParagraph);
            cell.setLeading(0, REMARKS_CONTENT_LEADING);
            cell.setColspan(4);
            cell.setRowspan(1 + suspensionTable);
            medicationEntryTable.addCell(cell);


        }


        if (suspensionsCount > 0) {

            PdfPCell cell = getCenteredCell();
            cell.addElement(createSuspensionTable(medicationEntry.getSuspensions(), 40, medicationEntry.getLocalId()));
            cell.setColspan(40);
            cell.setPaddingTop(0f);
            cell.setPaddingBottom(0f);
            cell.setPaddingLeft(0f);
            cell.setPaddingRight(0f);
            medicationEntryTable.addCell(cell);

        }



        PdfPCell cell1 = new PdfPCell(medicationEntryTable);
        cell1.setColspan(getNumberOfColumns(isGlobalScheme));
        table.addCell(cell1);

    }

    private static List<List<RegimenEntry>> groupByDayperiodOrTime(final List<RegimenEntry> regimenEntriesOriginal) {

        List<RegimenEntry> regimenEntries = new ArrayList<>(regimenEntriesOriginal);

        List<List<RegimenEntry>> groupedRegimenEntries = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(regimenEntriesOriginal)) {
            return groupedRegimenEntries;
        }

        boolean allEntriesProcessed = false;
        int currentRegimenEntry = 0;

        while (!allEntriesProcessed) {

            List<RegimenEntry> similarEntries = new ArrayList<>();

            RegimenEntry regimenEntry = regimenEntries.get(currentRegimenEntry);
            similarEntries.add(regimenEntry);

            ListIterator<RegimenEntry> innerIterator = regimenEntries.listIterator(currentRegimenEntry + 1);
            while (innerIterator.hasNext()) {
                RegimenEntry otherRegimenEntry = (RegimenEntry) innerIterator.next();

                if (regimenEntry.appliesToSameDay(otherRegimenEntry)) {
                    similarEntries.add(otherRegimenEntry);
                    innerIterator.remove();
                }
            }

            if (regimenEntries.size() == 1) {
                allEntriesProcessed = true;
            }

            currentRegimenEntry++;

            groupedRegimenEntries.add(similarEntries);

            if (currentRegimenEntry + 1 > regimenEntries.size()) {
                break;
            }


        }

        return groupedRegimenEntries;

    }

    private static Phrase getRemarksPhrase(MedicationEntry medicationEntry) {

        Phrase phrase = getDefaultPhrase("");

        if (StringUtils.isNotEmpty(medicationEntry.getMedicationUse())){
            if (phrase.size() > 1) {
                phrase.add(System.lineSeparator());
            }
            phrase.addAll(toCommentHeaderAndValueChunk("indicatie:", adaptLengthIfNecessary(medicationEntry.getMedicationUse())));
        }

        if (StringUtils.isNotEmpty(medicationEntry.getInstructionForPatient())){
            if (phrase.size() > 1) {
                phrase.add(System.lineSeparator());
            }
            phrase.addAll(toCommentHeaderAndValueChunk("gebruiksaanwijzing:", adaptLengthIfNecessary(medicationEntry.getInstructionForPatient())));
        }

        if (StringUtils.isNotEmpty(medicationEntry.getInstructionForPatient())) {
            if (phrase.size() > 1) {
                phrase.add(System.lineSeparator());
            }
            phrase.addAll(toCommentHeaderAndValueChunk("magistrale bereiding:", adaptLengthIfNecessary(medicationEntry.getCompoundPrescription())));
        }

        if (StringUtils.isNotEmpty(medicationEntry.getInstructionForOverdosing())) {
            if (phrase.size() > 1) {
                phrase.add(System.lineSeparator());
            }
            phrase.addAll(toCommentHeaderAndValueChunk("overdosis:", adaptLengthIfNecessary(medicationEntry.getInstructionForOverdosing())));
        }

        return phrase;

    }

    private static String adaptLengthIfNecessary(String text) {

        if (StringUtils.length(text) > MAX_LENGTH_TEXT_FIElDS) {
            return TOO_LARGE_TEXT;
        } else {
            return text;
        }

    }

    private static PdfPTable createSuspensionTable(List<Suspension> suspensions, int tableColumnCount, LocalId localId) {

        if (CollectionsUtil.emptyOrNull(suspensions)) {
            return null;
        }

        PdfPTable table = new PdfPTable(tableColumnCount);
        table.setWidthPercentage(100);
        PdfPCell cellVertical = getSuspensionHeaderCell();
        cellVertical.setPhrase(getSuspensionHeaderPhrase("STOP"));
        cellVertical.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellVertical.setRotation(90);
        cellVertical.setColspan(1);
        cellVertical.setRowspan(suspensions.size() + 1);
        table.addCell(cellVertical);

        PdfPCell cell = getSuspensionHeaderCell();
        cell.setPhrase(getSuspensionHeaderPhrase("ID"));
        cell.setColspan(1);
        table.addCell(cell);

        cell.setPhrase(getSuspensionHeaderPhrase("Type"));
        cell.setColspan(3);
        table.addCell(cell);

        cell.setPhrase(getSuspensionHeaderPhrase("Van"));
        cell.setColspan(3);
        table.addCell(cell);

        cell.setPhrase(getSuspensionHeaderPhrase("Tot"));
        cell.setColspan(3);
        table.addCell(cell);

        cell.setPhrase(getSuspensionHeaderPhrase("Reden"));
        cell.setColspan(18);
        table.addCell(cell);

        cell.setPhrase(getSuspensionHeaderPhrase("Aangemaakt op"));
        cell.setColspan(3);
        table.addCell(cell);

        cell.setPhrase(getSuspensionHeaderPhrase("Aangemaakt door"));
        cell.setColspan(8);
        table.addCell(cell);

        for (Suspension suspension : suspensions) {

            PdfPCell cellVertical2 = getCenteredCell();
            Phrase defaultPhrase = getDefaultPhrase("");
            defaultPhrase.addAll(toLocalIdChunks(localId));
            cellVertical2.setPhrase(defaultPhrase);
            cellVertical2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellVertical2.setRotation(90);
            cellVertical2.setColspan(1);
            table.addCell(cellVertical2);

            cell = getCenteredCell();
            cell.setPhrase(getDefaultPhrase(translateLifecycle(suspension.getLifecycle())));
            cell.setColspan(3);
            table.addCell(cell);

            cell = getCenteredCell();
            cell.setPhrase(getDefaultPhrase(combineStartDateAndCondition(suspension.getBeginDate(), null)));
            cell.setColspan(3);
            table.addCell(cell);

            cell = getCenteredCell();
            cell.setPhrase(getDefaultPhrase(combineEndDateAndConditionAndDuration(suspension.getEndDate(), null, suspension.getDuration(), suspension.getBeginDate())));
            cell.setColspan(3);
            table.addCell(cell);

            cell = getCenteredCell();
            cell.setPhrase(getDefaultPhrase(suspension.getReason()));
            cell.setColspan(18);
            table.addCell(cell);

            cell = getCenteredCell();
            cell.setPhrase(getDefaultPhrase(combineDateAndTime(suspension.getCreatedDate(), suspension.getCreatedTime())));
            cell.setColspan(3);
            table.addCell(cell);

            cell = getCenteredCell();
            Phrase author = getDefaultPhrase("");
            author.addAll(toHCPartyChunks(suspension.getAuthors()));
            author.add(System.lineSeparator());
            cell.setPhrase(author);
            cell.setColspan(8);
            table.addCell(cell);

        }

        return table;

    }

    private static String combineDateAndTime(LocalDate date,  DateTime time) {
        return joinFields(formatAsDate(date), formatAsTime(time), System.lineSeparator());
    }

    /**
     * Create a medication subrow for columns: frequency
     * @param frequencyCode
     * @param regimenEntry
     */
    private static void createMedicationSubRowsPart1(PdfPTable table, FrequencyCode frequencyCode, RegimenEntry regimenEntry) {

        PdfPCell cell = getCenteredCell();

        cell.setPhrase(getDefaultPhrase(translateFrequency(frequencyCode) + translateRegimenRepetition(regimenEntry, frequencyCode)));
        cell.setColspan(2);
        table.addCell(cell);


    }

    /**
     * Create a medication subrows for colums: begin, end, inname/eenheid
     * @param medicationEntry
     * @param administrationUnit
     */
    private static void createMedicationSubRowsPart2(PdfPTable table, MedicationEntry medicationEntry, String administrationUnit) {
        PdfPCell cell = getCenteredCell();

        cell.setPhrase(getDefaultPhrase(combineStartDateAndCondition(medicationEntry.getBeginDate(), adaptLengthIfNecessary(medicationEntry.getBeginCondition()))));
        cell.setColspan(3);
        cell.setRowspan(1);
        table.addCell(cell);

        cell.setPhrase(getDefaultPhrase(combineEndDateAndConditionAndDuration(medicationEntry.getEndDate(), adaptLengthIfNecessary(medicationEntry.getEndCondition()), medicationEntry.getDuration(), medicationEntry.getBeginDate())));
        cell.setColspan(3);
        cell.setRowspan(1);
        table.addCell(cell);

        cell.setPhrase(getDefaultPhrase(translateRoute(medicationEntry.getRoute()) + " / " + translateAdministrationUnit(administrationUnit)));
        cell.setColspan(4);
        cell.setRowspan(1);
        table.addCell(cell);

    }

    private static PdfPCell prepareQuantityCell(BigDecimal quantity) {

        PdfPCell cell = null;

        if (quantity == null) {
            cell = getCenteredCell();
            cell.setPhrase(getQuantityPhrase(""));
            return cell;
        }

        String quantityForMoment = translateQuantity(quantity);
        cell = getQuantityWithValueCell();
        cell.setPhrase(getQuantityPhrase(quantityForMoment));

        return cell;
    }


    private static Map<Dayperiod, BigDecimal> sumQuantitiesPerDayperiod(List<RegimenEntry> regimenEntries) {

        Map<Dayperiod, BigDecimal> quantitiesPerDayperiod = new HashMap<>();

        for (RegimenEntry regimenEntry : regimenEntries) {
            if (regimenEntry.getDayperiodOrTime() instanceof RegimenDayperiod) {
                RegimenDayperiod regimenDayperiod = (RegimenDayperiod) regimenEntry.getDayperiodOrTime();

                if (quantitiesPerDayperiod.containsKey(regimenDayperiod.getDayperiod())) {
                    quantitiesPerDayperiod.put(regimenDayperiod.getDayperiod(), quantitiesPerDayperiod.get(regimenDayperiod.getDayperiod()).add(regimenEntry.getQuantity()));
                } else {
                    quantitiesPerDayperiod.put(regimenDayperiod.getDayperiod(), regimenEntry.getQuantity());
                }

            }
        }

        return quantitiesPerDayperiod;

    }

    private static Map<Dayperiod, BigDecimal> sumQuantitiesPerNonStandaloneDayperiod(Map<Dayperiod, BigDecimal> quantitiesPerDayperiod) {

        Map<Dayperiod, BigDecimal> quantitiesPerStandaloneDayperiod = new HashMap<>();

        for (Map.Entry<Dayperiod, BigDecimal> entry : quantitiesPerDayperiod.entrySet()) {
            if (!dayperiodTakeManager.isStandaloneDayperiod(entry.getKey())) {
                quantitiesPerStandaloneDayperiod.put(entry.getKey(), entry.getValue());
            }
        }

        return quantitiesPerStandaloneDayperiod;

    }

    private static Map<String, BigDecimal> sumQuantitiesPerTakeTime(List<RegimenEntry> regimenEntries) {

        Map<String, BigDecimal> quantitiesPerTakeTime = new HashMap<>();

        for (RegimenEntry regimenEntry : regimenEntries) {
            if (regimenEntry.getDayperiodOrTime() instanceof RegimenTime) {
                RegimenTime regimenTime = (RegimenTime) regimenEntry.getDayperiodOrTime();

                String timeString = takeTimeManager.toTakeTimeString(regimenTime.getTime());
                if (quantitiesPerTakeTime.containsKey(timeString)) {
                    quantitiesPerTakeTime.put(timeString, quantitiesPerTakeTime.get(timeString).add(regimenEntry.getQuantity()));
                } else {
                    quantitiesPerTakeTime.put(timeString, regimenEntry.getQuantity());
                }

            }
        }

        return quantitiesPerTakeTime;

    }

    private static Map<String, BigDecimal> sumQuantitiesPerNonStandaloneTakeTime(Map<String, BigDecimal> quantitiesPerTakeTime) {

        Map<String, BigDecimal> quantitiesPerStandaloneTakeTime = new HashMap<>();

        for (Map.Entry<String, BigDecimal> entry : quantitiesPerTakeTime.entrySet()) {
            if (!takeTimeManager.isStandaloneTakeTime(entry.getKey())) {
                quantitiesPerStandaloneTakeTime.put(entry.getKey(), entry.getValue());
            }
        }

        return quantitiesPerStandaloneTakeTime;

    }


    private static String concatenateTakeMoments(Map<Dayperiod, BigDecimal> sumQuantitiesPerNonStandaloneDayperiod, Map<String, BigDecimal> sumQuantitiesPerNonStandaloneTakeTime) {

        ArrayList<String> takemoments = new ArrayList<>();

        for (Map.Entry<Dayperiod, BigDecimal> dayperiodBigDecimalEntry : sumQuantitiesPerNonStandaloneDayperiod.entrySet()) {
            takemoments.add(translateQuantity(dayperiodBigDecimalEntry.getValue()) + " " + translateDayperiod(dayperiodBigDecimalEntry.getKey()));
        }

        for (Map.Entry<String, BigDecimal> takeTimeBigDecimalEntry : sumQuantitiesPerNonStandaloneTakeTime.entrySet()) {
            takemoments.add(translateQuantity(takeTimeBigDecimalEntry.getValue()) + " om " + takeTimeBigDecimalEntry.getKey());
        }

        Collections.sort(takemoments, NumberAwareStringComparator.INSTANCE);

        return StringUtils.joinWith(System.lineSeparator() + System.lineSeparator(), takemoments.toArray());

    }



    /**
     * Create a medication subrow for intake columns (based on dayperiod and daytime)
     * @param similarEntries
     */
    private static void createMedicationSubRowsPart3(PdfPTable table, List<RegimenEntry> similarEntries) {

        PdfPCell cell;

        Map<Dayperiod, BigDecimal> quantitiesPerDayperiod = sumQuantitiesPerDayperiod(similarEntries);
        Map<String, BigDecimal> sumQuantitiesPerTakeTime = sumQuantitiesPerTakeTime(similarEntries);

        Map<Dayperiod, BigDecimal> sumQuantitiesPerNonStandaloneDayperiod = sumQuantitiesPerNonStandaloneDayperiod(quantitiesPerDayperiod);
        Map<String, BigDecimal> sumQuantitiesPerNonStandaloneTakeTime = sumQuantitiesPerNonStandaloneTakeTime(sumQuantitiesPerTakeTime);

        // Standalone dayperiod columns
        for (Dayperiod currentDayperiod : dayperiodTakeManager.getAllPossibleStandaloneDayperiods()) {
            cell = prepareQuantityCell(quantitiesPerDayperiod.get(currentDayperiod));
            cell.setColspan(2);
            table.addCell(cell);
        }

        // non standalone dayperiod or time, grouped in once cell
        if (!sumQuantitiesPerNonStandaloneDayperiod.isEmpty() || !sumQuantitiesPerNonStandaloneTakeTime.isEmpty()) {

            cell = getQuantityWithValueCell();
            cell.setPhrase(getQuantityPhrase(concatenateTakeMoments(sumQuantitiesPerNonStandaloneDayperiod, sumQuantitiesPerTakeTime)));
            cell.setColspan(6);
            table.addCell(cell);

        // standalone take times
        } else {

            Set<String> standaloneTakeTimes = takeTimeManager.getStandaloneTakeTimes();
            for (String takeTimeHeader : standaloneTakeTimes) {

                if (sumQuantitiesPerTakeTime.containsKey(takeTimeHeader)) {
                    cell = getQuantityWithValueCell();
                    cell.setPhrase(getQuantityPhrase(translateQuantity(sumQuantitiesPerTakeTime.get(takeTimeHeader))));
                    cell.setColspan(2);
                    table.addCell(cell);
                } else {
                    cell = getCenteredCell();
                    cell.setPhrase(getDefaultPhrase(""));
                    cell.setColspan(2);
                    table.addCell(cell);

                }

            }
            if (standaloneTakeTimes.size() < MAX_NUMBER_OF_STANDALONE_TAKING_TIMES) {
                for (int columnNumber = standaloneTakeTimes.size(); columnNumber < MAX_NUMBER_OF_STANDALONE_TAKING_TIMES; columnNumber++) {
                    cell = getCenteredCell();
                    cell.setPhrase(getQuantityPhrase(""));
                    cell.setColspan(2);
                    table.addCell(cell);
                }
            }

        }

    }

    private static String combineStartDateAndCondition(LocalDate date, String condition) {

        return joinFields(formatAsDate(date), condition);

    }

    private static String combineEndDateAndConditionAndDuration(LocalDate date, String condition, Duration duration, LocalDate startDate) {

        if (duration == null) {
            return combineStartDateAndCondition(date, condition);
        }

        String durationString = durationToString(duration, startDate);

        return joinFields(durationString, combineStartDateAndCondition(date, condition));

    }

    @Override
    protected boolean isSupported(CDTRANSACTION cdtransaction) {
        return true;
    }
}