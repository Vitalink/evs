package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.CompoundprescriptionType;
import org.apache.commons.collections.CollectionUtils;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

public class CompoundPrescriptionUtil {


    /**
     * Get CompoundPrescription content as Magistral text type. New style, kmehr 1.19
     * @param compoundprescriptionType
     * @return
     */
    public List<TextType> getMagistralText(CompoundprescriptionType compoundprescriptionType) {

        return getCompoundFieldsAsJaxBElements(compoundprescriptionType, TextType.class);

    }

    /**
     * Get CompoundPrescription content as String. Old style, kmehr 1.3 and kmehr 1.5
     * @param compoundprescriptionType
     * @return
     */
    public List<String> getCompoundText(CompoundprescriptionType compoundprescriptionType) {

        return getCompoundFields(compoundprescriptionType, String.class);

    }

    private static <T> List<T> getCompoundFieldsAsJaxBElements(CompoundprescriptionType compoundprescriptionType, Class<T> objectType) {

        List<T> compoundFields = new ArrayList<>();

        if (compoundprescriptionType == null || CollectionUtils.isEmpty(compoundprescriptionType.getContent())) {
            return null;
        }

        for (Object object : compoundprescriptionType.getContent()) {

            if (! (object instanceof JAXBElement)) {
                continue;
            }
            JAXBElement jaxbElement = (JAXBElement) object;

            if (objectType.isInstance(jaxbElement.getValue()) ) {
                compoundFields.add((T) jaxbElement.getValue());
            }

        }

        return compoundFields;

    }

    private <T> List<T> getCompoundFields(CompoundprescriptionType compoundprescriptionType, Class<T> objectType) {

        List<T> transactionFields = new ArrayList<>();

        if (compoundprescriptionType == null || CollectionUtils.isEmpty(compoundprescriptionType.getContent())) {
            return null;
        }

        for (Object object : compoundprescriptionType.getContent()) {

            if (objectType.isInstance(object) ) {
                transactionFields.add((T) object);
            }

        }

        return transactionFields;

    }

}
