package org.imec.ivlab.core.model.patient;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.data.PatientKey;
import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.model.patient.exceptions.PatientDataConfigurationInvalidException;
import org.imec.ivlab.core.model.patient.exceptions.PatientDataNotFoundException;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.util.IOUtils;
import org.imec.ivlab.core.util.JAXBUtils;
import org.imec.ivlab.core.util.ResourceResolver;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.imec.ivlab.core.constants.CoreConstants.PATH_TO_PATIENT_CONFIG_FOLDER;

public class PatientReader {

    private final static Logger log = Logger.getLogger(PatientReader.class);

    private static Set<String> scanPaths = new HashSet<>();


    static {
        addScanPath();
    }

    private static Map<String, Patient> patientConfigHashMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);


    public static Patient loadPatientByKey(PatientKey patientKey) {
        return loadPatientByKey(patientKey.getValue());
    }

    public static Patient loadPatientByKey(String configKey) {

        if (patientConfigHashMap.containsKey(configKey)) {
            return patientConfigHashMap.get(configKey);
        }

        scan();

        if (patientConfigHashMap.containsKey(configKey)) {
            return patientConfigHashMap.get(configKey);
        } else {
            throw new PatientDataNotFoundException("Patient configuration with name " + configKey + " was not found");
        }

    }

    private static void scan() {

        List<File> configurationFiles = new ArrayList<>();

        for (String scanPath : scanPaths) {
            File[] xmlFilesInScanPath = new File(scanPath).listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File folder, String name) {
                    return name.toLowerCase().endsWith(".xml");
                }
            });

            configurationFiles.addAll(Arrays.asList(xmlFilesInScanPath));
        }

        patientConfigHashMap.clear();

        for (File configurationFile : configurationFiles) {
            try {
                Patient patient = readConfig(configurationFile);
                patientConfigHashMap.put(StringUtils.lowerCase(patient.getKey()), patient);
            } catch (TransformationException e) {
                throw new PatientDataConfigurationInvalidException("Invalid patient configuration file content for file: " + configurationFile.getAbsolutePath(), e);
            }
        }


    }

    private static Patient readConfig(File configFile) throws TransformationException {
        String content = IOUtils.getResourceAsString(configFile.getAbsolutePath());
        Patient patient = JAXBUtils.unmarshal(Patient.class, content);
        String patientKey = FilenameUtils.getBaseName(configFile.getAbsolutePath());
        patient.setKey(patientKey);
        log.debug("Read config: " + patientKey);
        return patient;
    }

    private static void addScanPath() {

        File resourceDirectoryAsFile = ResourceResolver.getProjectDirectoryAsFile(PATH_TO_PATIENT_CONFIG_FOLDER);
        if (resourceDirectoryAsFile == null) {
            throw new RuntimeException("Failed to locate folder: " + PATH_TO_PATIENT_CONFIG_FOLDER);
        }
        log.info("Scan path exists (" + resourceDirectoryAsFile.getAbsolutePath() + ")? -> " + resourceDirectoryAsFile.exists());
        scanPaths.add(resourceDirectoryAsFile.getAbsolutePath());

    }

}