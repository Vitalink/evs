/*
 * Copyright (c) eHealth
 */
package be.ehealth.businessconnector.testcommons.tests;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import be.ehealth.businessconnector.testcommons.testperson.TestPersonFactory;
import be.ehealth.businessconnector.testcommons.testperson.domain.Environment;
import be.ehealth.businessconnector.testcommons.testperson.domain.Person;
import be.ehealth.businessconnector.testcommons.testperson.domain.TestPerson;
import be.ehealth.businessconnector.testcommons.testperson.util.TestPersonSelecter;
import be.ehealth.businessconnector.testcommons.testperson.util.TestPersonVisitor;
import be.ehealth.businessconnector.testcommons.testperson.util.search.EnvironmentVisitor;
import be.ehealth.businessconnector.testcommons.testperson.util.search.InssVisitor;
import be.ehealth.businessconnector.testcommons.testperson.util.search.ProfessionVisitor;
import be.ehealth.businessconnector.testcommons.testperson.util.search.SecurityConfigVisitor;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.utils.ConnectorIOUtils;


/**
 * Test for TestPersonFactory
 * 
 * @author EHP
 */
public class TestPersonFactoryTest {

    @After
    public void reset() {
        TestPersonFactory.reset();
    }

    /*
     * TEST FOR TESTPERSONFACTORY HIMSELF
     */
    @Test
    public void fileLoadingTest() throws TechnicalConnectorException {
        InputStreamReader inputStreamReader = new InputStreamReader(ConnectorIOUtils.getResourceAsStream("/examples/CorrectOrder.properties"));
        TestPersonFactory.load(inputStreamReader);
    }

    @Test
    public void generateObjectsCorrectOrderTest() throws TechnicalConnectorException {
        InputStreamReader inputStreamReader = new InputStreamReader(ConnectorIOUtils.getResourceAsStream("/examples/CorrectOrder.properties"));
        TestPersonFactory.load(inputStreamReader);
        TestPersonFactory.generateObjects();

        List<Person> allPerson = TestPersonFactory.getAllPerson();
        List<Environment> allEnv = TestPersonFactory.getAllEnvironment();
        List<TestPerson> alltpers = TestPersonFactory.getAllTestPerson();

        Assert.assertEquals(1, allPerson.size());
        Assert.assertEquals(2, allEnv.size());
        Assert.assertEquals(10, alltpers.size());
    }

    @Test
    public void generateObjectsWrongOrderTest() throws TechnicalConnectorException {
        InputStreamReader inputStreamReader = new InputStreamReader(ConnectorIOUtils.getResourceAsStream("/examples/WrongOrder.properties"));
        TestPersonFactory.load(inputStreamReader);
        TestPersonFactory.generateObjects();

        List<Person> allPerson = TestPersonFactory.getAllPerson();
        List<Environment> allEnv = TestPersonFactory.getAllEnvironment();
        List<TestPerson> alltpers = TestPersonFactory.getAllTestPerson();

        Assert.assertEquals(1, allPerson.size());
        Assert.assertEquals(2, allEnv.size());
        Assert.assertEquals(10, alltpers.size());
    }

    @Test(expected = TechnicalConnectorException.class)
    public void generateObjectsMissingInssTest() throws TechnicalConnectorException {
        InputStreamReader inputStreamReader = new InputStreamReader(ConnectorIOUtils.getResourceAsStream("/examples/MissingInss.properties"));
        TestPersonFactory.load(inputStreamReader);
        TestPersonFactory.generateObjects();

        List<Person> allPerson = TestPersonFactory.getAllPerson();
        List<Environment> allEnv = TestPersonFactory.getAllEnvironment();
        List<TestPerson> alltpers = TestPersonFactory.getAllTestPerson();

        Assert.assertEquals(0, allPerson.size());
        Assert.assertEquals(0, allEnv.size());
        Assert.assertEquals(0, alltpers.size());
    }

    @Test
    public void generateObjectsMissingEnvTest() throws TechnicalConnectorException {
        InputStreamReader inputStreamReader = new InputStreamReader(ConnectorIOUtils.getResourceAsStream("/examples/MissingEnv.properties"));
        TestPersonFactory.load(inputStreamReader);
        TestPersonFactory.generateObjects();

        List<Person> allPerson = TestPersonFactory.getAllPerson();
        List<Environment> allEnv = TestPersonFactory.getAllEnvironment();
        List<TestPerson> alltpers = TestPersonFactory.getAllTestPerson();

        Assert.assertEquals(1, allPerson.size());
        Assert.assertEquals(1, allEnv.size());
        Assert.assertEquals(5, alltpers.size());
    }

    /*
     * TEST OF SELECTOR
     */
    @Test
    public void searchByInssTest() throws TechnicalConnectorException {
        InputStreamReader inputStreamReader = new InputStreamReader(ConnectorIOUtils.getResourceAsStream("/examples/FileForSearch.properties"));

        List<TestPersonVisitor> searchCriteriaList = new ArrayList<TestPersonVisitor>();
        searchCriteriaList.add(new InssVisitor("77013014990", "91121208397"));

        List<TestPerson> select = TestPersonSelecter.select(inputStreamReader, searchCriteriaList);

        Assert.assertEquals(2, select.size());
    }

    @Test
    public void searchNotContainsInssTest() throws TechnicalConnectorException {
        InputStreamReader inputStreamReader = new InputStreamReader(ConnectorIOUtils.getResourceAsStream("/examples/FileForSearch.properties"));

        List<TestPersonVisitor> searchCriteriaList = new ArrayList<TestPersonVisitor>();
        searchCriteriaList.add(new InssVisitor(false, "77013014990", "91121208397"));

        List<TestPerson> select = TestPersonSelecter.select(inputStreamReader, searchCriteriaList);

        Assert.assertEquals(10, select.size());
    }

    @Test
    public void searchByProfessionTest() throws TechnicalConnectorException {
        InputStreamReader inputStreamReader = new InputStreamReader(ConnectorIOUtils.getResourceAsStream("/examples/FileForSearch.properties"));

        List<TestPersonVisitor> searchCriteriaList = new ArrayList<TestPersonVisitor>();
        searchCriteriaList.add(new ProfessionVisitor("physician", "nurse"));

        List<TestPerson> select = TestPersonSelecter.select(inputStreamReader, searchCriteriaList);

        Assert.assertEquals(5, select.size());
    }

    @Test
    public void searchNotContainsProfessionTest() throws TechnicalConnectorException {
        InputStreamReader inputStreamReader = new InputStreamReader(ConnectorIOUtils.getResourceAsStream("/examples/FileForSearch.properties"));

        List<TestPersonVisitor> searchCriteriaList = new ArrayList<TestPersonVisitor>();
        searchCriteriaList.add(new ProfessionVisitor(false, "physician", "nurse"));

        List<TestPerson> select = TestPersonSelecter.select(inputStreamReader, searchCriteriaList);

        Assert.assertEquals(7, select.size());
    }

    @Test
    public void searchByEnvironmentTest() throws TechnicalConnectorException {
        InputStreamReader inputStreamReader = new InputStreamReader(ConnectorIOUtils.getResourceAsStream("/examples/FileForSearch.properties"));

        List<TestPersonVisitor> searchCriteriaList = new ArrayList<TestPersonVisitor>();
        searchCriteriaList.add(new EnvironmentVisitor("int"));

        List<TestPerson> select = TestPersonSelecter.select(inputStreamReader, searchCriteriaList);

        Assert.assertEquals(5, select.size());
    }

    @Test
    public void searchNotContainsEnvironmentTest() throws TechnicalConnectorException {
        InputStreamReader inputStreamReader = new InputStreamReader(ConnectorIOUtils.getResourceAsStream("/examples/FileForSearch.properties"));

        List<TestPersonVisitor> searchCriteriaList = new ArrayList<TestPersonVisitor>();
        searchCriteriaList.add(new EnvironmentVisitor(false, "int"));

        List<TestPerson> select = TestPersonSelecter.select(inputStreamReader, searchCriteriaList);

        Assert.assertEquals(7, select.size());
    }

    @Test
    public void searchBySecurityConfigTest() throws TechnicalConnectorException {
        InputStreamReader inputStreamReader = new InputStreamReader(ConnectorIOUtils.getResourceAsStream("/examples/FileForSearch.properties"));

        List<TestPersonVisitor> searchCriteriaList = new ArrayList<TestPersonVisitor>();
        searchCriteriaList.add(new SecurityConfigVisitor());

        List<TestPerson> select = TestPersonSelecter.select(inputStreamReader, searchCriteriaList);

        Assert.assertEquals(5, select.size());
    }

    @Test
    public void searchCombiTest() throws TechnicalConnectorException {
        InputStreamReader inputStreamReader = new InputStreamReader(ConnectorIOUtils.getResourceAsStream("/examples/FileForSearch.properties"));

        List<TestPersonVisitor> searchCriteriaList = new ArrayList<TestPersonVisitor>();
        searchCriteriaList.add(new ProfessionVisitor(false, "physician", "nurse"));
        searchCriteriaList.add(new EnvironmentVisitor("acc"));
        searchCriteriaList.add(new InssVisitor("82051234978"));
        searchCriteriaList.add(new SecurityConfigVisitor());

        List<TestPerson> select = TestPersonSelecter.select(inputStreamReader, searchCriteriaList);

        Assert.assertEquals(3, select.size());
    }

    @Test
    public void searchNoVisitorTest() throws TechnicalConnectorException {
        InputStreamReader inputStreamReader = new InputStreamReader(ConnectorIOUtils.getResourceAsStream("/examples/FileForSearch.properties"));

        List<TestPersonVisitor> searchCriteriaList = new ArrayList<TestPersonVisitor>();
        List<TestPerson> select = TestPersonSelecter.select(inputStreamReader, searchCriteriaList);

        Assert.assertEquals(12, select.size());
    }

}
