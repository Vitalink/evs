package org.imec.ivlab.datagenerator.uploader;

import org.apache.commons.io.FilenameUtils;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.util.DateUtils;
import org.imec.ivlab.core.util.FilenameUtil;
import org.imec.ivlab.datagenerator.uploader.model.action.Action;
import org.imec.ivlab.datagenerator.uploader.model.State;

import java.io.File;
import java.nio.file.Path;

public class UploaderHelper {

    public static File getLocationOfProcessedFolder(File rootFolder) {
        return new File(rootFolder, State.PROCESSED.getValue());
    }

    public static File getProcessedLocationForInstruction(File rootFolder, File uploadFile, String patientKey, String actorId, Action action, TransactionType transactionType) {

        Path uploadLocation = uploadFile.toPath();

        Path inputFileRelativeToRoot = rootFolder.toPath().relativize(uploadLocation);

        String actualFilename = inputFileRelativeToRoot.getName(inputFileRelativeToRoot.getNameCount() - 1).toString();

        String newFilename = DateUtils.formatDateTime(DateUtils.getDate(), DateUtils.DEFAULT_DATETIME_FORMAT_SHORT) + "_" + transactionType.getOneLetterSummary() + "_" + getParameterInfoString(patientKey, actorId, action) + FilenameUtils.getBaseName(actualFilename) + ".inp";

        String processedLocation = getLocationOfProcessedFolder(rootFolder).getAbsolutePath() + File.separator + newFilename;

        return FilenameUtil.chompIfTooLong(new File(processedLocation));

    }

    private static String getParameterInfoString(String patientId, String actorId, Action action) {

        StringBuilder sb = new StringBuilder(patientId).append("_");
        sb.append(actorId).append("_");
        sb.append(action.getValue()).append("_");
        return sb.toString();

    }

}
