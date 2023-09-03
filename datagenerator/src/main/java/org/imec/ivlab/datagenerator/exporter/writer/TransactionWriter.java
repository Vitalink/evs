package org.imec.ivlab.datagenerator.exporter.writer;

import be.fgov.ehealth.hubservices.core.v3.GetTransactionResponse;
import be.fgov.ehealth.hubservices.core.v3.Latestupdate;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.kmehr.model.util.FolderUtil;
import org.imec.ivlab.core.kmehr.model.util.HCPartyUtil;
import org.imec.ivlab.core.kmehr.model.util.IDKmehrUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.kmehr.model.localid.LocalId;
import org.imec.ivlab.core.kmehr.model.localid.LocalIdParser;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.DateUtils;
import org.imec.ivlab.core.util.FileUtil;
import org.imec.ivlab.core.util.FilenameUtil;
import org.imec.ivlab.core.util.RandomGenerator;
import org.imec.ivlab.datagenerator.exporter.export.ExportResult;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TransactionWriter extends AbstractWriter implements Writer<GetTransactionResponse> {

    private final static Logger LOG = LogManager.getLogger(TransactionWriter.class);

    protected final static String DEFAULT_EXTENSION = "exp";

    private org.imec.ivlab.core.model.upload.TransactionType transactionType;
    private CDTRANSACTIONvalues cdtransactioNvalues;


    public TransactionWriter(org.imec.ivlab.core.model.upload.TransactionType transactionType, CDTRANSACTIONvalues cdtransactioNvalues) {
        this.transactionType = transactionType;
        this.cdtransactioNvalues = cdtransactioNvalues;
    }

    @Override
    public List<ExportResult<GetTransactionResponse>> write(Patient patient, List<GetTransactionResponse> getTransactionResponses, List<Latestupdate> latestupdates, File outputDirectory, String filename, String extension) {

        return writeResponses(patient, getTransactionResponses, latestupdates, outputDirectory, filename, extension);

    }

    protected File createOutputFile(File outputDirectory, String filename, String extension) {
        FileUtil.createDirectoriesRecursively(outputDirectory.getAbsolutePath());
        if (StringUtils.isBlank(extension)) {
            extension = DEFAULT_EXTENSION;
        }
        return FilenameUtil.chompIfTooLong(new File(outputDirectory + File.separator + filename + "." + extension));
    }


    protected List<ExportResult<GetTransactionResponse>> writeResponses(Patient patient, List<GetTransactionResponse> getTransactionResponses, List<Latestupdate> latestupdates, File outputDirectory, String filename, String extension) {

        List<ExportResult<GetTransactionResponse>> results = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(getTransactionResponses)) {
            if (filename == null) {
                results.add(writeResponse(null, outputDirectory, generateFilenameForEmptyFile(patient, getLatestUpdateVersion(latestupdates)), extension));
            } else {
                results.add(writeResponse(null, outputDirectory, filename, extension));
            }
            return results;
        }


        for (int i = 0; i < getTransactionResponses.size(); i++) {
            String filenameTemp = null;

            GetTransactionResponse getTransactionResponse = getTransactionResponses.get(i);
            Kmehrmessage kmehrmessage = getTransactionResponse == null ? null : getTransactionResponse.getKmehrmessage();

            if (filename == null) {
                if (kmehrmessage == null) {
                    filenameTemp = generateFilenameForEmptyFile(patient, getLatestUpdateVersion(latestupdates));
                } else {
                    filenameTemp = generateFilename(patient, getLatestUpdateVersion(latestupdates), kmehrmessage);
                }
            } else {
                filenameTemp = filename;
            }

            filenameTemp = filenameTemp + "_tr-" + (i + 1) + "of" + getTransactionResponses.size();
            filenameTemp = filenameTemp + "_" + getLocalId(kmehrmessage);
            results.add(writeResponse(getTransactionResponses.get(i), outputDirectory, filenameTemp, extension));

        }

        return results;

    }



    protected ExportResult<GetTransactionResponse> writeResponse(GetTransactionResponse getTransactionResponse, File outputDirectory, String filename, String extension) {

        File outputFile = createOutputFile(outputDirectory, filename, extension);

        try {
            FileUtils.writeStringToFile(outputFile, getExportContent(getTransactionResponse), Charset.defaultCharset());
            LOG.info("Exported to: " + outputFile);
            return new ExportResult<>(getTransactionResponse, outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    protected String getExportContent(GetTransactionResponse getTransactionResponse) {

        if (getTransactionResponse == null || getTransactionResponse.getKmehrmessage() == null) {
            return "";
        }

        return formatKmehrMessage(getTransactionResponse.getKmehrmessage());

    }

    protected String getLatestUpdateVersion(List<Latestupdate> latestupdates) {
        if (CollectionsUtil.emptyOrNull(latestupdates)) {
            throw new RuntimeException("Couldn't find the version in the list of latest updates");
        }
        return latestupdates.get(0).getVersion();
    }

    protected String getLocalId(Kmehrmessage kmehrmessage) {

        if (kmehrmessage == null || CollectionsUtil.emptyOrNull(kmehrmessage.getFolders())) {
            return null;
        }

        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessage);
        if (folderType == null || CollectionsUtil.emptyOrNull(folderType.getTransactions())) {
            return null;
        }

        TransactionType transactionType = folderType.getTransactions().get(0);

        if (transactionType == null) {
            return null;
        }

        List<IDKMEHR> idKmehrs = IDKmehrUtil.getIDKmehrs(transactionType.getIds(), IDKMEHRschemes.LOCAL);

        List<LocalId> localIds = LocalIdParser.parseLocalIds(idKmehrs);
        List<String> localIDStrings = new ArrayList<>();
        if (CollectionsUtil.emptyOrNull(localIds)) {
            return null;
        }

        for (LocalId localId : localIds) {
            localIDStrings.add(localId.getValue());
        }

        return org.imec.ivlab.core.util.StringUtils.joinWith("-", localIDStrings.toArray());

    }

    protected String getLatestUpdateString(TransactionType transactionType) {

        if (transactionType == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        if (transactionType.getDate() != null) {
            String dateString = DateUtils.toLocalDate(transactionType.getDate()).format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
            sb.append(dateString);
            if (transactionType.getTime() != null) {
                String timeString = DateTimeFormat.forPattern("HHmmss").print(transactionType.getTime());
                sb.append("_");
                sb.append(timeString);
            }
        }

        if (transactionType.getAuthor() != null && CollectionsUtil.notEmptyOrNull(transactionType.getAuthor().getHcparties())) {

            HcpartyType HcParty;

            HcpartyType hubHcParty = HCPartyUtil.findHubHcParty(transactionType.getAuthor().getHcparties());
            if (hubHcParty != null && transactionType.getAuthor().getHcparties().size() > 1) {
                HcParty = transactionType.getAuthor().getHcparties().get(1);
            } else {
                HcParty = hubHcParty;
            }

            String author = (HcParty != null) 
                ? org.imec.ivlab.core.util.StringUtils.joinWith("-", HcParty.getFirstname(), HcParty.getFamilyname(), HcParty.getName())
                : "";
            if (StringUtils.isNotBlank(author)) {
                if (sb.length() > 0) {
                    sb.append("-");
                } else {
                    sb.append("_");
                }
                sb.append(author);
            }
        }

        return sb.toString();

    }

    public org.imec.ivlab.core.model.upload.TransactionType getTransactionType() {
        return transactionType;
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

    private String generateFilename(Patient patient, String latestUpdateVersion, Kmehrmessage kmehrmessage) {

        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessage);
        TransactionType transaction = FolderUtil.getTransaction(folderType, cdtransactioNvalues);

        FilenameBuilder filenameBuilder = new FilenameBuilder();
        filenameBuilder
                .add(DateUtils.formatDate(DateUtils.getDate(), DateUtils.DEFAULT_DATETIME_FORMAT_SHORT))
                .add(getTransactionType().getOneLetterSummary())
                .add(latestUpdateVersion)
                .add(StringUtils.lowerCase(patient.getFirstName()))
                .add(getLatestUpdateString(transaction))
                .add(RandomGenerator.getBase52(3));
        return filenameBuilder.toString();

    }
}
