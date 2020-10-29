/*
 * Copyright (c) eHealth
 */
package be.ehealth.businessconnector.testcommons.testperson.util.search;

import java.util.Arrays;
import java.util.List;

import be.ehealth.businessconnector.testcommons.testperson.domain.TestPerson;
import be.ehealth.businessconnector.testcommons.testperson.util.TestPersonVisitor;


/**
 * Class to search on the inss field contains defined if we want some results with an inss equal or not
 * 
 * @author EHP
 */
public class InssVisitor implements TestPersonVisitor {

    private boolean contains = true;

    private final List<String> inssList;


    /**
     * @param inss
     */
    public InssVisitor(String... inss) {
        this(true, inss);
    }

    /**
     * contains defined if we want some results with an inss equal or not
     * 
     * @param contains
     * @param inss
     */
    public InssVisitor(boolean contains, String... inss) {
        super();
        if (inss == null || inss.length == 0) {
            throw new IllegalArgumentException("At least one inss number is required");

        }
        this.contains = contains;
        inssList = Arrays.asList(inss);
    }

    /**
     * @see be.ehealth.businessconnector.testcommons.testperson.util.TestPersonVisitor#visit(be.ehealth.businessconnector.testcommons.testperson.domain.TestPerson)
     */
    @Override
    public boolean visit(TestPerson person) {
        for (String inss : inssList) {
            if (person.getSsin().equals(inss)) {
                return contains;
            }
        }

        return !contains;
    }
}
