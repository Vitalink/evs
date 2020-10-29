/*
 * Copyright (c) eHealth
 */
package be.ehealth.businessconnector.testcommons.testperson.util.search;

import java.util.Arrays;
import java.util.List;

import be.ehealth.businessconnector.testcommons.testperson.domain.TestPerson;
import be.ehealth.businessconnector.testcommons.testperson.util.TestPersonVisitor;


/**
 * Class to search on the profession field contains defined if we want some results with an profession equal or not
 * 
 * @author EHP
 */
public class ProfessionVisitor implements TestPersonVisitor {

    private boolean contains = true;

    private final List<String> professionList;


    /**
     * @param inss
     */
    public ProfessionVisitor(String... professions) {
        this(true, professions);
    }

    /**
     * contains defined if we want some results with an inss equal or not
     * 
     * @param contains
     * @param inss
     */
    public ProfessionVisitor(boolean contains, String... professions) {
        super();
        if (professions == null || professions.length == 0) {
            throw new IllegalArgumentException("At least one profession type is required");

        }
        this.contains = contains;
        professionList = Arrays.asList(professions);
    }

    /**
     * @see be.ehealth.businessconnector.testcommons.testperson.util.TestPersonVisitor#visit(be.ehealth.businessconnector.testcommons.testperson.domain.TestPerson)
     */
    @Override
    public boolean visit(TestPerson person) {
        for (String profession : professionList) {
            if (person.getProfessionType().equals(profession)) {
                return contains;
            }
        }

        return !contains;
    }

}
