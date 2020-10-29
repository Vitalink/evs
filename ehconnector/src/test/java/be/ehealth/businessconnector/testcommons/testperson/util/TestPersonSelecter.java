/*
 * Copyright (c) eHealth
 */
package be.ehealth.businessconnector.testcommons.testperson.util;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import be.ehealth.businessconnector.testcommons.testperson.TestPersonFactory;
import be.ehealth.businessconnector.testcommons.testperson.domain.TestPerson;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;


/**
 * Used to make a search in all the testperson from TestPersonFactory.getall() See
 * {@link be.ehealth.businessconnector.testcommons.testperson.TestPersonFactory}
 * 
 * @author EHP
 */
public final class TestPersonSelecter {

    private TestPersonSelecter() {
    }

    /**
     * Will go through all the TestPersonVisitor if there is a result false the TestPerson will not be added
     * 
     * @param reader
     * @param searchCriteriaList
     * @return
     */
    public static List<TestPerson> select(Reader reader, List<TestPersonVisitor> searchCriteriaList) throws TechnicalConnectorException {
        TestPersonFactory.load(reader);
        List<TestPerson> all = TestPersonFactory.getAllTestPerson();
        List<TestPerson> result = new ArrayList<TestPerson>();

        for (TestPerson testp : all) {
            boolean toBeAdded = true;
            for (TestPersonVisitor searchCriteria : searchCriteriaList) {
                if (toBeAdded && !searchCriteria.visit(testp)) {
                    toBeAdded = false;
                }
            }
            if (toBeAdded) {
                result.add(testp);
            }
        }
        return result;
    }
}
