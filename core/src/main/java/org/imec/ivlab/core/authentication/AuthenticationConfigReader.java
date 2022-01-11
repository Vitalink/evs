package org.imec.ivlab.core.authentication;

import static org.imec.ivlab.core.constants.CoreConstants.PATH_TO_ACTOR_CONFIG_FOLDER;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.authentication.exceptions.AuthenticationConfigurationErrorException;
import org.imec.ivlab.core.authentication.exceptions.AuthenticationConfigurationNotFoundException;
import org.imec.ivlab.core.authentication.model.AuthenticationConfig;
import org.imec.ivlab.core.authentication.model.ConfigDetails;
import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.util.IOUtils;
import org.imec.ivlab.core.util.JAXBUtils;
import org.imec.ivlab.core.util.ResourceResolver;

public class AuthenticationConfigReader {

    private final static Logger log = LogManager.getLogger(AuthenticationConfigReader.class);

    public static final String GP_EXAMPLE = "gp_example";

    private static final String CONFIG_EXTENSION = ".xml";

    public static final String DEFAULT_CONFIGURATION = AuthenticationConfigReader.GP_EXAMPLE;

    private static Set<String> scanPaths = new HashSet<>();

    private static Map<String, AuthenticationConfig> authenticationConfigHashMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    static {
        addScanPath();
    }

    public static AuthenticationConfig loadByName(String configName) {

        if (authenticationConfigHashMap.containsKey(configName)) {
            return authenticationConfigHashMap.get(configName);
        }

        scan();

        if (authenticationConfigHashMap.containsKey(configName)) {
            return authenticationConfigHashMap.get(configName);
        } else {
            throw new AuthenticationConfigurationNotFoundException("Configuration with name " + configName + " was not found. Please make sure an actor configuration exists with name: " + configName + CONFIG_EXTENSION);
        }

    }

    private static void scan() {

        List<File> configurationFiles = new ArrayList<>();

        for (String scanPath : scanPaths) {
            File[] xmlFilesInScanPath = new File(scanPath).listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File folder, String name) {
                    return name.toLowerCase().endsWith(CONFIG_EXTENSION);
                }
            });

            configurationFiles.addAll(Arrays.asList(xmlFilesInScanPath));
        }

        authenticationConfigHashMap.clear();

        for (File configurationFile : configurationFiles) {
            try {
                AuthenticationConfig config = readConfig(configurationFile);
                authenticationConfigHashMap.put(StringUtils.lowerCase(config.getName()), config);
            } catch (TransformationException e) {
                log.error("Failed to interpret configuration file: " + configurationFile.getAbsolutePath(), e);
            }
        }


    }

    private static AuthenticationConfig readConfig(File configFile) throws TransformationException {
        String content = IOUtils.getResourceAsString(configFile.getAbsolutePath());
        ConfigDetails configDetails;
        try {
            configDetails = JAXBUtils.unmarshal(ConfigDetails.class, content);
        } catch (TransformationException e) {
            throw new AuthenticationConfigurationErrorException("Invalid actor configuration file content for file: " + configFile.getAbsolutePath(), e);
        }
        String configurationName = FilenameUtils.getBaseName(configFile.getAbsolutePath());
        AuthenticationConfig configuration = new AuthenticationConfig(configurationName, configDetails);
        log.debug("Read config: " + configuration.getName());
        return configuration;
    }

    private static void addScanPath() {

        File resourceDirectoryAsFile = ResourceResolver.getProjectDirectoryAsFile(PATH_TO_ACTOR_CONFIG_FOLDER);
        if (resourceDirectoryAsFile == null) {
            throw new RuntimeException("Failed to locate folder: " + PATH_TO_ACTOR_CONFIG_FOLDER);
        }
        scanPaths.add(resourceDirectoryAsFile.getAbsolutePath());
    }

    public static void main(String[] args) throws TechnicalConnectorException {
        AuthenticationConfig authenticationConfig = AuthenticationConfigReader.loadByName(AuthenticationConfigReader.GP_EXAMPLE);
        log.info(authenticationConfig);
    }

}
