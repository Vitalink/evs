/*
 * Copyright (c) eHealth
 */
package org.imec.ivlab.ehconnector.business.medicationscheme.hubhelper;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYvalues;

/**
 * @author EHP
 *
 */
public class HubConfigCommon {


    /*
     * Professional Information. The nihii is found in the session
     */
    public static final String PROF_NISS = "90010100101";
    public static final String PROF_FIRSTNAME = "Jane";
    public static final String PROF_LASTNAME = "Doe";
    public static final String PROF_PROFESSION = CDHCPARTYvalues.PERSPHYSICIAN.value();

    public static final String HUB_NAME = "TEST HUB ORG";

    public static final String MESSAGE_ID = "1";
}
