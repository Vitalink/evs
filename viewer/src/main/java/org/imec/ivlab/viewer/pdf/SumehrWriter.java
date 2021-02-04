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
import static org.imec.ivlab.viewer.pdf.TableHelper.toDetailRowsIfHasValue;
import static org.imec.ivlab.viewer.pdf.TableHelper.toUnparsedContentTables;
import static org.imec.ivlab.viewer.pdf.Translator.durationToString;
import static org.imec.ivlab.viewer.pdf.Translator.formatAsDate;
import static org.imec.ivlab.viewer.pdf.Translator.formatAsDateTime;
import static org.imec.ivlab.viewer.pdf.Translator.formatAsTime;
import static org.imec.ivlab.viewer.pdf.Translator.translateAdministrationUnit;
import static org.imec.ivlab.viewer.pdf.Translator.translateDayperiod;
import static org.imec.ivlab.viewer.pdf.Translator.translateFrequency;
import static org.imec.ivlab.viewer.pdf.Translator.translateRoute;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.imec.ivlab.core.kmehr.model.util.CDContentUtil;
import org.imec.ivlab.core.kmehr.model.util.TextTypeUtil;
import org.imec.ivlab.core.model.internal.mapper.medication.Identifier;
import org.imec.ivlab.core.model.internal.mapper.medication.Posology;
import org.imec.ivlab.core.model.internal.mapper.medication.Regimen;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenDate;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenDaynumber;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenDayperiod;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenEntry;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenTime;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenWeekday;
import org.imec.ivlab.core.model.internal.parser.ParsedItem;
import org.imec.ivlab.core.model.internal.parser.sumehr.ContactPerson;
import org.imec.ivlab.core.model.internal.parser.sumehr.HcParty;
import org.imec.ivlab.core.model.internal.parser.sumehr.HealthCareElement;
import org.imec.ivlab.core.model.internal.parser.sumehr.MedicationEntrySumehr;
import org.imec.ivlab.core.model.internal.parser.sumehr.PatientWill;
import org.imec.ivlab.core.model.internal.parser.sumehr.Problem;
import org.imec.ivlab.core.model.internal.parser.sumehr.Risk;
import org.imec.ivlab.core.model.internal.parser.sumehr.Sumehr;
import org.imec.ivlab.core.model.internal.parser.sumehr.Treatment;
import org.imec.ivlab.core.model.internal.parser.sumehr.Vaccination;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
import org.imec.ivlab.core.model.upload.sumehrlist.SumehrList;
import org.imec.ivlab.core.model.upload.sumehrlist.SumehrListExtractor;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.IOUtils;
import org.imec.ivlab.core.util.StringUtils;
import org.imec.ivlab.viewer.converter.TestFileConverter;
import org.imec.ivlab.viewer.converter.exceptions.SchemaConversionException;

public class SumehrWriter extends Writer {

    private static HashMap<String, String> cdContentTranslations = new HashMap<>();
    static {
        cdContentTranslations.put("CD-CLINICAL", "IBUI");
        cdContentTranslations.put("CD-PATIENTWILL", "Patientwill");
        cdContentTranslations.put("CD-EAN", "EAN");
        cdContentTranslations.put("CD-ATC", "ATC");
        cdContentTranslations.put("CD-VACCINEINDICATION", "Indication");

    }

    private static Set<String> cdContentToIgnore = new HashSet<>();
    static {
        cdContentToIgnore.add("LOCAL");
    }

    public static void main(String[] args) {

        SumehrWriter sumehrWriter = new SumehrWriter();
        Stream
            .of("1-sumehr-1dot1-all-parseable", "2-sumehr-1dot1-unparseable-content", "3-sumehr-2dot0", "4-sumehr-2dot0", "5-sumehr-2dot0-risk-text-and-medication")
            .forEach(filename -> sumehrWriter.createPdf(readTestFile(filename + ".xml").get(0), filename + ".pdf"));

    }

    private static List<Sumehr> readTestFile(String filename) {
        File inputFile = IOUtils.getResourceAsFile("/sumehr/" + filename);

        KmehrEntryList kmehrEntryList = KmehrExtractor.getKmehrEntryList(inputFile);
        SumehrList sumehrList = SumehrListExtractor.getSumehrList(kmehrEntryList);

        return TestFileConverter.convertToSumehrs(sumehrList);
    }

    public void createPdf(Sumehr sumehr, String fileLocation) throws SchemaConversionException {

        String schemeTitle = "Sumehr visualisation";

        PdfPTable generalInfoTable = createGeneralInfoTable(schemeTitle, sumehr.getEvsRef());
        List<PdfPTable> detailTables = createSumehrDetailTables(sumehr);

        writeToDocument(fileLocation, generalInfoTable, detailTables);
    }


    private PdfPTable createGeneralInfoTable(String title, String evsRef) {

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

        cell = new PdfPCell(getFrontPageHeaderPhrase(""));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(10);
        table.addCell(cell);

        cell = new PdfPCell(getDefaultPhrase("Afdruk op: "));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(6);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        cell = new PdfPCell(getDefaultPhraseBold(formatAsDateTime(LocalDateTime.now())));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(4);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(getFrontPageHeaderPhrase(""));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(10);
        table.addCell(cell);

        cell = new PdfPCell(getDefaultPhrase("EVSref: "));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(6);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        cell = new PdfPCell(getDefaultPhrase(evsRef));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(4);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(getFrontPageHeaderPhrase(" "));
        cell.setBorderColor(BaseColor.WHITE);
        cell.setColspan(20);
        table.addCell(cell);

        return table;

    }

    private List<PdfPTable> createSumehrDetailTables(Sumehr sumehr) {

        List<PdfPTable> tables = new ArrayList<>();

        tables.add(combineTables(null, new ArrayList<>(), toUnparsedContentTables(Collections.singletonList(sumehr), null)));

        tables.add(combineTables(createTitleTable("Author"), createHcPartyTables(sumehr.getTransactionCommon().getAuthor()), toUnparsedContentTables(sumehr.getTransactionCommon().getAuthor(), "Author")));
        tables.add(combineTables(createTitleTable("Patient"), patientToTable(sumehr.getTransactionCommon().getPerson()), toUnparsedContentTable(sumehr.getTransactionCommon().getPerson(), "Patient")));
        Collection<PdfPTable> riskTables = new ArrayList<>();
        riskTables.addAll(createRisksTable(sumehr.getAllergies(), "Allergy"));
        riskTables.addAll(createRisksTable(sumehr.getAdrs(), "Adverse drug reaction"));
        riskTables.addAll(createRisksTable(sumehr.getSocialRisks(), "Social risk"));
        riskTables.addAll(createRisksTable(sumehr.getRisks(), "Other risk"));
        ArrayList<Risk> risks = new ArrayList<>();
        risks.addAll(sumehr.getAllergies());
        risks.addAll(sumehr.getAdrs());
        risks.addAll(sumehr.getSocialRisks());
        risks.addAll(sumehr.getRisks());
        tables.add(combineTables(createTitleTable("Risks"), riskTables, toUnparsedContentTables(risks, "Risks")));
        tables.add(combineTables(createTitleTable("Medication"), createMedicationEntriesTable(sumehr.getMedicationEntries()), toUnparsedContentTables(sumehr.getMedicationEntries(), "Medication")));
        tables.add(combineTables(createTitleTable("Vaccinations"), createVaccinationsTable(sumehr.getVaccinations()), toUnparsedContentTables(sumehr.getVaccinations(), "Vaccinations")));
        tables.add(combineTables(createTitleTable("Treatments"), createTreatmentTables(sumehr.getTreatments()), toUnparsedContentTables(sumehr.getTreatments(), "Treatments")));
        tables.add(combineTables(createTitleTable("Problems"), createProblemTables(sumehr.getProblems()), toUnparsedContentTables(sumehr.getProblems(), "Problems")));
        tables.add(combineTables(createTitleTable("Healthcare elements"), createHealthCareTables(sumehr.getHealthCareElements()), toUnparsedContentTables(sumehr.getHealthCareElements(), "Healthcare elements")));
        tables.add(combineTables(createTitleTable("Patient will"), createPatientWillsTable(sumehr.getPatientWills()), toUnparsedContentTables(sumehr.getPatientWills(), "Patient will")));
        tables.add(combineTables(createTitleTable("GMD manager"), createHcPartyTables(sumehr.getGmdManagers()), toUnparsedContentTables(sumehr.getGmdManagers(), "GMD manager")));
        tables.add(combineTables(createTitleTable("Contact - acquaintances"), createContactPersonTables(sumehr.getContactPersons()), toUnparsedContentTables(sumehr.getContactPersons(), "Contact - acquaintances")));
        tables.add(combineTables(createTitleTable("Contact - healthcare parties"), createHcPartyTables(sumehr.getContactHCParties()), toUnparsedContentTables(sumehr.getContactHCParties(), "Contact - healthcare parties")));
        return tables;

    }

    private <T extends ParsedItem> List<PdfPTable> toUnparsedContentTable(T parsedItem, String topic) {
        ArrayList<T> list = new ArrayList<>();
        list.add(parsedItem);
        return toUnparsedContentTables(list, topic);
    }

    private PdfPTable vaccinationToTable(Vaccination vaccination) {

        PdfPTable table = initializeDetailTable();

        addRow(table, createDetailHeader(vaccination.getVaccinatedAgainst()));
        addRow(table, toDetailRowIfHasValue("Vaccine", identifierIdAndName(vaccination.getIdentifier())));
        addRow(table, toDetailRowIfHasValue("Text", collectText(vaccination.getTextTypes())));

        if (CollectionsUtil.notEmptyOrNull(vaccination.getCdcontents())) {
            for (CDCONTENT cdcontent : vaccination.getCdcontents()) {
                addRow(table, cdContentToDetailRow(cdcontent));
            }
        }

        addRow(table, toDetailRowIfHasValue("Vaccination date", vaccination.getApplicationDate()));
        if (vaccination.getLifecycle() != null) {
            addRow(table, toDetailRowIfHasValue("Lifecycle", vaccination.getLifecycle().value()));
        }
        addRow(table, toDetailRowIfHasValue("Date of registration", Translator.formatAsDateTime(vaccination.getRecordDateTime())));
        addRow(table, toDetailRowIfHasValue("Relevant", vaccination.getRelevant()));


        return table;
    }

    private String collectText(List<TextType> textTypes) {
        return StringUtils.joinWith(System.lineSeparator(), TextTypeUtil
            .toStrings(textTypes).toArray());
    }

    private Collection<PdfPTable> createRisksTable(List<Risk> risks, String category) {
        return Optional.ofNullable(risks)
            .orElse(Collections.emptyList())
            .stream()
            .map(risk -> riskToTable(risk, category))
            .collect(Collectors.toList());
    }

    private Collection<PdfPTable> createHcPartyTables(List<HcParty> hcParties) {
        return Optional.ofNullable(hcParties)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::hcpartyTypeToTable)
            .collect(Collectors.toList());
    }

    private Collection<PdfPTable> createPatientWillsTable(List<PatientWill> patientWills) {
        return Optional.ofNullable(patientWills)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::patientWillToTable)
            .collect(Collectors.toList());
    }

    private Collection<PdfPTable> createVaccinationsTable(List<Vaccination> vaccinations) {
        return Optional.ofNullable(vaccinations)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::vaccinationToTable)
            .collect(Collectors.toList());
    }

    private Collection<PdfPTable> createMedicationEntriesTable(List<MedicationEntrySumehr> medicationEntries) {
        return Optional.ofNullable(medicationEntries)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::medicationEntryToTable)
            .collect(Collectors.toList());
    }

    private List<PdfPTable> createHealthCareTables(List<HealthCareElement> healthCareElements) {
        return Optional.ofNullable(healthCareElements)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::healthCareElementToTable)
            .collect(Collectors.toList());
    }

    private List<PdfPTable> createTreatmentTables(List<Treatment> treatments) {
        return Optional
            .ofNullable(treatments)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::treatmentToTable)
            .collect(Collectors.toList());
    }

    private List<PdfPTable> createProblemTables(List<Problem> problems) {
        return Optional
            .ofNullable(problems)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::problemToTable)
            .collect(Collectors.toList());
    }

    private List<PdfPTable> createContactPersonTables(List<ContactPerson> contactPersons) {
        if (CollectionsUtil.emptyOrNull(contactPersons)) {
            return null;
        }

        List<PdfPTable> tables = new ArrayList<>();

        for (ContactPerson contactPerson : contactPersons) {
            PdfPTable table = contactPersonToTable(contactPerson);
            addRow(table, toDetailRowIfHasValue("Relationship", contactPerson.getRelation()));
            tables.add(table);
        }

        return tables;

    }

    private PdfPTable riskToTable(Risk risk, String category) {

        PdfPTable table = initializeDetailTable();
        String title = StringUtils.joinWith(" & ", TextTypeUtil.toStrings(risk.getContentTextTypes()).toArray());
        addRow(table, createDetailHeader(StringUtils.joinWith(": ", category, ""), title));

        if (CollectionsUtil.notEmptyOrNull(risk.getCdcontents())) {
            for (CDCONTENT cdcontent : risk.getCdcontents()) {
                addRow(table, cdContentToDetailRow(cdcontent));
            }
        }
        addRow(table, toDetailRowIfHasValue("Text", collectText(risk.getContentTextTypes())));
        addRow(table, toDetailRowIfHasValue("Text", collectText(risk.getTextTypes())));
        addRow(table, toDetailRowIfHasValue("Begin", risk.getBeginmoment()));
        addRow(table, toDetailRowIfHasValue("Relevant", risk.getIsRelevant()));
        if (risk.getLifecycle() != null) {
            addRow(table, toDetailRowIfHasValue("Lifecycle", risk.getLifecycle().value()));
        }

        return table;

    }

    private PdfPTable patientWillToTable(PatientWill patientWill) {

        PdfPTable table = initializeDetailTable();

        String title = StringUtils.joinWith(System.lineSeparator(), CDContentUtil.toStrings(patientWill.getCdcontents()).toArray());
        addRow(table, createDetailHeader(title));

        if (CollectionsUtil.notEmptyOrNull(patientWill.getCdcontents())) {
            for (CDCONTENT cdcontent : patientWill.getCdcontents()) {
                addRow(table, cdContentToDetailRow(cdcontent));
            }
        }

        addRow(table, toDetailRowIfHasValue("Text", collectText(patientWill.getContentTextTypes())));
        addRow(table, toDetailRowIfHasValue("Text", collectText(patientWill.getTextTypes())));
        addRow(table, toDetailRowIfHasValue("Date of registration", Translator.formatAsDateTime(patientWill.getRecordDateTime())));
        addRow(table, toDetailRowIfHasValue("Begin", patientWill.getBeginmoment()));
        addRow(table, toDetailRowIfHasValue("Relevant", patientWill.getIsRelevant()));
        if (patientWill.getLifecycle() != null) {
            addRow(table, toDetailRowIfHasValue("Lifecycle", patientWill.getLifecycle().value()));
        }

        return table;

    }

    private PdfPTable treatmentToTable(Treatment treatment) {

        PdfPTable table = initializeDetailTable();

        String name = collectText(treatment.getContentTextTypes());

        addRow(table, createDetailHeader(name));

        if (CollectionsUtil.notEmptyOrNull(treatment.getCdcontents())) {
            for (CDCONTENT cdcontent : treatment.getCdcontents()) {
                addRow(table, cdContentToDetailRow(cdcontent));
            }
        }

        //addRow(table, toDetailRowIfHasValue("Text", name));
        addRow(table, toDetailRowIfHasValue("Begin", treatment.getBeginmoment()));
        addRow(table, toDetailRowIfHasValue("End", treatment.getEndmoment()));
        addRow(table, toDetailRowIfHasValue("Text", collectText(treatment.getContentTextTypes())));
        addRow(table, toDetailRowIfHasValue("Text", collectText(treatment.getTextTypes())));
        addRow(table, toDetailRowIfHasValue("Relevant", treatment.getIsRelevant()));
        if (treatment.getLifecycle() != null) {
            addRow(table, toDetailRowIfHasValue("Lifecycle", treatment.getLifecycle().value()));
        }
        return table;
    }

    private PdfPTable problemToTable(Problem problem) {

        PdfPTable table = initializeDetailTable();

        String name = collectText(problem.getContentTextTypes());

        addRow(table, createDetailHeader(name));

        if (CollectionsUtil.notEmptyOrNull(problem.getCdcontents())) {
            for (CDCONTENT cdcontent : problem.getCdcontents()) {
                addRow(table, cdContentToDetailRow(cdcontent));
            }
        }

        //addRow(table, toDetailRowIfHasValue("Text", name));
        addRow(table, toDetailRowIfHasValue("Begin", problem.getBeginmoment()));
        addRow(table, toDetailRowIfHasValue("End", problem.getEndmoment()));
        addRow(table, toDetailRowIfHasValue("Text", collectText(problem.getContentTextTypes())));
        addRow(table, toDetailRowIfHasValue("Text", collectText(problem.getTextTypes())));
        addRow(table, toDetailRowIfHasValue("Relevant", problem.getIsRelevant()));
        if (problem.getLifecycle() != null) {
            addRow(table, toDetailRowIfHasValue("Lifecycle", problem.getLifecycle().value()));
        }
        return table;
    }



    private String identifierIdAndName(Identifier identifier) {
        if (identifier == null) {
            return null;
        }
        return StringUtils.joinWith(System.lineSeparator(), identifier.getId(), identifier.getName());
    }

    private String identifierNameOrId(Identifier identifier) {
        if (identifier == null) {
            return null;
        }
        if (identifier.getName() != null) {
            return identifier.getName();
        }
        return identifier.getId();
    }

    private PdfPTable medicationEntryToTable(MedicationEntrySumehr medicationEntrySumehr) {

        PdfPTable table = initializeDetailTable();
        addRow(table, createDetailHeader(identifierNameOrId(medicationEntrySumehr.getIdentifier())));

        addRow(table, toDetailRowIfHasValue("Medication", identifierIdAndName(medicationEntrySumehr.getIdentifier())));

        Optional
            .ofNullable(medicationEntrySumehr.getCdcontents())
            .orElse(Collections.emptyList())
            .forEach(cdcontent -> addRow(table, cdContentToDetailRow(cdcontent)));

        addRow(table, toDetailRowIfHasValue("Begin", medicationEntrySumehr.getBeginDate()));
        addRow(table, toDetailRowIfHasValue("End", medicationEntrySumehr.getEndDate()));
        if (medicationEntrySumehr.getFrequencyCode() != null) {
            addRow(table, toDetailRowIfHasValue("Frequency", StringUtils.joinWith(" ", "(NL)", translateFrequency(medicationEntrySumehr.getFrequencyCode()))));
        }
        if (medicationEntrySumehr.getDuration() != null) {
            addRow(table, toDetailRowIfHasValue("Duration", StringUtils.joinWith(" ", "(NL)", durationToString(medicationEntrySumehr.getDuration(), medicationEntrySumehr.getBeginDate()))));
        }

        Optional
            .ofNullable(medicationEntrySumehr.getDayperiods())
            .orElse(Collections.emptyList())
            .forEach(dayperiod -> addRow(table, toDetailRowIfHasValue("Dayperiod", dayperiod.getDayperiod().getValue())));

        if (medicationEntrySumehr.getPosologyOrRegimen() instanceof Posology) {
            Posology posology = (Posology) medicationEntrySumehr.getPosologyOrRegimen();
            if (posology != null) {
                addRow(table, toDetailRowIfHasValue("Posology", posology.getText()));
                addRow(table, toDetailRowIfHasValue("High", posology.getPosologyHigh()));
                addRow(table, toDetailRowIfHasValue("Low", posology.getPosologyLow()));
                addRow(table, toDetailRowIfHasValue("Unit", Translator.translateAdministrationUnit(posology.getAdministrationUnit())));
                addRow(table, toDetailRowIfHasValue("Takes high", posology.getTakesHigh()));
                addRow(table, toDetailRowIfHasValue("Takes low", posology.getTakesLow()));
            }
        }

        if (medicationEntrySumehr.getRoute() != null) {
            addRow(table, toDetailRowIfHasValue("Route", StringUtils.joinWith(" ", "(NL)", translateRoute(medicationEntrySumehr.getRoute()))));
        }

        if (medicationEntrySumehr.getPosologyOrRegimen() instanceof Regimen) {
            Regimen regimen = (Regimen) medicationEntrySumehr.getPosologyOrRegimen();
            if (regimen != null) {
                if (regimen.getAdministrationUnit() != null) {
                    addRow(table, toDetailRowIfHasValue("Administration unit", StringUtils.joinWith(" ", "(NL)", translateAdministrationUnit(regimen.getAdministrationUnit()))));
                }
            }
            addRow(table, toDetailRowsIfHasValue(getRegimenEntries(regimen.getEntries())));
        }

        addRow(table, toDetailRowIfHasValue("Relevant", medicationEntrySumehr.getRelevant()));

        if (medicationEntrySumehr.getLifecycle() != null) {
            addRow(table, toDetailRowIfHasValue("Lifecycle", medicationEntrySumehr.getLifecycle().value()));
        }
        if (medicationEntrySumehr.getTemporality() != null) {
            addRow(table, toDetailRowIfHasValue("Temporality", medicationEntrySumehr.getTemporality().value()));
        }
        addRow(table, toDetailRowIfHasValue("Date of registration", Translator.formatAsDateTime(medicationEntrySumehr.getRecordDateTime())));
        return table;

    }



    private List<Pair> getRegimenEntries(List<RegimenEntry> regimenEntries) {
        List<Pair> existingRows = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(regimenEntries)) {
            return null;
        }

        for (RegimenEntry regimenEntry : regimenEntries) {
            String key = "Regimen item";
            String quantity = regimenEntry.getQuantity().toString();

            String daynumberOrDateOrWeekday = null;
            if (regimenEntry.getDaynumberOrDateOrWeekday() instanceof RegimenDaynumber) {
                RegimenDaynumber regimenDaynumber = (RegimenDaynumber) regimenEntry.getDaynumberOrDateOrWeekday();
                if (regimenDaynumber != null) {
                    daynumberOrDateOrWeekday = org.apache.commons.lang3.StringUtils.join("day ", regimenDaynumber.getNumber());
                }
            } else if (regimenEntry.getDaynumberOrDateOrWeekday() instanceof RegimenDate) {
                RegimenDate regimenDate = (RegimenDate) regimenEntry.getDaynumberOrDateOrWeekday();
                if (regimenDate != null) {
                    daynumberOrDateOrWeekday = formatAsDate(regimenDate.getDate());
                }
            } else if (regimenEntry.getDaynumberOrDateOrWeekday() instanceof RegimenWeekday) {
                RegimenWeekday regimenWeekday = (RegimenWeekday) regimenEntry.getDaynumberOrDateOrWeekday();
                if (regimenWeekday != null) {
                    daynumberOrDateOrWeekday = regimenWeekday.getWeekday().getValue();
                }
            }

            String dayperiodOrTime = null;
            if (regimenEntry.getDayperiodOrTime() instanceof RegimenDayperiod) {
                RegimenDayperiod dayperiod = (RegimenDayperiod) regimenEntry.getDayperiodOrTime();
                if (dayperiod != null) {
                    dayperiodOrTime = translateDayperiod(dayperiod.getDayperiod());
                }
            } else if (regimenEntry.getDayperiodOrTime() instanceof RegimenTime) {
                RegimenTime time = (RegimenTime) regimenEntry.getDayperiodOrTime();
                if (time != null) {
                    dayperiodOrTime = "at " + formatAsTime(time.getTime());
                }
            }
            existingRows.add(Pair.of(key, StringUtils.joinWith(" / ", daynumberOrDateOrWeekday, quantity, dayperiodOrTime)));
        }

        return existingRows;
    }

    private PdfPTable healthCareElementToTable(HealthCareElement healthCareElement) {

        PdfPTable table = initializeDetailTable();

        String name = collectText(healthCareElement.getContentTextTypes());

        addRow(table, createDetailHeader(name));

        if (CollectionsUtil.notEmptyOrNull(healthCareElement.getCdcontents())) {
            for (CDCONTENT cdcontent : healthCareElement.getCdcontents()) {
                addRow(table, cdContentToDetailRow(cdcontent));
            }
        }

        addRow(table, toDetailRowIfHasValue("Begin", healthCareElement.getBeginmoment()));
        addRow(table, toDetailRowIfHasValue("End", healthCareElement.getEndmoment()));
        addRow(table, toDetailRowIfHasValue("Text", collectText(healthCareElement.getContentTextTypes())));
        addRow(table, toDetailRowIfHasValue("Text", collectText(healthCareElement.getTextTypes())));
        addRow(table, toDetailRowIfHasValue("Relevant", healthCareElement.getIsRelevant()));
        if (healthCareElement.getLifecycle() != null) {
            addRow(table, toDetailRowIfHasValue("Lifecycle", healthCareElement.getLifecycle().value()));
        }
        return table;
    }

    private String translateCdContent(CDCONTENT cdcontent) {

        if (cdContentTranslations.containsKey(cdcontent.getS().value())) {
            return cdContentTranslations.get(cdcontent.getS().value());
        }

        return cdcontent.getS().value();
    }

    private List<PdfPCell> cdContentToDetailRow(CDCONTENT cdcontent) {

        if (cdcontent.getS() != null && cdContentToIgnore.contains(cdcontent.getS().value())) {
            return null;
        }

        String key = null;
        if (cdcontent.getS() != null) {
            if (!org.apache.commons.lang3.StringUtils.startsWithIgnoreCase(cdcontent.getS().value(), "CD-")) {
                key = StringUtils.joinFields(translateCdContent(cdcontent), cdcontent.getSV(), " ");
            } else {
                key = translateCdContent(cdcontent);
            }
        } else {
            key = "?";
        }

        String value = cdcontent.getValue();
        if (cdcontent.getDN() != null) {
            if (value != null) {
                value += " (" + cdcontent.getDN() + ")";
            } else {
                value = cdcontent.getDN();
            }
        }

        return createDetailRow(key, value);
    }

}