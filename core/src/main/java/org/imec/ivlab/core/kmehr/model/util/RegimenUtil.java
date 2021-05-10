package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.schema.v1.AdministrationquantityType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.WeekdayType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.imec.ivlab.core.kmehr.model.RegimenEntry;
import org.imec.ivlab.core.util.ArrayUtil;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegimenUtil {

    public static List<BigInteger> getDayNumbers(ItemType.Regimen regimen) {

        return getRegimenFields(regimen, BigInteger.class);

    }

    public static List<Calendar> getDates(ItemType.Regimen regimen) {

        return getRegimenFields(regimen, Calendar.class);

    }

    public static List<WeekdayType> getWeekdays(ItemType.Regimen regimen) {

        return getRegimenFields(regimen, WeekdayType.class);

    }

    public static List<ItemType.Regimen.Daytime> getDaytimes(ItemType.Regimen regimen) {

        return getRegimenFields(regimen, ItemType.Regimen.Daytime.class);

    }

    public static List<AdministrationquantityType> getQuantities(ItemType.Regimen regimen) {

        return getRegimenFields(regimen, AdministrationquantityType.class);

    }

    private static <T> List<T> getRegimenFields(ItemType.Regimen regimen, Class<T> objectType) {

        List<T> regimenFields = new ArrayList<>();

        if (regimen == null || CollectionUtils.isEmpty(regimen.getDaynumbersAndQuantitiesAndDates())) {
            return null;
        }

        for (Object object : regimen.getDaynumbersAndQuantitiesAndDates()) {

            JAXBElement jaxbElement = (JAXBElement) object;

            if (objectType.isInstance(jaxbElement.getValue()) ) {
                regimenFields.add((T) jaxbElement.getValue());
            }

        }

        return regimenFields;

    }

    public static List<RegimenEntry> getRegimenEntries(ItemType.Regimen regimen) {

        if (regimen == null || CollectionUtils.isEmpty(regimen.getDaynumbersAndQuantitiesAndDates())) {
            return null;
        }

        List<RegimenEntry> regimenEntries = new ArrayList<>();


        int regimenEntryIndex = 0;

        Object[] regimenFields = regimen.getDaynumbersAndQuantitiesAndDates().toArray();

        while (regimenEntryIndex >= 0 && regimenEntryIndex <= regimenFields.length - 1) {

            int previousRegimenEntryIndex = regimenEntryIndex;

            regimenEntryIndex = ArrayUtil.indexOfJAXBElement(regimen.getDaynumbersAndQuantitiesAndDates().toArray(), AdministrationquantityType.class, regimenEntryIndex + 1);

            if (regimenEntryIndex >= 0) {

                Object[] regimenFieldsForEntry = ArrayUtils.subarray(regimenFields, previousRegimenEntryIndex, regimenEntryIndex + 1);
                RegimenEntry regimenEntry = createRegimenEntry(regimenFieldsForEntry);
                regimenEntries.add(regimenEntry);

            }

        }

        return regimenEntries;

    }
    private static RegimenEntry createRegimenEntry(Object[] regimenEntryFields) {

        RegimenEntry regimenEntry = new RegimenEntry();

        for (Object object : regimenEntryFields) {

            JAXBElement jaxbElement = (JAXBElement) object;

            if (BigInteger.class.isInstance(jaxbElement.getValue()) ) {
                regimenEntry.setDayNumber((BigInteger) jaxbElement.getValue());
                continue;
            }

            if (Calendar.class.isInstance(jaxbElement.getValue()) ) {
                Calendar calendarDate = ((Calendar) jaxbElement.getValue());
                regimenEntry.setDate(calendarDate.getTime());
                continue;
            }

            if (WeekdayType.class.isInstance(jaxbElement.getValue()) ) {
                regimenEntry.setWeekday((WeekdayType) jaxbElement.getValue());
                continue;
            }


            if (ItemType.Regimen.Daytime.class.isInstance(jaxbElement.getValue()) ) {
                regimenEntry.setDaytime((ItemType.Regimen.Daytime) jaxbElement.getValue());
                continue;
            }

            if (AdministrationquantityType.class.isInstance(jaxbElement.getValue()) ) {
                regimenEntry.setQuantity((AdministrationquantityType) jaxbElement.getValue());
                continue;
            }

            throw new RuntimeException("Regimen entry field of type " + jaxbElement.getValue().getClass() + " cannot be used to create a regimen entry");

        }

        return regimenEntry;

    }

}
