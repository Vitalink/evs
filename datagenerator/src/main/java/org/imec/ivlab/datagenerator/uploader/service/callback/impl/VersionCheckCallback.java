package org.imec.ivlab.datagenerator.uploader.service.callback.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.util.DateUtils;
import org.imec.ivlab.core.version.VersionCheckResult;
import org.imec.ivlab.core.version.VersionManager;
import org.imec.ivlab.datagenerator.uploader.UploaderHelper;
import org.imec.ivlab.datagenerator.uploader.exception.CallbackException;
import org.imec.ivlab.datagenerator.uploader.service.callback.Callback;
import org.imec.ivlab.datagenerator.util.kmehrmodifier.VersionWriter;

import java.io.File;

public class VersionCheckCallback implements Callback {

    private final static Logger LOG = LogManager.getLogger(VersionCheckCallback.class);

    private File rootFolder;

    public VersionCheckCallback(File rootFolder) {
        this.rootFolder = rootFolder;
    }

    @Override
    public void pass() throws CallbackException {

        writeVersionInfoFileIfApplicable(UploaderHelper.getLocationOfProcessedFolder(rootFolder));

    }

    @Override
    public void fail(String message) throws CallbackException {

        writeVersionInfoFileIfApplicable(UploaderHelper.getLocationOfProcessedFolder(rootFolder));

    }


    private void writeVersionInfoFileIfApplicable(File outputDirectory) {

        try {
            VersionCheckResult versionCheckResult = VersionManager.getVersionCheckResult();
            File versionOutputFile = new File(
                    outputDirectory + File.separator + DateUtils.formatDate(DateUtils.getDate(), DateUtils.DEFAULT_DATETIME_FORMAT_SHORT) + VersionWriter.VERSION_FILE_NAME_PART);
            VersionWriter.writeVersionInfoFileIfOutdated(versionOutputFile, versionCheckResult);
        } catch (Exception e) {
            LOG.error(e);
        }

    }


}
