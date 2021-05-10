package org.imec.ivlab.datagenerator;

import static org.imec.ivlab.core.constants.CoreConstants.EXPORT_NAME_DAILYSCHEME;
import static org.imec.ivlab.core.constants.CoreConstants.EXPORT_NAME_DIARYNOTE_OVERVIEW;
import static org.imec.ivlab.core.constants.CoreConstants.EXPORT_NAME_GLOBALSCHEME;
import static org.imec.ivlab.core.constants.CoreConstants.EXPORT_NAME_SUMEHR_OVERVIEW;
import static org.imec.ivlab.core.constants.CoreConstants.EXPORT_NAME_VACCINATION_LIST_OVERVIEW;
import static org.imec.ivlab.core.constants.CoreConstants.EXPORT_NAME_VACCINATION_OVERVIEW;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.model.internal.mapper.medication.DailyScheme;
import org.imec.ivlab.core.model.internal.mapper.medication.GlobalScheme;
import org.imec.ivlab.core.model.internal.mapper.medication.MedicationEntry;
import org.imec.ivlab.core.model.internal.parser.diarynote.DiaryNote;
import org.imec.ivlab.core.model.internal.parser.diarynote.mapper.DiaryNoteMapper;
import org.imec.ivlab.core.model.internal.parser.sumehr.Sumehr;
import org.imec.ivlab.core.model.internal.parser.sumehr.mapper.SumehrMapper;
import org.imec.ivlab.core.model.internal.parser.vaccination.Vaccination;
import org.imec.ivlab.core.model.internal.parser.vaccination.mapper.VaccinationMapper;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.core.model.upload.msentrylist.MedicationSchemeExtractor;
import org.imec.ivlab.core.util.DateUtils;
import org.imec.ivlab.core.util.FileUtil;
import org.imec.ivlab.core.util.FilenameUtil;
import org.imec.ivlab.core.util.RandomGenerator;
import org.imec.ivlab.datagenerator.exporter.writer.FilenameBuilder;
import org.imec.ivlab.datagenerator.util.kmehrmodifier.SchemeHelper;
import org.imec.ivlab.viewer.converter.DailySchemeFilter;
import org.imec.ivlab.viewer.converter.TestFileConverter;
import org.imec.ivlab.viewer.converter.exceptions.SchemaConversionException;
import org.imec.ivlab.viewer.pdf.DiaryNoteWriter;
import org.imec.ivlab.viewer.pdf.MSWriter;
import org.imec.ivlab.viewer.pdf.SumehrWriter;
import org.imec.ivlab.viewer.pdf.VaccinationListWriter;
import org.imec.ivlab.viewer.pdf.VaccinationWriter;

public class SchemeExporter {
    //creates a schema based on the Kmehrmessage.
    private final static Logger log = Logger.getLogger(SchemeExporter.class);

    public static void generateMSGlobalScheme(File inputFile, Kmehrmessage kmehrmessage, Patient patient) {

        try {

            log.info("Generating global medication schema for " + patient.getId());

            KmehrEntryList kmehrEntryList = KmehrExtractor.getKmehrEntryList(inputFile);
            MSEntryList MSEntries = MedicationSchemeExtractor.getMedicationSchemeEntries(kmehrEntryList);

            File outputFile = new File(inputFile.getAbsolutePath());
            outputFile = FileUtil.appendTextToFilename(outputFile, "_" + EXPORT_NAME_GLOBALSCHEME);
            outputFile = FileUtil.getFileWithNewExtension(outputFile, "pdf");
            outputFile = FilenameUtil.chompIfTooLong(outputFile);

            List<MedicationEntry> medicationEntries = TestFileConverter.convertToMedicationEntries(MSEntries);

            GlobalScheme globalScheme = SchemeHelper.toGlobalScheme(kmehrmessage, patient);
            globalScheme.setMedicationEntries(medicationEntries);

            MSWriter schemeWriter = new MSWriter();
            try {
                schemeWriter.createPdf(globalScheme, outputFile.getAbsolutePath());
            } catch (SchemaConversionException e) {
                log.error("Failed to generate global scheme", e);
            }

            log.info("Global Scheme generation finished");

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

    }

    public static void generateSumehrOverview(File inputFile, Kmehrmessage kmehrmessage) {


        SumehrWriter sumehrWriter = new SumehrWriter();

        Sumehr sumehr = SumehrMapper.kmehrToSumehr(kmehrmessage);

        File outputFile = new File(inputFile.getAbsolutePath());
        outputFile = FileUtil.appendTextToFilename(outputFile, "_" + EXPORT_NAME_SUMEHR_OVERVIEW);
        outputFile = FileUtil.getFileWithNewExtension(outputFile, "pdf");
        outputFile = FilenameUtil.chompIfTooLong(outputFile);

        try {
            sumehrWriter.createPdf(sumehr, outputFile.getAbsolutePath());
        } catch (SchemaConversionException e) {
            log.error("Failed to generate sumehr overview", e);
        }


    }

    public static void generateDiaryNoteVisualization(File inputFile, Kmehrmessage kmehrmessage) {


        DiaryNoteWriter diaryNoteWriter = new DiaryNoteWriter();

        DiaryNote diaryNote = DiaryNoteMapper.kmehrToDiaryNote(kmehrmessage);

        File outputFile = new File(inputFile.getAbsolutePath());
        outputFile = FileUtil.appendTextToFilename(outputFile, "_" + EXPORT_NAME_DIARYNOTE_OVERVIEW);
        outputFile = FileUtil.getFileWithNewExtension(outputFile, "pdf");
        outputFile = FilenameUtil.chompIfTooLong(outputFile);

        try {
            diaryNoteWriter.createPdf(diaryNote, outputFile.getAbsolutePath());
        } catch (SchemaConversionException e) {
            log.error("Failed to generate diaryNote visualization", e);
        }


    }

    public static void generateVaccinationVisualization(File inputFile, Kmehrmessage kmehrmessage) {


        VaccinationWriter vaccinationWriter = new VaccinationWriter();

        Vaccination vaccination = VaccinationMapper.kmehrToVaccination(kmehrmessage);

        File outputFile = new File(inputFile.getAbsolutePath());
        outputFile = FileUtil.appendTextToFilename(outputFile, "_" + EXPORT_NAME_VACCINATION_OVERVIEW);
        outputFile = FileUtil.getFileWithNewExtension(outputFile, "pdf");
        outputFile = FilenameUtil.chompIfTooLong(outputFile);

        try {
            vaccinationWriter.createPdf(vaccination, outputFile.getAbsolutePath());
        } catch (SchemaConversionException e) {
            log.error("Failed to generate vaccination visualization", e);
        }


    }

    public static void generateVaccinationListVisualization(Patient patient, File directory, List<Kmehrmessage> kmehrmessages) {

        String fileName = createFilename(patient);

        VaccinationListWriter vaccinationListWriter = new VaccinationListWriter();

        List<Vaccination> vaccinations = kmehrmessages
            .stream()
            .map(VaccinationMapper::kmehrToVaccination)
            .collect(Collectors.toList());

        File outputFile = new File(directory.getAbsolutePath());
        outputFile = FileUtil.appendTextToFilename(outputFile,File.separator + fileName);
        outputFile = FileUtil.appendTextToFilename(outputFile, "_" + EXPORT_NAME_VACCINATION_LIST_OVERVIEW);
        outputFile = FileUtil.getFileWithNewExtension(outputFile, "pdf");
        outputFile = FilenameUtil.chompIfTooLong(outputFile);

        try {
            vaccinationListWriter.createPdf(vaccinations, outputFile.getAbsolutePath());
        } catch (SchemaConversionException e) {
            log.error("Failed to generate vaccination visualization", e);
        }


    }

    private static String createFilename(Patient patient) {
        FilenameBuilder filenameBuilder = new FilenameBuilder();
        filenameBuilder
            .add(DateUtils.formatDate(DateUtils.getDate(), DateUtils.DEFAULT_DATETIME_FORMAT_SHORT))
            .add(StringUtils.lowerCase(patient.getFirstName()))
            .add(RandomGenerator.getBase52(3));
        return filenameBuilder.toString();
    }

    public static void generateMSDailyScheme(File inputFile, Kmehrmessage kmehrmessage, LocalDate medicationSchemeDate, Patient patient) {

        try {

            log.info("Generating daily medication schema for " + patient.getId() + " and date: " + medicationSchemeDate);

            KmehrEntryList kmehrEntryList = KmehrExtractor.getKmehrEntryList(inputFile);
            MSEntryList MSEntries = MedicationSchemeExtractor.getMedicationSchemeEntries(kmehrEntryList);

            File outputFile = new File(inputFile.getAbsolutePath());
            outputFile = FileUtil.appendTextToFilename(outputFile, "_" + EXPORT_NAME_DAILYSCHEME + "-" + medicationSchemeDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            outputFile = FileUtil.getFileWithNewExtension(outputFile, "pdf");
            outputFile = FilenameUtil.chompIfTooLong(outputFile);

            List<MedicationEntry> medicationEntries = TestFileConverter.convertToMedicationEntries(MSEntries);

            DailyScheme dailyScheme = SchemeHelper.toDailyScheme(kmehrmessage, patient, medicationSchemeDate);
            dailyScheme.setMedicationEntries(DailySchemeFilter.filterMedicationForSchemeDate(medicationSchemeDate, medicationEntries));

            MSWriter schemeWriter = new MSWriter();
            try {
                schemeWriter.createPdf(dailyScheme, outputFile.getAbsolutePath());
            } catch (SchemaConversionException e) {
                log.error("Failed to generate daily scheme", e);
            }

            log.info("Daily Scheme generation finished");

        } catch (Throwable t) {

            log.error("Error while generating daily scheme", t);

        }

    }

}
