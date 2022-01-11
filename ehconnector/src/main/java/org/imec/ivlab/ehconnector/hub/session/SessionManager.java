package org.imec.ivlab.ehconnector.hub.session;

import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.config.Configuration;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.service.sts.security.SAMLToken;
import be.ehealth.technicalconnector.session.Session;
import be.ehealth.technicalconnector.session.SessionItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.authentication.AuthenticationConfigReader;
import org.imec.ivlab.core.authentication.model.AuthenticationConfig;
import org.imec.ivlab.core.authentication.model.Certificate;
import org.imec.ivlab.core.authentication.model.ConfigDetails;
import org.imec.ivlab.core.authentication.model.Ehealth;
import org.imec.ivlab.core.authentication.model.Type;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.IOUtils;
import org.imec.ivlab.core.util.OSUtils;
import org.imec.ivlab.core.util.PathUtils;
import org.imec.ivlab.ehconnector.hub.HubService;
import org.imec.ivlab.ehconnector.hub.exception.incurable.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static be.ehealth.businessconnector.hubv3.service.ServiceFactory.PROP_ENDPOINT_INTRAHUB;
import static be.ehealth.businessconnector.hubv3.service.ServiceFactory.PROP_HUBAPPID;
import static be.ehealth.businessconnector.hubv3.service.ServiceFactory.PROP_HUBID;
import static org.imec.ivlab.ehconnector.hub.session.CertificateManager.getCertificate;


public class SessionManager {

    private final static Logger log = LogManager.getLogger(SessionManager.class);

    private static HashMap<String, CacheItem> sessionCache = new HashMap<>();
    private static CacheItem loadedSession = null;

    protected static final String SESSION_CONFIG_LOCATION = "/be.ehealth.technicalconnector.properties";

    private static final int MAX_SESSION_CREATION_ATTEMPTS = 10;

    private static HashSet<String> PROPERTY_NAMES_NO_RELOAD = new HashSet<>();
    static {
        PROPERTY_NAMES_NO_RELOAD.add(PROP_HUBAPPID);
        PROPERTY_NAMES_NO_RELOAD.add(PROP_HUBID);
        PROPERTY_NAMES_NO_RELOAD.add(PROP_ENDPOINT_INTRAHUB);
    }

    private static Set<String> USED_PROPERTY_NAMES = new HashSet<>();

    public static void connectWith(AuthenticationConfig configuration) throws TechnicalConnectorException, InvalidConfigurationException {

        log.debug("Starting authentication for configuration: " + configuration.getName());

        // check if session in cache
        if (sessionCache.containsKey(configuration.getName())) {

            CacheItem sessionItem = sessionCache.get(configuration.getName());

            // load session from cache if needed
            if (sessionItem.getSamlToken() != loadedSession.getSamlToken()) {
                log.debug("Loading session " + configuration.getName() + " from local session cache");
                updateTechnicalConnectorConfig(configuration.getConfigDetails());
                Session.getInstance().unloadSession();
                Session.getInstance().loadSession(sessionItem.getSamlToken(), sessionItem.getHolderOfKeyPassword(), sessionItem.getEncryptionPassword());
                loadedSession = sessionItem;
            }

            // check if loaded session is still valid
            if (Session.getInstance().hasValidSession()) {
                log.debug("Session " + configuration.getName() + " already loaded and still valid");
                return;
            } else {
                log.debug("Session " + configuration.getName() + " already loaded but not valid anymore, will create a new one");
                Session.getInstance().unloadSession();
                updateTechnicalConnectorConfig(configuration.getConfigDetails());
                CacheItem session = createSession(configuration.getConfigDetails().getEvs().getCertificates());
                sessionCache.put(configuration.getName(), session);
                loadedSession = session;
            }

        } else {
            log.debug("Session " + configuration.getName() + " is not not in cache yet, will create the session and add it to cache");
            updateTechnicalConnectorConfig(configuration.getConfigDetails());
            CacheItem session = createSession(configuration.getConfigDetails().getEvs().getCertificates());
            sessionCache.put(configuration.getName(), session);
            loadedSession = session;
        }

        log.debug("Successfully authenticated with configuration: " + configuration.getName());

    }

    private static CacheItem createSession(List<Certificate> certificates) throws TechnicalConnectorException, InvalidConfigurationException {

        log.info("Session default keystore: " + ConfigFactory.getConfigValidator().getProperty("session.default.keystore"));

        // Password of the keystore defined in the properties file
        Certificate identityCertificate = getCertificate(certificates, Type.IDENTIFICATION);
        Certificate holderOfKeyCertificate = getCertificate(certificates, Type.HOLDEROFKEY);
        Certificate encryptionCertificate = getCertificate(certificates, Type.ENCRYPTION);

        Session.getInstance().unloadSession();

        int attempt = 0;
        while (true) {
            try {
                SessionItem session = Session.getInstance().createFallbackSession(identityCertificate.getPassword(), holderOfKeyCertificate.getPassword(), encryptionCertificate.getPassword());
                log.debug("Token expires at: " + session.getSAMLToken().getExpirationDateTime());

                return new CacheItem(holderOfKeyCertificate.getPassword(), encryptionCertificate.getPassword(), session.getSAMLToken());
            } catch (TechnicalConnectorException e) {
                if (++attempt >= MAX_SESSION_CREATION_ATTEMPTS) {
                    throw e;
                } else {
                    log.info("Session creation attempt #" + attempt + " failed with message: '" + e.getMessage() + "'. Retrying...");
                }
            }

        }


    }

    private static void addPropertiesToConfiguration(Configuration configuration, Properties properties) {

        if (properties == null) {
            return;
        }

        for (Map.Entry<Object, Object> property : properties.entrySet()) {
            log.trace("Adding property to configuration: " + property.getKey() + "=" + property.getValue());
            configuration.setProperty((String) property.getKey(), (String) property.getValue());
            USED_PROPERTY_NAMES.add((String) property.getKey());
        }

    }

    private static void loadPropertiesFromDisk(Configuration configuration) {
        Properties sessionProps = new Properties();
        try {
            sessionProps.load(IOUtils.getResourceAsStream(SESSION_CONFIG_LOCATION));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Object keyObj : sessionProps.keySet()) {
            String key = keyObj.toString();
            if (!key.startsWith("test.") && !PROPERTY_NAMES_NO_RELOAD.contains(key)) {
                String value = sessionProps.getProperty(key);
                configuration.setProperty(key, value);
            }
        }
    }


    private static Properties getUserdefinedEhealthProperties(Ehealth userEhealthConfig) throws InvalidConfigurationException {

        Properties properties = new Properties();

        if (userEhealthConfig == null || CollectionsUtil.emptyOrNull(userEhealthConfig.getEntries())) {
            return properties;
        }

        for (String propertyFullString : userEhealthConfig.getEntries()) {
            int posEqual = StringUtils.indexOf(propertyFullString, "=");
            if (posEqual < 1) {
                throw new InvalidConfigurationException("A Property must contain a key and value separated by an equal sign '=': " + propertyFullString);
            }
            String propertyKey = StringUtils.left(propertyFullString, posEqual);
            if (StringUtils.isBlank(propertyKey)) {
                throw new InvalidConfigurationException("Property key cannot be blank: " + propertyFullString);
            }
            String propertyValue = StringUtils.mid(propertyFullString, posEqual + 1, StringUtils.length(propertyFullString) - StringUtils.length(propertyKey) - 1);
            properties.put(propertyKey, propertyValue);
        }

        return properties;

    }


    private static boolean relativePathsDefined(List<Certificate> certificates) throws InvalidConfigurationException {

        if (CollectionsUtil.emptyOrNull(certificates)) {
            return false;
        }

        boolean firstPathIsAbsolute = (new File(certificates.get(0).getPath()).isAbsolute());
        log.trace("First path (" + certificates.get(0).getPath() + ") is absolute? " + firstPathIsAbsolute);
        for (int i = 1; i < certificates.size(); i++ ) {
            File certificateFile = new File(certificates.get(i).getPath());
            if (!certificateFile.exists() || !certificateFile.isFile()) {
                String resolvedPath = "";
                try {
                    resolvedPath = certificateFile.getCanonicalPath();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                throw new InvalidConfigurationException("Certificate configuration file does not exist: " + certificateFile.getPath() + " . Full path: " + resolvedPath);
            }

            if (new File(certificates.get(i).getPath()).isAbsolute() != firstPathIsAbsolute) {
                throw new InvalidConfigurationException("A mix of relative and absolute paths to certificates were used in the configuration file. Please use either absolute or relative certificate paths within one actor configuration file");
            }
        }

        return !firstPathIsAbsolute;

    }

    private static Properties generateCertificateProperties(List<Certificate> certificates) throws InvalidConfigurationException {

        Properties properties = new Properties();

        Set<Certificate> certificatesFiltered = new HashSet<>();
        Certificate identificationCertificate = CertificateManager.getCertificate(certificates, Type.IDENTIFICATION);
        certificatesFiltered.add(identificationCertificate);
        certificatesFiltered.add(CertificateManager.getCertificate(certificates, Type.HOLDEROFKEY));
        certificatesFiltered.add(CertificateManager.getCertificate(certificates, Type.ENCRYPTION));

        List<String> certificatePaths = new ArrayList<>();
        for (Certificate certificate : certificatesFiltered) {
            certificatePaths.add(certificate.getPath());
        }


        boolean relativePathsDefined = relativePathsDefined(certificates);

        String keystoreDir = "";
        if (relativePathsDefined) {
            keystoreDir = System.getProperty("user.dir");
        } else {
            keystoreDir = getCertificatesCommonPath(certificatePaths);
        }
        properties.put("KEYSTORE_DIR", keystoreDir + File.separator);


        Iterator<Certificate> iterator = certificatesFiltered.iterator();
        while (iterator.hasNext()) {
            Certificate certificate = iterator.next();
            String osAdaptedFilePath = OSUtils.adaptFilePath(PathUtils.relativizePath(keystoreDir, certificate.getPath()));
            properties.put("sessionmanager." + certificate.getType().getValue() + ".keystore", osAdaptedFilePath);
        }

        String osAdaptedFilePathIdentificationCertificate = OSUtils.adaptFilePath(PathUtils.relativizePath(keystoreDir, identificationCertificate.getPath()));
        properties.put("session.default.keystore", osAdaptedFilePathIdentificationCertificate);

        return properties;

    }

    private static String getCertificatesCommonPath(List<String> paths) throws InvalidConfigurationException {
        String[] pathArray = paths.toArray(new String[0]);
        String commonPath = PathUtils.commonParent(pathArray);
        if (commonPath == null) {
            throw new InvalidConfigurationException("All certificates must be located under the same root. The configuration contains certificate configurations with different roots: " + StringUtils.joinWith(", ", pathArray));
        }

        return commonPath;
    }

    private static void updateTechnicalConnectorConfig(ConfigDetails configDetails) throws TechnicalConnectorException, InvalidConfigurationException {

        Configuration eHealthConfig = ConfigFactory.getConfigValidator().getConfig();
        eHealthConfig.reload();

        removePreviouslySetProperties(eHealthConfig);
        loadPropertiesFromDisk(eHealthConfig);

        Properties userdefinedEhealthProperties = getUserdefinedEhealthProperties(configDetails.getEhealth());
        addPropertiesToConfiguration(eHealthConfig, userdefinedEhealthProperties);

        eHealthConfig.reload();

        // Add certificate properties after having reloaded config
        // Otherwise: adding certificate properties will change the KEYSTORE_DIR property what also the ehealth connector is using.
        // by reloading the config, we would force ehealth to look again for the caCertificateKeystore.jks which cannot be found anymore once we change the KEYSTORE_DIR property
        Properties certificateProperties = generateCertificateProperties(configDetails.getEvs().getCertificates());
        addPropertiesToConfiguration(eHealthConfig, certificateProperties);

    }

    private static void removePreviouslySetProperties(Configuration configuration) {
        if (USED_PROPERTY_NAMES != null) {
            Iterator<String> iterator = USED_PROPERTY_NAMES.iterator();
            while (iterator.hasNext()) {
                configuration.setProperty(iterator.next(), null);
            }
        }
    }

    private static class CacheItem {

        private String holderOfKeyPassword;
        private String encryptionPassword;
        private SAMLToken samlToken;

        public CacheItem(String holderOfKeyPassword, String encryptionPassword, SAMLToken samlToken) {
            this.holderOfKeyPassword = holderOfKeyPassword;
            this.encryptionPassword = encryptionPassword;
            this.samlToken = samlToken;
        }

        public String getHolderOfKeyPassword() {
            return holderOfKeyPassword;
        }

        public void setHolderOfKeyPassword(String holderOfKeyPassword) {
            this.holderOfKeyPassword = holderOfKeyPassword;
        }

        public SAMLToken getSamlToken() {
            return samlToken;
        }

        public void setSamlToken(SAMLToken samlToken) {
            this.samlToken = samlToken;
        }

        public String getEncryptionPassword() {
            return encryptionPassword;
        }

        public void setEncryptionPassword(String encryptionPassword) {
            this.encryptionPassword = encryptionPassword;
        }
    }

}
