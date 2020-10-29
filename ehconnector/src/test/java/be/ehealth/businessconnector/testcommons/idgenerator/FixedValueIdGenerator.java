/*
 * Copyright (c) Smals
 */
package be.ehealth.businessconnector.testcommons.idgenerator;

import be.ehealth.technicalconnector.idgenerator.IdGenerator;


/**
 * Dummy idGenerator which always returns the same id 123456789.
 * 
 * @author EHP
 */
public class FixedValueIdGenerator implements IdGenerator {

    /**
     * @see be.ehealth.technicalconnector.idgenerator.IdGenerator#generateId()
     */
    @Override
    public String generateId() {
        return "123456789";
    }

}
