/*
 * Copyright (c) eHealth
 */
package be.ehealth.businessconnector.testcommons.testperson.util.search;

import be.ehealth.businessconnector.testcommons.testperson.domain.TestPerson;
import be.ehealth.businessconnector.testcommons.testperson.util.TestPersonVisitor;


/**
 * Search all the TestPerson who has some informations for the p12
 * 
 * @author EHP
 */
public class SecurityConfigVisitor implements TestPersonVisitor {

    /**
     * @see be.ehealth.businessconnector.testcommons.testperson.util.TestPersonVisitor#visit(be.ehealth.businessconnector.testcommons.testperson.domain.TestPerson)
     */
    @Override
    public boolean visit(TestPerson person) {
        if (person.getP12Location() == null || person.getP12Password() == null) {
            return false;
        }
        return true;
    }

}
