package org.imec.ivlab.viewer.converter;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTEMPORALITYvalues;
import org.apache.commons.lang3.SerializationUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.RangeChecker;
import org.imec.ivlab.core.kmehr.model.Frequency;
import org.imec.ivlab.core.kmehr.model.FrequencyCode;
import org.imec.ivlab.core.model.internal.mapper.medication.MedicationEntry;
import org.imec.ivlab.core.model.internal.mapper.medication.Posology;
import org.imec.ivlab.core.model.internal.mapper.medication.Regimen;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenDate;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenDaynumber;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenEntry;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenWeekday;
import org.imec.ivlab.core.model.internal.mapper.medication.Suspension;
import org.imec.ivlab.core.util.CollectionsUtil;

import org.joda.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DailySchemeFilter {

    //private final static Logger log = LogManager.getLogger(DailySchemeFilter.class);
    private static RangeChecker rangeChecker = new RangeChecker();


    public static List<MedicationEntry> filterMedicationForSchemeDate(LocalDate schemeDate, List<MedicationEntry> allMedicationEntries) {

        List<MedicationEntry> medicationEntries = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(allMedicationEntries)) {
            return medicationEntries;
        }

        for (MedicationEntry medicationEntry : allMedicationEntries) {

            if (rangeChecker.isDateInActivePeriod(schemeDate, medicationEntry)) {

                if (medicationEntry.getPosologyOrRegimen() instanceof Posology) {
                    // we can safely assume the medication is applicable today
                    medicationEntries.add(medicationEntry);
                    continue;
                }

                if (CDTEMPORALITYvalues.ONESHOT.equals(medicationEntry.getTemporality())) {
                    // for oneshot medication no further checking is needed
                    medicationEntries.add(medicationEntry);
                    continue;
                }

                if (medicationEntry.getPosologyOrRegimen() instanceof Regimen) {

                    MedicationEntry copy = SerializationUtils.clone(medicationEntry);

                    // for every regimen entry, verify if the regimen applies to the schemeDate

                    Regimen regimen = (Regimen) copy.getPosologyOrRegimen();

                    if (CollectionsUtil.notEmptyOrNull(regimen.getEntries())) {
                        Iterator<RegimenEntry> regimenEntryIterator = regimen.getEntries().iterator();

                        while (regimenEntryIterator.hasNext()) {
                            RegimenEntry regimenEntry = regimenEntryIterator.next();
                            if (!regimenEntryAppliesToDate(schemeDate, regimenEntry, medicationEntry.getFrequencyCode(), medicationEntry.getBeginDate(), medicationEntry.getSuspensions())) {
                                regimenEntryIterator.remove();
                            }

                        }

                    }

                    if (CollectionsUtil.notEmptyOrNull(regimen.getEntries())) {

                        medicationEntries.add(copy);

                    }

                }

            }
        }

        return medicationEntries;

    }

    public static LocalDate calculateDateForDaynumberOffsetting(FrequencyCode frequencyCode, LocalDate medicationBeginDate, LocalDate schemeDate, List<Suspension> suspensions) {

        if (CollectionsUtil.emptyOrNull(suspensions)) {
            return medicationBeginDate;
        }

        if (frequencyCode == null || !Frequency.DAY.equals(frequencyCode.getFrequency())) {
            return medicationBeginDate;
        }

        // only for Frequency codes on a daily basis, the daynumber for offesetting needs to be recalculated
        LocalDate lastSuspensionEndDateBeforeSchemeDate = null;
        for (Suspension suspension : suspensions) {
            LocalDate calculatedEndDate = rangeChecker.calculateEndDate(suspension.getBeginDate(), suspension.getEndDate(), suspension.getDuration());
            if (calculatedEndDate != null && calculatedEndDate.isBefore(schemeDate)) {
                if (lastSuspensionEndDateBeforeSchemeDate == null || calculatedEndDate.isAfter(lastSuspensionEndDateBeforeSchemeDate)) {
                    lastSuspensionEndDateBeforeSchemeDate = calculatedEndDate;
                }
            }
        }

        if (lastSuspensionEndDateBeforeSchemeDate != null) {
            return lastSuspensionEndDateBeforeSchemeDate.plusDays(1);
        } else {
            return medicationBeginDate;
        }

    }


    protected static boolean regimenEntryAppliesToDate(LocalDate schemeDate, RegimenEntry regimenEntry, FrequencyCode frequencyCode, LocalDate medicationBeginDate, List<Suspension> suspensions) {

        if (regimenEntry.getDaynumberOrDateOrWeekday() instanceof RegimenDaynumber) {

            RegimenDaynumber regimenDaynumber = (RegimenDaynumber) regimenEntry.getDaynumberOrDateOrWeekday();

            return rangeChecker.isActiveByDayNumber(schemeDate, frequencyCode, regimenDaynumber.getNumber(), calculateDateForDaynumberOffsetting(frequencyCode, medicationBeginDate, schemeDate, suspensions));

        } else if (regimenEntry.getDaynumberOrDateOrWeekday() instanceof RegimenWeekday) {

        RegimenWeekday regimenWeekday = (RegimenWeekday) regimenEntry.getDaynumberOrDateOrWeekday();

        return rangeChecker.isActiveByWeekday(schemeDate, frequencyCode, regimenWeekday.getWeekday(), medicationBeginDate);

        } else if (regimenEntry.getDaynumberOrDateOrWeekday() instanceof RegimenDate) {

            RegimenDate regimenDate = (RegimenDate) regimenEntry.getDaynumberOrDateOrWeekday();

            return rangeChecker.isActiveByDate(schemeDate, frequencyCode, regimenDate.getDate(), medicationBeginDate);

        } else {

            if (Frequency.MONTH.equals(frequencyCode.getFrequency())) {
                return rangeChecker.isActiveByDate(schemeDate, frequencyCode, medicationBeginDate, medicationBeginDate);
            }

            if (Frequency.YEAR.equals(frequencyCode.getFrequency())) {
                return rangeChecker.isActiveByDate(schemeDate, frequencyCode, medicationBeginDate, medicationBeginDate);
            }

            return rangeChecker.isActiveByDayNumber(schemeDate, frequencyCode, 1, medicationBeginDate);
        }


    }







}
