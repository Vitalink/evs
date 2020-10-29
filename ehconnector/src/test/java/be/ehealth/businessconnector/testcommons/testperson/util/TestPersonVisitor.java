/*
 * Copyright (c) eHealth
 */
package be.ehealth.businessconnector.testcommons.testperson.util;

import be.ehealth.businessconnector.testcommons.testperson.domain.TestPerson;


/**
 * Common interface for all the search criteria of TestPerson.
 * 
 * @author EHP
 */
public interface TestPersonVisitor {

    boolean visit(TestPerson person);
}
