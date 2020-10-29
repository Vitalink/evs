/*
 * Copyright (c) eHealth
 */
package be.ehealth.businessconnector.testcommons.testperson.util.search;

import java.util.Arrays;
import java.util.List;

import be.ehealth.businessconnector.testcommons.testperson.domain.TestPerson;
import be.ehealth.businessconnector.testcommons.testperson.util.TestPersonVisitor;


/**
 * Class to search on the environment field contains defined if we want some results with an environment equal or not
 * 
 * @author EHP
 */
public class EnvironmentVisitor implements TestPersonVisitor {

    private boolean contains = true;

    private final List<String> environmentList;


    /**
     * @param inss
     */
    public EnvironmentVisitor(String... envs) {
        this(true, envs);
    }

    /**
     * contains defined if we want some results with an inss equal or not
     * 
     * @param contains
     * @param inss
     */
    public EnvironmentVisitor(boolean contains, String... envs) {
        super();
        if (envs == null || envs.length == 0) {
            throw new IllegalArgumentException("At least one environment is required");

        }
        this.contains = contains;
        environmentList = Arrays.asList(envs);
    }

    /**
     * @see be.ehealth.businessconnector.testcommons.testperson.util.TestPersonVisitor#visit(be.ehealth.businessconnector.testcommons.testperson.domain.TestPerson)
     */
    @Override
    public boolean visit(TestPerson person) {
        for (String env : environmentList) {
            if (person.getEnvironmentName().equals(env)) {
                return contains;
            }
        }

        return !contains;
    }
}
