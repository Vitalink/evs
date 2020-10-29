/*
 * Copyright (c) eHealth
 */
package be.ehealth.businessconnector.testcommons.testperson;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ehealth.businessconnector.testcommons.testperson.domain.Environment;
import be.ehealth.businessconnector.testcommons.testperson.domain.Person;
import be.ehealth.businessconnector.testcommons.testperson.domain.TestPerson;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorExceptionValues;


/**
 * Factory to generate {@link TestPerson} from a properties given by the load method.
 * 
 * @author EHP
 */
public final class TestPersonFactory {

    private static final Logger LOG = LoggerFactory.getLogger(TestPersonFactory.class);


    private static String baseKey = "TestPerson";

    private static Properties props = new Properties();

    private static Map<String, Person> mapPerson = new TreeMap<String, Person>();

    private static Map<String, Environment> mapEnv = new TreeMap<String, Environment>();

    private static Map<String, TestPerson> mapProf = new TreeMap<String, TestPerson>();


    private TestPersonFactory() {
        super();
    }

    /**
     * Method who call load and generate objects
     * 
     * @param reader
     */
    public static void load(Reader reader) throws TechnicalConnectorException {
        loadFile(reader);
        generateObjects();
    }

    public static void reset() {
        props = new Properties();
        mapEnv = new TreeMap<String, Environment>();
        mapPerson = new TreeMap<String, Person>();
        mapProf = new TreeMap<String, TestPerson>();
    }

    /**
     * Store the testperson with the given {@link Writer}
     * 
     * @param writer
     * @throws IOException
     */
    public static void write(Writer writer) throws IOException {
        props.store(writer, "Generate");
    }

    /**
     * create the identifier to retrieve a person in the map
     * 
     * @param identifier
     */
    public static String createPersonKey(String identifier) {
        return baseKey + "_" + identifier;
    }

    /**
     * create the identifier to retrieve a env in the map
     * 
     * @param identifier
     * @param env
     */
    public static String createEnvironmentKey(String identifier, String env) {
        return createPersonKey(identifier) + "_" + env;
    }

    /**
     * create the testPerson to retrieve a person in the map
     * 
     * @param identifier
     * @param env
     * @param profession
     */
    public static String createTestPersonKey(String identifier, String env, String profession) {
        return createEnvironmentKey(identifier, env) + "_" + profession;
    }


    /**
     * Load the file given in the reader to set the properties (used to generate the objects)
     * 
     * @param reader
     */
    public static void loadFile(Reader reader) {
        try {
            props.load(reader);
        } catch (Exception e) {
            LOG.error(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Generate from the properties the objects in the different Map
     */
    public static void generateObjects() throws TechnicalConnectorException {
        Enumeration<Object> keys = props.keys();
        Set<String> personKeySet = new HashSet<String>();
        Set<String> envKeySet = new HashSet<String>();
        Set<String> testPersonKeySet = new HashSet<String>();

        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            key = new StringTokenizer(key).nextToken(".");
            if (key.startsWith(baseKey)) {
                switch (StringUtils.countMatches(key, "_")) {

                    case 1:
                        personKeySet.add(key);
                        break;

                    case 2:
                        envKeySet.add(key);
                        break;

                    case 3:
                        testPersonKeySet.add(key);
                        break;

                    default:
                        break;
                }
            }
        }

        for (String personKey : personKeySet) {
            StringTokenizer tokenizer = new StringTokenizer(personKey);
            tokenizer.nextToken("_");
            generatePerson(tokenizer.nextToken());
        }

        for (String envKey : envKeySet) {
            StringTokenizer tokenizer = new StringTokenizer(envKey);
            tokenizer.nextToken("_");
            String identifier = tokenizer.nextToken("_");
            generateEnvironment(identifier, tokenizer.nextToken());
        }

        for (String testpKey : testPersonKeySet) {
            StringTokenizer tokenizer = new StringTokenizer(testpKey);
            tokenizer.nextToken("_");
            String identifier = tokenizer.nextToken("_");
            String env = tokenizer.nextToken("_");
            generateTestPerson(identifier, env, tokenizer.nextToken());
        }
        return;
    }

    /**
     * Generate a single person
     * 
     * @param identifier
     */
    private static void generatePerson(String identifier) throws TechnicalConnectorException {
        String key = createPersonKey(identifier);

        if (!mapPerson.containsKey(key)) {
            Person toAdd = new Person();

            String ssin = props.getProperty(key + ".inss");
            if (ssin == null || ssin.equals("")) {
                throw new TechnicalConnectorException(TechnicalConnectorExceptionValues.INVALID_PROPERTY, "inss for identifier : " + identifier);
            }

            toAdd.setSsin(ssin);
            toAdd.setFirstName(props.getProperty(key + ".firstname"));
            toAdd.setLastName(props.getProperty(key + ".lastname"));
            toAdd.setMut(props.getProperty(key + ".mutuality"));
            toAdd.setMutualityNumber(props.getProperty(key + ".mutualitynumber"));
            toAdd.setCardnumber(props.getProperty(key + ".cardnumber"));
            mapPerson.put(key, toAdd);
        }
    }

    /**
     * Generate a single Environment
     * 
     * @param identifier
     * @param env
     */
    private static void generateEnvironment(String identifier, String env) throws TechnicalConnectorException {

        String envKey = createEnvironmentKey(identifier, env);
        if (!mapEnv.containsKey(envKey)) {

            String personKey = createPersonKey(identifier);
            Person toReference = mapPerson.get(personKey);
            if (toReference == null) {
                throw new TechnicalConnectorException(TechnicalConnectorExceptionValues.INVALID_PROPERTY, "inss for identifier : " + identifier);
            }

            Environment toAdd = new Environment(toReference);
            toAdd.setP12Location(props.getProperty(envKey + ".p12Location"));
            toAdd.setP12Password(props.getProperty(envKey + ".p12Password"));
            toAdd.setEnvironmentName(env);
            mapEnv.put(envKey, toAdd);
        }
    }

    /**
     * Generate a single TestPerson
     * 
     * @param identifier
     * @param env
     * @param profession
     */
    private static void generateTestPerson(String identifier, String env, String profession) throws TechnicalConnectorException {

        String testPKey = createTestPersonKey(identifier, env, profession);
        if (!mapProf.containsKey(testPKey)) {

            String envKey = createEnvironmentKey(identifier, env);
            Environment toReference = mapEnv.get(envKey);
            if (toReference == null) {
                generateEnvironment(identifier, env);
                toReference = mapEnv.get(envKey);
            }

            TestPerson toAdd = new TestPerson(toReference);
            toAdd.setProfessionNihii(testPKey + ".nihii");
            toAdd.setProfessionType(profession);
            mapProf.put(testPKey, toAdd);
        }
    }

    /**
     * Return all TestPerson contained in the given properties
     */
    public static List<Person> getAllPerson() {
        return Arrays.asList(mapPerson.values().toArray(new Person[mapPerson.size()]));
    }

    /**
     * Return all TestPerson contained in the given properties
     */
    public static List<Environment> getAllEnvironment() {
        return Arrays.asList(mapEnv.values().toArray(new Environment[mapEnv.size()]));
    }

    /**
     * Return all TestPerson contained in the given properties
     */
    public static List<TestPerson> getAllTestPerson() {
        return Arrays.asList(mapProf.values().toArray(new TestPerson[mapProf.size()]));
    }

    /**
     * Will create a new testperson
     * 
     * @param toCreate
     * @return
     */
    public static TestPerson createTestPerson(TestPerson toCreate) {
        throw new UnsupportedOperationException("Not yet implemented");

    }
}
