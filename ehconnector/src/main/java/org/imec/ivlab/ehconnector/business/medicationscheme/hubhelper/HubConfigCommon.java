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
    public static final String PROF_NISS = "82051234978";
    public static final String PROF_FIRSTNAME = "Hannes";
    public static final String PROF_LASTNAME = "De Clercq";
    public static final String PROF_PROFESSION = CDHCPARTYvalues.PERSPHYSICIAN.value();

    public static final String HUB_NAME = "TEST HUB ORG";

    public static final String MESSAGE_ID = "1";
}
