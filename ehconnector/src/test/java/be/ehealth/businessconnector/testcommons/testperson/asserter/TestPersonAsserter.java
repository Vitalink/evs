/*
 * Copyright (c) eHealth
 */
package be.ehealth.businessconnector.testcommons.testperson.asserter;

import be.ehealth.businessconnector.testcommons.testperson.domain.TestPerson;


/**
 * Interface who propose a method to validate a test person
 * 
 * @author EHP
 */
public interface TestPersonAsserter {

    void validate(TestPerson person);

}
