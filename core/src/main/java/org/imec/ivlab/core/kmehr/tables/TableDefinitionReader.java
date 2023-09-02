package org.imec.ivlab.core.kmehr.tables;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.kmehr.tables.exception.CsvParseException;
import org.imec.ivlab.core.kmehr.tables.exception.IncorrectTableVersionConfigurationException;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.IOUtils;
import org.imec.ivlab.core.util.OSUtils;
import org.imec.ivlab.core.util.ResourceResolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TableDefinitionReader {

    private static TableDefinitionReader instance;

    private static Map<String, List<TableDefinition>> definitionsPerLocation = new HashMap<>();

    private final static Logger log = LogManager.getLogger(TableDefinitionReader.class);

    private static final String EXPRESSION_RANGE = "^(\\d+(?:\\.\\d+)?)(?::)(\\d+)(?:->)(\\d+)$";

    private static final String TABLE_NAME_MARKER = "-----";


    public static TableDefinitionReader getInstance() {

        if (instance == null) {
            instance = new TableDefinitionReader();
        }

        return instance;

    }

    private TableDefinitionReader() {

    }

    public List<TableDefinition> readDefinitions(String pathToTableVersionsFolder, String pathToTablesFolder) {

        if (definitionsPerLocation.containsKey(pathToTableVersionsFolder + pathToTablesFolder)) {
            return definitionsPerLocation.get(pathToTableVersionsFolder + pathToTablesFolder);
        } else {
            List<File> tableVersionsFiles = getTableVersionsFiles(pathToTableVersionsFolder);
            List<File> tableFiles = getTableFiles(pathToTablesFolder);

            List<TableDefinition> tableDefinitions = toTableDefinitions(tableVersionsFiles, tableFiles);

            definitionsPerLocation.put(pathToTableVersionsFolder + pathToTablesFolder, tableDefinitions);

            return definitionsPerLocation.get(pathToTableVersionsFolder + pathToTablesFolder);

        }

    }

    private List<TableDefinition> toTableDefinitions(List<File> tableVersionsFiles, List<File> tableFiles) {

        List<TableDefinition> tableDefinitions = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(tableFiles)) {
            return tableDefinitions;
        }

        for (File tableFile : tableFiles) {
            File tableVersionsFile = getCorrespondingVersionsFile(tableFile, tableVersionsFiles);

            TableDefinition tableDefinition = createTableDefinition(tableFile, tableVersionsFile);
            tableDefinitions.add(tableDefinition);
        }

        return tableDefinitions;

    }

    private TableDefinition createTableDefinition(File tableFile, File tableVersionsFile) {

        TableDefinition tableDefinition = new TableDefinition();

        tableDefinition.setName(StringUtils.upperCase(getTableName(tableFile)));
        tableDefinition.setCodesPerVersion(getCodesPerVersion(tableFile, tableVersionsFile));

        return tableDefinition;

    }

    private Map<String, List<CodeDefinition>> getCodesPerVersion(File tableFile, File tableVersionsFile) {


        Map<String, List<CodeDefinition>> codesPerVersion = new HashMap<>();

        if (tableVersionsFile == null) {
            log.error("No table versions file supplied");
            return codesPerVersion;
        }

        List<Range> ranges = collectRanges(tableVersionsFile);
        List<CodeDefinition> allCodeDefinitions = collectCodeDefinitions(tableFile);

        for (Range range : ranges) {

            List<CodeDefinition> codeDefinitions = new ArrayList<>();

            for (CodeDefinition codeDefinition : allCodeDefinitions) {
                if (codeDefinition.getDefinitionNumber() >= range.getStart() && codeDefinition.getDefinitionNumber() <= range.getEnd()) {
                    codeDefinitions.add(codeDefinition);
                }
            }

            codesPerVersion.put(range.getVersion(), codeDefinitions);

        }

        return codesPerVersion;

    }

    private String getTableName(File tableFile) {
        return StringUtils.substringAfterLast(FilenameUtils.getBaseName(tableFile.getName()), TABLE_NAME_MARKER);
    }

    private File getCorrespondingVersionsFile(File tableFile, List<File> tableVersionsFiles) {

        String tableName = getTableName(tableFile);

        if (CollectionsUtil.notEmptyOrNull(tableVersionsFiles)) {

            for (File tableVersionsFile : tableVersionsFiles) {
                if (StringUtils.endsWithIgnoreCase(FilenameUtils.getBaseName(tableVersionsFile.getName()), tableName)) {
                    return tableVersionsFile;
                }
            }

        }

        log.warn("No table versions file found with name: " + tableName);

        return null;

    }

    private List<Range> collectRanges(File tableVersionsFile) {

        log.trace("Collecting ranges from file: " + tableVersionsFile.getAbsolutePath());

        List<Range> ranges = new ArrayList<>();

        List<String> rangeStrings = null;
        try {
            rangeStrings = FileUtils.readLines(tableVersionsFile, "UTF-8");
        } catch (IOException e) {
            throw new IncorrectTableVersionConfigurationException("Failed to read table versions file located at: " + tableVersionsFile);
        }

        if (CollectionsUtil.emptyOrNull(rangeStrings)) {
            return ranges;
        }

        for (String rangeString : rangeStrings) {
            ranges.add(toRange(rangeString));
        }

        return ranges;

    }

    private Range toRange(String rangeString) {

        Pattern pattern = Pattern.compile(EXPRESSION_RANGE, Pattern.DOTALL + Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(rangeString);

        if (matcher.find()) {
            Range range = new Range();
            range.setVersion(matcher.group(1));
            range.setStart(Long.parseLong(matcher.group(2)));
            range.setEnd(Long.parseLong(matcher.group(3)));
            return range;
        } else {
            throw new IncorrectTableVersionConfigurationException("Invalid range specified: " + rangeString);
        }

    }

    private List<CodeDefinition> collectCodeDefinitions(File tableFile) {

        log.trace("Collecting code definitions from: " + tableFile.getAbsolutePath());

        List<CodeDefinition> codeDefinitions = new ArrayList<>();

        Reader in = null;
        try {
            in = new FileReader(tableFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to read table file located at: " + tableFile.getAbsolutePath());
        }
        Iterable<CSVRecord> records;
        try {
            records = CSVFormat.EXCEL.builder().setDelimiter(';').setHeader().setSkipHeaderRecord(true).build().parse(in);
        } catch (IOException e) {
            throw new CsvParseException("Failed to parse table file at: " + tableFile.getAbsolutePath(), e);
        }

        for (CSVRecord record : records) {
            CodeDefinition definition = new CodeDefinition();
            definition.setCode(record.get("Code"));
            definition.setMeaningDutch(record.get("Meaning Dutch"));
            definition.setMeaningEnglish(record.get("Meaning English"));
            definition.setMeaningFrench(record.get("Meaning French"));
            definition.setMeaningGerman(record.get("Meaning German"));
            definition.setSnomedCt(record.get("SNOMED-CT"));
            definition.setDefinitionNumber(record.getRecordNumber() + 1);
            codeDefinitions.add(definition);
        }

        return codeDefinitions;

    }


    private List<File> getFilesWithSuffix(String folderToScan, String fileEndsWith) {

        List<File> files = new ArrayList<>();

        String fileSeparatorInJarFile = "/";

        log.trace("Will try to find this folder on disk: " + folderToScan);
        File dirOnDisk = null;
        try {
            dirOnDisk = IOUtils.getFile(this.getClass().getClassLoader(), folderToScan);
        } catch (RuntimeException e) {

        }
        if (dirOnDisk != null && dirOnDisk.exists()) {
            log.trace("folder exists on disk: " + folderToScan);
            try {
                Files.list(Paths.get(dirOnDisk.getAbsolutePath())).filter(path ->  StringUtils.endsWith(path.toString(), fileEndsWith)).forEach(path -> files.add(
                        IOUtils.getResourceAsFile(path.toString(), TABLE_NAME_MARKER + StringUtils.substringAfterLast(path.toString(), File.separator))

                ));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            log.trace("Folder does not exist on disk. Will look in jar file for table definitions using path: " + folderToScan);
            List<String> matchingJarEntries = ResourceResolver.getMatchingJarEntries(OSUtils.adaptFilePath(folderToScan, fileSeparatorInJarFile));
            if (matchingJarEntries != null) {
                log.trace("Found " + matchingJarEntries.size() + " jar entries.");
                for (String matchingJarEntry : matchingJarEntries) {
                    String tempFileName = TABLE_NAME_MARKER + StringUtils.substringAfterLast(matchingJarEntry, fileSeparatorInJarFile);
                    log.trace("Adding matching jar entry: " + matchingJarEntry + ". Name to be used for temp file: " + tempFileName);
                    File resourceAsFile = IOUtils.getResourceAsFile(fileSeparatorInJarFile + matchingJarEntry, tempFileName);
                    log.trace("resource as file: " + resourceAsFile.getAbsolutePath());
                    files.add(resourceAsFile);

                }

            }

        }

        log.info("Found " + files.size() + " files at location: " + folderToScan);

        return files;

    }

    private List<File> getTableVersionsFiles(String pathToTableVersionsFolder) {

        return getFilesWithSuffix(pathToTableVersionsFolder, ".txt");

    }

    private List<File> getTableFiles(String pathToTablesFolder) {

        return getFilesWithSuffix(pathToTablesFolder, ".csv");


    }

}
