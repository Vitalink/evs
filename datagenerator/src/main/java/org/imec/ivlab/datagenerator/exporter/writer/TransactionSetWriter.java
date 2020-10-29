package org.imec.ivlab.datagenerator.exporter.writer;

import be.fgov.ehealth.hubservices.core.v3.GetTransactionSetResponse;
import be.fgov.ehealth.hubservices.core.v3.Latestupdate;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.kmehr.model.util.FolderUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.DateUtils;
import org.imec.ivlab.core.util.FileUtil;
import org.imec.ivlab.core.util.FilenameUtil;
import org.imec.ivlab.core.util.RandomGenerator;
import org.imec.ivlab.datagenerator.exporter.export.ExportResult;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TransactionSetWriter extends AbstractWriter implements SetWriter<GetTransactionSetResponse> {

    private final static Logger LOG = Logger.getLogger(TransactionSetWriter.class);

    protected final static String DEFAULT_EXPORT_EXTENSION = "exp";
    protected final static String DEFAULT_PDF_EXTENSION = "pdf";

    private org.imec.ivlab.core.model.upload.TransactionType transactionType;

    public TransactionSetWriter(org.imec.ivlab.core.model.upload.TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public ExportResult<GetTransactionSetResponse> write(Patient patient, GetTransactionSetResponse getTransactionSetResponse, List<Latestupdate> latestupdates, File outputDirectory, String filename, String extension) {

        if (filename == null) {
            if (getTransactionSetResponse != null && getTransactionSetResponse.getKmehrmessage() != null) {
                filename = generateFilename(patient, getTransactionSetResponse.getKmehrmessage());
            } else {
                filename = generateFilenameForEmptyFile(patient, getLatestUpdateVersion(latestupdates));
            }
        }

        if (StringUtils.isBlank(extension)) {
            extension = DEFAULT_EXPORT_EXTENSION;
        }

        return writeExportFile(getTransactionSetResponse, outputDirectory, filename, extension);

    }

    @Override
    public ExportResult<GetTransactionSetResponse> writePdf(Patient patient, byte[] pdfContent, File outputDirectory, String filename, String extension) {

        if (filename == null) {
            throw new NullArgumentException("A filename for the pdf must be specified");
        }

        if (StringUtils.isBlank(extension)) {
            extension = DEFAULT_PDF_EXTENSION;
        }

        return writePdfFile(pdfContent, outputDirectory, filename, extension);

    }

    protected ExportResult<GetTransactionSetResponse> writeExportFile(GetTransactionSetResponse getTransactionSetResponse, File outputDirectory, String filename, String extension) {

        File outputFile = createOutputFile(outputDirectory, filename, extension);

        try {
            FileUtils.writeStringToFile(outputFile, getExportContent(getTransactionSetResponse));
            LOG.info("Exported to: " + outputFile);
            return new ExportResult<>(getTransactionSetResponse, outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    protected ExportResult<GetTransactionSetResponse> writePdfFile(byte[] pdfContent, File outputDirectory, String filename, String extension) {

        File outputFile = createOutputFile(outputDirectory, filename, extension);

        try {
            FileUtils.writeByteArrayToFile(outputFile, pdfContent);
            LOG.info("Exported pdf to: " + outputFile);
            return new ExportResult<>(null, outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String generateFilenameForEmptyFile(Patient patient, String version) {

        FilenameBuilder filenameBuilder = new FilenameBuilder();
        filenameBuilder
                .add(DateUtils.formatDate(DateUtils.getDate(), DateUtils.DEFAULT_DATETIME_FORMAT_SHORT))
                .add(getTransactionType().getOneLetterSummary())
                .add(version)
                .add(StringUtils.lowerCase(patient.getFirstName()))
                .add("empty")
                .add(RandomGenerator.getBase52(3));
        return filenameBuilder.toString();

    }

    private String generateFilename(Patient patient, Kmehrmessage kmehrmessage) {

        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessage);
        TransactionType transactionMS = FolderUtil.getTransaction(folderType, CDTRANSACTIONvalues.MEDICATIONSCHEME);
        List<TransactionType> transactionsMSE = FolderUtil.getTransactions(folderType, CDTRANSACTIONvalues.MEDICATIONSCHEMEELEMENT);

        FilenameBuilder filenameBuilder = new FilenameBuilder();
        filenameBuilder
                .add(DateUtils.formatDate(DateUtils.getDate(), DateUtils.DEFAULT_DATETIME_FORMAT_SHORT))
                .add(getTransactionType().getOneLetterSummary())
                .add(transactionMS.getVersion())
                .add(StringUtils.lowerCase(patient.getFirstName()))
                .add(getLatestUpdateString(transactionMS))
                .add("size-" + transactionsMSE.size())
                .add(RandomGenerator.getBase52(3));
        return filenameBuilder.toString();

    }

    protected File createOutputFile(File outputDirectory, String filename, String extension) {
        FileUtil.createDirectoriesRecursively(outputDirectory.getAbsolutePath());
        return FilenameUtil.chompIfTooLong(new File(outputDirectory + File.separator + filename + "." + extension));
    }

    protected String getExportContent(GetTransactionSetResponse getTransactionSetResponse) {
        if (getTransactionSetResponse == null || getTransactionSetResponse.getKmehrmessage() == null) {
            return "";
        }

        return formatKmehrMessage(getTransactionSetResponse.getKmehrmessage());

    }

    protected String getLatestUpdateVersion(List<Latestupdate> latestupdates) {
        if (CollectionsUtil.emptyOrNull(latestupdates)) {
            throw new RuntimeException("Couldn't find the version in the list of latest updates");
        }
        return latestupdates.get(0).getVersion();
    }


    public org.imec.ivlab.core.model.upload.TransactionType getTransactionType() {
        return transactionType;
    }
}
