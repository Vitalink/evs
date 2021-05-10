package org.imec.ivlab.validator;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.imec.ivlab.core.jcommander.PathToFileConverter;
import org.imec.ivlab.core.jcommander.SemicolonSeparatedStringListConverter;

import java.io.File;
import java.util.List;

@Parameters(separators = "=")
public class BatchKmehrValidatorArguments {

    @Parameter(names = "-scanRootDir", description = "The root directory in which to start scanning", required = true, converter = PathToFileConverter.class)
    private File scanRootDir;

    @Parameter(names = "-reportDir", description = "The directory to store the reports in", required = true, converter = PathToFileConverter.class)
    private File reportDir;

    @Parameter(names = "-xsdErrorMessagesToIgnore", description = "Semicolon separated list of xsd failure messages to ignore", converter = SemicolonSeparatedStringListConverter.class)
    private List<String> xsdErrorMessagesToIgnore;

    @Parameter(names = "-ruleIdsToIgnore", description = "Semicolon separated list of rule id's tl ignore", converter = SemicolonSeparatedStringListConverter.class)
    private List<String> ruleIdsToIgnore;

    public File getScanRootDir() {
        return scanRootDir;
    }

    public void setScanRootDir(File scanRootDir) {
        this.scanRootDir = scanRootDir;
    }

    public File getReportDir() {
        return reportDir;
    }

    public void setReportDir(File reportDir) {
        this.reportDir = reportDir;
    }

    public List<String> getXsdErrorMessagesToIgnore() {
        return xsdErrorMessagesToIgnore;
    }

    public void setXsdErrorMessagesToIgnore(List<String> xsdErrorMessagesToIgnore) {
        this.xsdErrorMessagesToIgnore = xsdErrorMessagesToIgnore;
    }

    public List<String> getRuleIdsToIgnore() {
        return ruleIdsToIgnore;
    }

    public void setRuleIdsToIgnore(List<String> ruleIdsToIgnore) {
        this.ruleIdsToIgnore = ruleIdsToIgnore;
    }
}
