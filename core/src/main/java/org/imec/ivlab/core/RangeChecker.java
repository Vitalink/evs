package org.imec.ivlab.core;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.kmehr.model.Frequency;
import org.imec.ivlab.core.kmehr.model.FrequencyCode;
import org.imec.ivlab.core.model.internal.mapper.medication.Duration;
import org.imec.ivlab.core.model.internal.mapper.medication.MedicationEntry;
import org.imec.ivlab.core.model.internal.mapper.medication.Suspension;
import org.imec.ivlab.core.model.internal.mapper.medication.TimeUnit;
import org.imec.ivlab.core.model.internal.mapper.medication.Weekday;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.math.BigInteger;
import java.time.DayOfWeek;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class RangeChecker {

    //private final static Logger LOG = LogManager.getLogger(RangeChecker.class);


    /**
     * returns whether the given @date falls within an active period of the medication.
     * An active period is a moment >= medication beginmoment and <= medication endmoment and no suspension active on that date
     * @param date
     * @param medicationEntry
     * @return
     */
    public boolean isDateInActivePeriod(LocalDate date, MedicationEntry medicationEntry) {

        return medicationOnDate(date, medicationEntry.getBeginDate(), medicationEntry.getEndDate(), medicationEntry.getDuration()) && !anySuspensionOnDate(date, medicationEntry.getSuspensions());

    }

    /**
     * A simplified way of checking whether medication has ended at a given moment in time @date
     * @param date
     * @param medicationEntry
     * @return
     */
    public boolean isObsolete(LocalDate date, MedicationEntry medicationEntry) {

        if (medicationEntry == null) {
            return true;
        }

        boolean dateInActivePeriod = false;
        for (Period period : calculateActivePeriods(medicationEntry)) {
            if (!period.hasEnd() || date.isBefore(period.getEnd().plusDays(1))) {
                dateInActivePeriod = true;
            }
        }

        return !dateInActivePeriod;

    }

    public List<Period> calculateActivePeriods(MedicationEntry medicationEntry) {

        List<Period> activePeriods = new ArrayList<>();

        if (medicationEntry == null) {
            return activePeriods;
        }

        Period activePeriod = new Period(medicationEntry.getBeginDate(), medicationEntry.getEndDate());
        if (!activePeriod.isValid()) {
            return activePeriods;
        }

        if (CollectionsUtil.emptyOrNull(medicationEntry.getSuspensions())) {
            activePeriods.add(activePeriod);
            return activePeriods;
        }  else {

            List<Period> inactivityPeriods = new ArrayList<>();
            for (Suspension suspension : medicationEntry.getSuspensions()) {
                Period suspensionPeriod = new Period(suspension.getBeginDate(), calculateEndDate(suspension.getBeginDate(), suspension.getEndDate(), suspension.getDuration()));
                inactivityPeriods.add(suspensionPeriod);
            }

            return calculateActivityPeriods(activePeriod, inactivityPeriods);

        }

    }

    private List<Period> calculateActivityPeriods(Period activePeriod, List<Period> inactivityPeriods) {

        if (activePeriod == null || !activePeriod.isValid()) {
            return null;
        }

        if (CollectionsUtil.emptyOrNull(inactivityPeriods)) {
            ArrayList<Period> periods = new ArrayList<Period>();
            periods.add(activePeriod);
            return periods;
        }

        Range<LocalDate> activeRange = periodToRange(activePeriod);
        TreeRangeSet<LocalDate> rangeSet = TreeRangeSet.create();
        rangeSet.add(activeRange);


        for (Period inactivityPeriod : inactivityPeriods) {
            if  (inactivityPeriod.isValid()) {
                Range<LocalDate> inactivityRange = periodToRange(inactivityPeriod);
                rangeSet.remove(inactivityRange);
            }
        }

        List<Period> periods = new ArrayList<>();

        Set<Range<LocalDate>> ranges = rangeSet.asRanges();
        for (Range<LocalDate> range : ranges) {
            Period period = rangeToPeriod(range);
            if (period.isValid()) {
                periods.add(period);
            }
        }

        return periods;

    }

    private Range<LocalDate> periodToRange(Period period) {
        Range<LocalDate> range;
        if (period.hasEnd()) {
            range = Range.closed(period.getStart(), period.getEnd());
        } else {
            range = Range.atLeast(period.getStart());
        }
        return range;
    }

    private Period rangeToPeriod(Range<LocalDate> range) {

        if (range == null) {
            return null;
        }

        LocalDate start = null;
        LocalDate end = null;

        if (range.hasLowerBound()) {
            if (BoundType.CLOSED.equals(range.lowerBoundType())) {
                start = range.lowerEndpoint();
            } else {
                start = range.lowerEndpoint().plusDays(1);
            }
        }

        if (range.hasUpperBound()) {
            if (BoundType.CLOSED.equals(range.upperBoundType())) {
                end = range.upperEndpoint();
            } else {
                end = range.upperEndpoint().minusDays(1);
            }
        }

        return new Period(start, end);

    }

    private boolean anySuspensionOnDate(LocalDate takeDate, List<Suspension> suspensions) {

        if (CollectionsUtil.notEmptyOrNull(suspensions)) {
            for (Suspension suspension : suspensions) {
                if (suspensionOnDate(takeDate, suspension)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean medicationOnDate(LocalDate takeDate, LocalDate beginDate, LocalDate endDate, Duration duration) {

        return inRange(takeDate, beginDate, endDate, duration);

    }

    private boolean suspensionOnDate(LocalDate takeDate, Suspension suspension) {

        return inRange(takeDate, suspension.getBeginDate(), suspension.getEndDate(), suspension.getDuration());

    }

    public boolean isActiveByDayNumber(LocalDate schemeDate, FrequencyCode frequencyCode, int dayNumber, LocalDate medicationBeginDate) {

        if (frequencyCode == null) {

            // check if beginmoment + delta == exact date of schema generation
            if (medicationBeginDate.plusDays(dayNumber - 1).equals(schemeDate)) {
                return true;
            }

        } else if (Frequency.DAY.equals(frequencyCode.getFrequency())) {
            // check if date of schema generation - delta is a multiple of the frequency

            if (dayNumber < 1) {
                return false;
            }

            LocalDate tempSchemeDate = schemeDate.minusDays(dayNumber - 1);

            if (tempSchemeDate.isBefore(medicationBeginDate)) {
                return false;
            }

            long daysBetween = Days.daysBetween(tempSchemeDate, medicationBeginDate).getDays();
            
            if ((daysBetween % frequencyCode.getMultiplier().intValue()) == 0) {
                return true;
            }


        } else if (Frequency.WEEK.equals(frequencyCode.getFrequency())) {
            // check if date of schema generation - delta is a multiple of the frequency

            if (dayNumber < 1) {
                return false;
            }

            LocalDate tempSchemeDate = schemeDate.minusDays(dayNumber - 1);

            if (tempSchemeDate.isBefore(medicationBeginDate)) {
                return false;
            }

            long daysBetween = Days.daysBetween(tempSchemeDate, medicationBeginDate).getDays();
            if ((daysBetween % (frequencyCode.getMultiplier().intValue() * 7)) == 0) {
                return true;
            }

        }

        return false;

    }

    public boolean isActiveByDate(LocalDate schemeDate, FrequencyCode frequencyCode, LocalDate date, LocalDate medicationBeginDate) {

        if (schemeDate.isBefore(medicationBeginDate)) {
            return false;
        }

        if (frequencyCode == null) {
            return false;
        } else if (Frequency.MONTH.equals(frequencyCode.getFrequency())) {

            int dayOfMonth = date.getDayOfMonth();
            int differenceInMonths = Months.monthsBetween(schemeDate, medicationBeginDate).getMonths();

            if (((differenceInMonths % (frequencyCode.getMultiplier().intValue()) == 0))) {

                if (schemeDate.getDayOfMonth() == dayOfMonth) {
                    return true;
                }

            }

        } else if (Frequency.YEAR.equals(frequencyCode.getFrequency())) {

            int dayOfMonth = date.getDayOfMonth();
            int monthOfYear = date.getMonthOfYear();

            int differenceInYears = schemeDate.getYear() - medicationBeginDate.getYear();

            if (((differenceInYears % (frequencyCode.getMultiplier().intValue()) == 0))) {

                if (schemeDate.getDayOfMonth() == dayOfMonth && schemeDate.getMonthOfYear() == monthOfYear) {
                    return true;
                }

            }

        }

        return false;


    }

    public boolean isActiveByWeekday(LocalDate schemeDate, FrequencyCode frequencyCode, Weekday weekday, LocalDate medicationBeginDate) {

        if (frequencyCode == null) {
            return false;
        } else if (Frequency.WEEK.equals(frequencyCode.getFrequency())) {

            int weekDayOfMedicationBeginDate = medicationBeginDate.getDayOfWeek();
            int weekDayOfFirstMedicationIntake = DayOfWeek.valueOf(weekday.name()).getValue();
            int daysUntilFirstMedicationIntake = 0;

            if (weekDayOfMedicationBeginDate < weekDayOfFirstMedicationIntake) {
                daysUntilFirstMedicationIntake = weekDayOfFirstMedicationIntake - weekDayOfMedicationBeginDate;
            } else if (weekDayOfFirstMedicationIntake < weekDayOfMedicationBeginDate) {
                daysUntilFirstMedicationIntake = 7 - (weekDayOfMedicationBeginDate - weekDayOfFirstMedicationIntake);
            }

            LocalDate firstMedicationIntake = medicationBeginDate.plusDays(daysUntilFirstMedicationIntake);

            if (schemeDate.isBefore(firstMedicationIntake)) {
                return false;
            }

            long daysBetween = Days.daysBetween(schemeDate, firstMedicationIntake).getDays();
            if (((daysBetween % (frequencyCode.getMultiplier().intValue() * 7) == 0))) {
                return true;
            }

            return false;
        }

        return false;

    }

    public boolean inOrAfterRange(LocalDate takeDate, LocalDate beginDate, LocalDate endDate, Duration duration) {
        LocalDate endDateToUse = calculateEndDate(beginDate, endDate, duration);

        if (endDateToUse != null && endDateToUse.isBefore(takeDate)) {
            return false;
        }

        return true;
    }

    public boolean inRange(LocalDate takeDate, LocalDate beginDate, LocalDate endDate, Duration duration) {

        if (beginDate.isAfter(takeDate)) {
            return false;
        }

        LocalDate endDateToUse = calculateEndDate(beginDate, endDate, duration);

        if (endDateToUse != null && endDateToUse.isBefore(takeDate)) {
            return false;
        }

        return true;

    }

    /**
     * Calculate the final end date, which is based on either the begindate + duration, or equals the enddate already provided
     * @param beginDate
     * @param endDate
     * @param duration
     * @return
     */
    public LocalDate calculateEndDate(LocalDate beginDate, LocalDate endDate, Duration duration) {
        if (duration != null) {
            return calculateEndDateForDuration(beginDate, duration);
        } else if (endDate != null) {
            return endDate;
        } else {
            return null;
        }
    }

    /**
     * Calculate the end date for a given start date and a duration
     * @param startDate
     * @param duration
     * @return
     */
    public LocalDate calculateEndDateForDuration(LocalDate startDate, Duration duration) {

        if (startDate == null) {
            return null;
        }

        LocalDateTime endDate = new LocalDateTime(
            startDate.getYear(),
            startDate.getMonthOfYear(),
            startDate.getDayOfMonth(),
            0,
            0, 
            0, 
            0 
        );

        if (duration == null) {
            return endDate.toLocalDate();
        }

        BigInteger durationValue = duration.getValue().getMappedOrThrow();
        TimeUnit durationUnit = duration.getUnit().getMappedOrThrow();

        if (durationUnit == null || durationValue == null || durationValue.compareTo(BigInteger.ZERO) < 1) {
            return endDate.toLocalDate();
        }

        if (TimeUnit.WK.equals(durationUnit)) {
            // Convert to Days
            durationValue = durationValue.multiply(BigInteger.valueOf(7));
        }

        switch (durationUnit) {

            case A: 
                endDate = endDate.plusYears(durationValue.intValue());
                break;
            case MO: 
                endDate = endDate.plusMonths(durationValue.intValue());
                break;
            // For WEEKS, the supplied duration value was converted to days earlier in this function
            case WK: 
                endDate = endDate.plusDays(durationValue.intValue());
                break;
            case D: 
                endDate = endDate.plusDays(durationValue.intValue());
                break;
            case HR:
                endDate = endDate.plusHours(durationValue.intValue());
                break;
            case MIN: 
                endDate = endDate.plusMinutes(durationValue.intValue());
                break;
            case S: 
                endDate = endDate.plusSeconds(durationValue.intValue());
                break;
            case MS: 
                endDate = endDate.plusMillis(durationValue.intValue());
                break;
            case US: 
                endDate = endDate.plusMillis(durationValue.intValue() / 1000);
                //endDate = endDate.plus(durationValue.longValue(), ChronoUnit.MICROS);
                break;
            case NS: 
                endDate = endDate.plusMillis(durationValue.intValue() / 1000000);
                //endDate = endDate.plus(durationValue.longValue(), ChronoUnit.NANOS);
                break;

        }

        // Vitalink/EVS in the past substract one day for authorized units, to compute the endDate properly
        List<TimeUnit> unitsToSubtractOneDay = Arrays.asList(TimeUnit.A, TimeUnit.MO, TimeUnit.WK, TimeUnit.D);
        if (unitsToSubtractOneDay.contains(durationUnit)) {
            endDate = endDate.minusDays(1);
        }

        return endDate.toLocalDate();


    }

}
