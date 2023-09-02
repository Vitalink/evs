package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.schema.v1.AdministrationquantityType;
import be.fgov.ehealth.standards.kmehr.schema.v1.WeekdayType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Regimen;
import be.fgov.ehealth.standards.kmehr.schema.v1.Daytime;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.imec.ivlab.core.kmehr.model.RegimenEntry;
import org.imec.ivlab.core.util.ArrayUtil;
import org.joda.time.DateTime;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class RegimenUtil {

    public static List<BigInteger> getDayNumbers(Regimen regimen) {

        return regimen
            .getDaynumbersAndQuantitiesAndDates()
            .stream()
            .filter(s -> BigInteger.class.isInstance(s.getValue()))
            .map(s -> (BigInteger) s.getValue())
            .collect(Collectors.toList());

    }

    public static List<Calendar> getDates(Regimen regimen) {

        return regimen
            .getDaynumbersAndQuantitiesAndDates()
            .stream()
            .filter(s -> Calendar.class.isInstance(s.getValue()))
            .map(s -> (Calendar) s.getValue())
            .collect(Collectors.toList());

    }

    public static List<WeekdayType> getWeekdays(Regimen regimen) {

        return regimen
            .getDaynumbersAndQuantitiesAndDates()
            .stream()
            .filter(s -> WeekdayType.class.isInstance(s.getValue()))
            .map(s -> (WeekdayType) s.getValue())
            .collect(Collectors.toList());

    }

    public static List<Daytime> getDaytimes(Regimen regimen) {

        return regimen
            .getDaynumbersAndQuantitiesAndDates()
            .stream()
            .filter(s -> Daytime.class.isInstance(s.getValue()))
            .map(s -> (Daytime) s.getValue())
            .collect(Collectors.toList());

    }

    public static List<AdministrationquantityType> getQuantities(Regimen regimen) {

        return regimen
            .getDaynumbersAndQuantitiesAndDates()
            .stream()
            .filter(s -> AdministrationquantityType.class.isInstance(s.getValue()))
            .map(s -> (AdministrationquantityType) s.getValue())
            .collect(Collectors.toList());

    }

    public static List<RegimenEntry> getRegimenEntries(Regimen regimen) {

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

            JAXBElement<?> jaxbElement = (JAXBElement<?>) object;

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


            if (Daytime.class.isInstance(jaxbElement.getValue()) ) {
                regimenEntry.setDaytime((Daytime) jaxbElement.getValue());
                continue;
            }

            if (AdministrationquantityType.class.isInstance(jaxbElement.getValue()) ) {
                regimenEntry.setQuantity((AdministrationquantityType) jaxbElement.getValue());
                continue;
            }

            // TODO not quite sure why it is needed
            if (DateTime.class.isInstance(jaxbElement.getValue())) {
                DateTime dateTime = ( (DateTime) jaxbElement.getValue());
                regimenEntry.setDate(dateTime.toDate());
                continue;
            }

            throw new RuntimeException("Regimen entry field of type " + jaxbElement.getValue().getClass() + " cannot be used to create a regimen entry");

        }

        return regimenEntry;

    }

}
