package org.imec.ivlab.validator;

import com.beust.jcommander.JCommander;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.util.DateUtils;
import org.imec.ivlab.core.util.FileUtil;
import org.imec.ivlab.validator.exceptions.ValidatorException;
import org.imec.ivlab.validator.report.FileWithValidatedKmehrsFormatter;
import org.imec.ivlab.validator.scanner.FolderKmehrScanner;
import org.imec.ivlab.validator.scanner.model.FileWithKmehrs;
import org.imec.ivlab.validator.scanner.model.FileWithValidatedKmehrs;
import org.imec.ivlab.validator.validators.KmehrValidator;
import org.imec.ivlab.validator.validators.business.BusinessValidator;
import org.imec.ivlab.validator.validators.xsd.XsdValidator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BatchKmehrValidator {

    private static Logger LOG = LogManager.getLogger(FolderKmehrScanner.class);


    public static void main(String[] args) throws ValidatorException {

        String[] commandlineArguments = args;
        final BatchKmehrValidatorArguments folderKmehrValidatorArguments = new BatchKmehrValidatorArguments();
        // parse the arguments
        final JCommander commander = new JCommander(folderKmehrValidatorArguments);
        commander.setAcceptUnknownOptions(false);
        commander.parse(commandlineArguments);

        BatchKmehrValidator validator = new BatchKmehrValidator();
        validator.validate(folderKmehrValidatorArguments);

    }

    private void validateRootFolder(File file) {
        if (!file.exists()) {
            throw new RuntimeException("Folder doesn't exist: " + file.getAbsolutePath());
        }

        if (!file.isDirectory()) {
            throw new RuntimeException("Specified path is not of type directory: " + file.getAbsolutePath());
        }
    }

    /**
     * Validates all kmehr messages in a folder, recursively.
     * @param arguments
     * @return a file pointing to the validation report
     * @throws ValidatorException
     */
    public File validate(BatchKmehrValidatorArguments arguments) throws ValidatorException {

        validateRootFolder(arguments.getScanRootDir());

        LOG.info("Start validation of kmehrs in root folder: " + arguments.getScanRootDir());

        boolean recursive = true;
        String[] extensions = new String[] {"txt", "xml", "inp", "exp"};

        FolderKmehrScanner scanner = new FolderKmehrScanner();
        ArrayList<FileWithKmehrs> filesWithKmehrs = scanner.scanFolders(arguments.getScanRootDir(), extensions, recursive);

        KmehrValidator validator = new KmehrValidator();
        validator.setBusinessValidator(new BusinessValidator(arguments.getRuleIdsToIgnore()));
        validator.setXsdValidator(new XsdValidator(arguments.getXsdErrorMessagesToIgnore()));
        List<FileWithValidatedKmehrs> filesWithValidatedKmehrs = validator.performValidation(filesWithKmehrs);

        File reportFile = writeReport(arguments.getScanRootDir(), arguments.getReportDir(), filesWithValidatedKmehrs);

        LOG.info("Wrote validation report to: " + reportFile);

        return reportFile;

    }

    private File writeReport(File rootFolder, File reportDir, List<FileWithValidatedKmehrs> filesWithValidatedKmehrs) throws ValidatorException {

        FileUtil.createDirectoriesRecursively(reportDir.getAbsolutePath());

        String extension = ".txt";
        File reportFile = new File(reportDir.getAbsolutePath() + File.separator + "EVS-validationreport_" + DateUtils.formatDate(DateUtils.getDate(), "yyyy-MM-dd_HH-mm-ss") + extension);
        File reportFileUniqueName = new File(reportDir.getAbsolutePath() + File.separator + "EVS-validationreport" + extension);

        StringBuffer sb = new StringBuffer();
        sb.append(System.lineSeparator());
        sb.append("Validation results for folder: " + rootFolder);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(FileWithValidatedKmehrsFormatter.getOutput(filesWithValidatedKmehrs));
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());

        sb.append("All validated files:");
        sb.append(System.lineSeparator());

        for (FileWithValidatedKmehrs fileWithValidatedKmehrs : filesWithValidatedKmehrs) {
            sb.append(fileWithValidatedKmehrs.getFile().getAbsolutePath());
            sb.append(System.lineSeparator());
        }

        try {
            FileUtils.writeStringToFile(reportFileUniqueName, sb.toString(), "UTF-8");
            FileUtils.writeStringToFile(reportFile, sb.toString(), "UTF-8");
        } catch (IOException e) {
            throw new ValidatorException("Error while writing report", e);
        }

        return reportFile;

    }






}
