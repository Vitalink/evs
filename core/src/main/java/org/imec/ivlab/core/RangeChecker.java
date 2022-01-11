package org.imec.ivlab.core;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RangeChecker {

    private final static Logger LOG = LogManager.getLogger(RangeChecker.class);


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
            ArrayList periods = new ArrayList();
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


    private void applyInactivityPeriod(List<Period> activePeriods, Period inactivityPeriod) {

        if (CollectionsUtil.emptyOrNull(activePeriods)) {
            return;
        }

        if (!inactivityPeriod.isValid()) {
            return;
        }



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

    private boolean medicationOnOrAfterDate(LocalDate takeDate, LocalDate beginDate, LocalDate endDate, Duration duration) {

        return inOrAfterRange(takeDate, beginDate, endDate, duration);

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

            long daysBetween = ChronoUnit.DAYS.between(tempSchemeDate, medicationBeginDate);
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

            long daysBetween = ChronoUnit.DAYS.between(tempSchemeDate, medicationBeginDate);
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
            int differenceInMonths = 0;

            if (schemeDate.getYear() > (medicationBeginDate.getYear()) ) {

                if (schemeDate.getYear() - medicationBeginDate.getYear() > 1) {
                    differenceInMonths += (schemeDate.getYear() - medicationBeginDate.getYear() - 1) * 12;
                    differenceInMonths += schemeDate.getMonthValue();
                } else {
                    differenceInMonths += schemeDate.getMonthValue();
                }

                differenceInMonths += ( 12 - medicationBeginDate.getMonthValue());

            } else {
                differenceInMonths = schemeDate.getMonthValue() - medicationBeginDate.getMonthValue();
            }

            if (((differenceInMonths % (frequencyCode.getMultiplier().intValue()) == 0))) {

                if (schemeDate.getDayOfMonth() == dayOfMonth) {
                    return true;
                }

            }

        } else if (Frequency.YEAR.equals(frequencyCode.getFrequency())) {

            int dayOfMonth = date.getDayOfMonth();
            int monthOfYear = date.getMonthValue();

            int differenceInYears = schemeDate.getYear() - medicationBeginDate.getYear();

            if (((differenceInYears % (frequencyCode.getMultiplier().intValue()) == 0))) {

                if (schemeDate.getDayOfMonth() == dayOfMonth && schemeDate.getMonthValue() == monthOfYear) {
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

            DayOfWeek weekDayOfMedicationBeginDate = medicationBeginDate.getDayOfWeek();
            DayOfWeek weekDayOfFirstMedicationIntake = DayOfWeek.valueOf(weekday.name());
            int daysUntilFirstMedicationIntake = 0;

            if (weekDayOfMedicationBeginDate.getValue() < weekDayOfFirstMedicationIntake.getValue()) {
                daysUntilFirstMedicationIntake = weekDayOfFirstMedicationIntake.getValue() - weekDayOfMedicationBeginDate.getValue();
            } else if (weekDayOfFirstMedicationIntake.getValue() < weekDayOfMedicationBeginDate.getValue()) {
                daysUntilFirstMedicationIntake = 7 - (weekDayOfMedicationBeginDate.getValue() - weekDayOfFirstMedicationIntake.getValue());
            }

            LocalDate firstMedicationIntake = medicationBeginDate.plusDays(daysUntilFirstMedicationIntake);

            if (schemeDate.isBefore(firstMedicationIntake)) {
                return false;
            }

            long daysBetween = ChronoUnit.DAYS.between(schemeDate, firstMedicationIntake);
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

        LocalDateTime endDate = LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(), 0, 0);

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

            case A: endDate = endDate.plus(durationValue.longValue(), ChronoUnit.YEARS);
                break;
            case MO: endDate = endDate.plus(durationValue.longValue(), ChronoUnit.MONTHS);
                break;
            // For WEEKS, the supplied duration value was converted to days earlier in this function
            case WK: endDate = endDate.plus(durationValue.longValue(), ChronoUnit.DAYS);
                break;
            case D: endDate = endDate.plus(durationValue.longValue(), ChronoUnit.DAYS);
                break;
            case HR: endDate = endDate.plus(durationValue.longValue(), ChronoUnit.HOURS);
                break;
            case MIN: endDate = endDate.plus(durationValue.longValue(), ChronoUnit.MINUTES);
                break;
            case S: endDate = endDate.plus(durationValue.longValue(), ChronoUnit.SECONDS);
                break;
            case MS: endDate = endDate.plus(durationValue.longValue(), ChronoUnit.MILLIS);
                break;
            case US: endDate = endDate.plus(durationValue.longValue(), ChronoUnit.MICROS);
                break;
            case NS: endDate = endDate.plus(durationValue.longValue(), ChronoUnit.NANOS);
                break;

        }

        switch (durationUnit) {

            case A:
            case MO:
            case WK:
            case D:
                endDate = endDate.minus(1, ChronoUnit.DAYS);

        }

        return endDate.toLocalDate();


    }

}
