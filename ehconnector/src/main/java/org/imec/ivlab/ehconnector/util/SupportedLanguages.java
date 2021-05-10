/*
 * Copyright (c) eHealth
 */
package org.imec.ivlab.ehconnector.util;


/**
 * Enumeration that is contains all the supported programm languages.
 * 
 * @author EH053
 * 
 */
public enum SupportedLanguages {
    /** */
    JAVA(".java"), //
    /** */
    NET(".net");

    private String abbreviation;

    private SupportedLanguages(String abbreviation) {
        this.abbreviation = abbreviation;
    }


    /**
     * @return the abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }

}
