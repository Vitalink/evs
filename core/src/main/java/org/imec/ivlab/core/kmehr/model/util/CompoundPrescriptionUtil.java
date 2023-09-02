package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.CompoundprescriptionType;

import java.util.List;

public class CompoundPrescriptionUtil {


    /**
     * Get CompoundPrescription content as Magistral text type. New style, kmehr 1.19
     * @param compoundprescriptionType
     * @return
     */
    public List<TextType> getMagistralText(CompoundprescriptionType compoundprescriptionType) {
        return compoundprescriptionType.getMagistraltext();
    }

    /**
     * Get CompoundPrescription content as String. Old style, kmehr 1.3 and kmehr 1.5
     * @param compoundprescriptionType
     * @return
     */
    public List<String> getCompoundText(CompoundprescriptionType compoundprescriptionType) {
        return compoundprescriptionType.getMixedContent();
    }

}
