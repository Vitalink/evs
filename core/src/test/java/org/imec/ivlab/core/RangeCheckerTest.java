package org.imec.ivlab.core;

import org.imec.ivlab.core.kmehr.model.FrequencyCode;
import org.imec.ivlab.core.model.internal.mapper.MappingResult;
import org.imec.ivlab.core.model.internal.mapper.medication.Duration;
import org.imec.ivlab.core.model.internal.mapper.medication.MedicationEntry;
import org.imec.ivlab.core.model.internal.mapper.medication.Suspension;
import org.imec.ivlab.core.model.internal.mapper.medication.TimeUnit;
import org.imec.ivlab.core.model.internal.mapper.medication.Weekday;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;


@org.testng.annotations.Test
public class RangeCheckerTest {

    @org.testng.annotations.Test
    public void calculateActivePeriodsMedicationWithEndmomentAndStopped() {
        MedicationEntry medicationEntry = createMedicationEntry(new LocalDate(2017, 5, 15), new LocalDate(2020, 10, 20));
        medicationEntry.setSuspensions(createSuspensionsWithLastOneStopped());

        RangeChecker rangeChecker = new RangeChecker();
        List<Period> periods = rangeChecker.calculateActivePeriods(medicationEntry);

        assertThat(periods, hasSize(3));
        assertThat(periods, contains(   new Period(new LocalDate(2017, 5, 15), new LocalDate(2017, 7, 12)),
                new Period(new LocalDate(2017, 9, 14), new LocalDate(2017, 9, 14)),
                new Period(new LocalDate(2017, 9, 20), new LocalDate(2020, 10, 17))));

    }

    @org.testng.annotations.Test
    public void calculateActivePeriodsMedicationWithoutEndmomentAndStopped() {
        MedicationEntry medicationEntry = createMedicationEntry(new LocalDate(2017, 5, 15), null);
        medicationEntry.setSuspensions(createSuspensionsWithLastOneStopped());

        RangeChecker rangeChecker = new RangeChecker();
        List<Period> periods = rangeChecker.calculateActivePeriods(medicationEntry);

        assertThat(periods, hasSize(3));
        assertThat(periods, contains(   new Period(new LocalDate(2017, 5, 15), new LocalDate(2017, 7, 12)),
                new Period(new LocalDate(2017, 9, 14), new LocalDate(2017, 9, 14)),
                new Period(new LocalDate(2017, 9, 20), new LocalDate(2020, 10, 17))));

    }

    private MedicationEntry createMedicationEntry(LocalDate beginmoment, LocalDate endmoment) {

        MedicationEntry medicationEntry = new MedicationEntry();
        medicationEntry.setBeginDate(beginmoment);
        medicationEntry.setEndDate(endmoment);

        return medicationEntry;

    }

    @org.testng.annotations.Test
    public void calculateActivePeriodsMedicationWithEndmomentAndSuspended() {
        MedicationEntry medicationEntry = createMedicationEntry(new LocalDate(2017, 5, 15), new LocalDate(2020, 10, 20));
        medicationEntry.setSuspensions(createSuspensionsWithLastSuspended());

        RangeChecker rangeChecker = new RangeChecker();
        List<Period> periods = rangeChecker.calculateActivePeriods(medicationEntry);

        assertThat(periods, hasSize(4));
        assertThat(periods, contains(   new Period(new LocalDate(2017, 5, 15), new LocalDate(2017, 7, 12)),
                new Period(new LocalDate(2017, 9, 14), new LocalDate(2017, 9, 14)),
                new Period(new LocalDate(2017, 9, 20), new LocalDate(2020, 10, 17)),
                new Period(new LocalDate(2020, 10, 20), new LocalDate(2020, 10, 20))));

    }

    @org.testng.annotations.Test
    public void calculateActivePeriodsMedicationWithoutEndmomentAndSuspended() {
        MedicationEntry medicationEntry = createMedicationEntry(new LocalDate(2017, 5, 15), null);
        medicationEntry.setSuspensions(createSuspensionsWithLastSuspended());

        RangeChecker rangeChecker = new RangeChecker();
        List<Period> periods = rangeChecker.calculateActivePeriods(medicationEntry);

        assertThat(periods, hasSize(4));
        assertThat(periods, contains(   new Period(new LocalDate(2017, 5, 15), new LocalDate(2017, 7, 12)),
                new Period(new LocalDate(2017, 9, 14), new LocalDate(2017, 9, 14)),
                new Period(new LocalDate(2017, 9, 20), new LocalDate(2020, 10, 17)),
                new Period(new LocalDate(2020, 10, 20), null)));

    }

    private List<Suspension> createSuspensionsWithLastOneStopped() {

        List<Suspension> suspensions = new ArrayList<>();

        Suspension suspension1 = new Suspension();

        suspension1.setBeginDate(new LocalDate(2017, 7, 13));
        suspension1.setEndDate(new LocalDate(2017, 8, 13));
        suspensions.add(suspension1);

        // overlapping multiple days with previous
        Suspension suspension2 = new Suspension();
        suspension2.setBeginDate(new LocalDate(2017, 8, 1));
        suspension2.setEndDate(new LocalDate(2017, 9, 13));
        suspensions.add(suspension2);

        // 1 day between previous and this one
        Suspension suspension3 = new Suspension();
        suspension3.setBeginDate(new LocalDate(2017, 9, 15));
        suspension3.setEndDate(new LocalDate(2017, 9, 17));
        suspensions.add(suspension3);

        // 0 days between previosu and this one
        Suspension suspension4 = new Suspension();
        suspension4.setBeginDate(new LocalDate(2017, 9, 17));
        suspension4.setEndDate(new LocalDate(2017, 9, 19));
        suspensions.add(suspension4);

        // 0 days between previosu and this one
        Suspension suspension5 = new Suspension();
        suspension5.setBeginDate(new LocalDate(2017, 9, 19));
        suspension5.setEndDate(new LocalDate(2017, 9, 19));
        suspensions.add(suspension5);

        // suspension without endmoment
        Suspension suspension6 = new Suspension();
        suspension6.setBeginDate(new LocalDate(2020, 10, 18));
        suspensions.add(suspension6);

        return suspensions;

    }

    private List<Suspension> createSuspensionsWithLastSuspended() {

        List<Suspension> suspensions = new ArrayList<>();

        Suspension suspension1 = new Suspension();
        suspension1.setBeginDate(new LocalDate(2017, 7, 13));
        suspension1.setEndDate(new LocalDate(2017, 8, 13));
        suspensions.add(suspension1);

        // overlapping multiple days with previous
        Suspension suspension2 = new Suspension();
        suspension2.setBeginDate(new LocalDate(2017, 8, 1));
        suspension2.setEndDate(new LocalDate(2017, 9, 13));
        suspensions.add(suspension2);

        // 1 day between previous and this one
        Suspension suspension3 = new Suspension();
        suspension3.setBeginDate(new LocalDate(2017, 9, 15));
        suspension3.setEndDate(new LocalDate(2017, 9, 17));
        suspensions.add(suspension3);

        // 0 days between previosu and this one
        Suspension suspension4 = new Suspension();
        suspension4.setBeginDate(new LocalDate(2017, 9, 17));
        suspension4.setEndDate(new LocalDate(2017, 9, 19));
        suspensions.add(suspension4);

        // 0 days between previosu and this one
        Suspension suspension5 = new Suspension();
        suspension5.setBeginDate(new LocalDate(2017, 9, 19));
        suspension5.setEndDate(new LocalDate(2017, 9, 19));
        suspensions.add(suspension5);

        // suspension without endmoment
        Suspension suspension6 = new Suspension();
        suspension6.setBeginDate(new LocalDate(2020, 10, 18));
        suspension6.setEndDate(new LocalDate(2020, 10, 19));
        suspensions.add(suspension6);

        return suspensions;

    }

    @org.testng.annotations.Test
    public void testIsActiveByDayNumberAndDailyFrequency() throws Exception {

        LocalDate medicationBeginDate = new LocalDate(2017, 3, 2);

        RangeChecker rangeChecker = new RangeChecker();
        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 2), FrequencyCode.DA, 6, medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 3), FrequencyCode.DA, 6, medicationBeginDate));

        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 6), FrequencyCode.DA, 6, medicationBeginDate));
        Assert.assertTrue(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 7), FrequencyCode.DA, 6, medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 8), FrequencyCode.DA, 6, medicationBeginDate));

        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 14), FrequencyCode.DA, 6, medicationBeginDate));
        Assert.assertTrue(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 15), FrequencyCode.DA, 6, medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 16), FrequencyCode.DA, 6, medicationBeginDate));

    }

    @org.testng.annotations.Test
    public void testIsActiveByDayNumberAndWeeklyFrequency() throws Exception {

        LocalDate medicationBeginDate = new LocalDate(2017, 3, 2);

        RangeChecker rangeChecker = new RangeChecker();
        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 1), FrequencyCode.W, 1, medicationBeginDate));
        Assert.assertTrue(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 2), FrequencyCode.W, 1, medicationBeginDate));

        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 8), FrequencyCode.W, 1, medicationBeginDate));
        Assert.assertTrue(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 9), FrequencyCode.W, 1, medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 10), FrequencyCode.W, 1, medicationBeginDate));

        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 5), FrequencyCode.W, 5, medicationBeginDate));
        Assert.assertTrue(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 6), FrequencyCode.W, 5, medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 7), FrequencyCode.W, 5, medicationBeginDate));

        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 19), FrequencyCode.W, 5, medicationBeginDate));
        Assert.assertTrue(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 20), FrequencyCode.W, 5, medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 3, 21), FrequencyCode.W, 5, medicationBeginDate));

        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 2, 23), FrequencyCode.W, 1, medicationBeginDate));


        // 12 weeks -> takedate 25/05/2017
        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 5, 24), FrequencyCode.WW, 1, medicationBeginDate));
        Assert.assertTrue(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 5, 25), FrequencyCode.WW, 1, medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 5, 26), FrequencyCode.WW, 1, medicationBeginDate));

        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 5, 30), FrequencyCode.WW, 7, medicationBeginDate));
        Assert.assertTrue(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 5, 31), FrequencyCode.WW, 7, medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByDayNumber(new LocalDate(2017, 6, 1), FrequencyCode.WW, 7, medicationBeginDate));

    }

    @org.testng.annotations.Test
    public void testIsActiveByWeekday() throws Exception {

        LocalDate medicationBeginDate = new LocalDate(2017, 3, 2); // = THURSDAY

        RangeChecker rangeChecker = new RangeChecker();
        Assert.assertFalse(rangeChecker.isActiveByWeekday(new LocalDate(2017, 3, 2), FrequencyCode.WT, Weekday.TUESDAY, medicationBeginDate));

        Assert.assertFalse(rangeChecker.isActiveByWeekday(new LocalDate(2017, 3, 6), FrequencyCode.WT, Weekday.TUESDAY, medicationBeginDate));
        Assert.assertTrue(rangeChecker.isActiveByWeekday(new LocalDate(2017, 3, 7), FrequencyCode.WT, Weekday.TUESDAY, medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByWeekday(new LocalDate(2017, 3, 8), FrequencyCode.WT, Weekday.TUESDAY, medicationBeginDate));

        Assert.assertFalse(rangeChecker.isActiveByWeekday(new LocalDate(2017, 3, 14), FrequencyCode.WT, Weekday.TUESDAY, medicationBeginDate));

        Assert.assertFalse(rangeChecker.isActiveByWeekday(new LocalDate(2017, 3, 20), FrequencyCode.WT, Weekday.TUESDAY, medicationBeginDate));
        Assert.assertTrue(rangeChecker.isActiveByWeekday(new LocalDate(2017, 3, 21), FrequencyCode.WT, Weekday.TUESDAY, medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByWeekday(new LocalDate(2017, 3, 22), FrequencyCode.WT, Weekday.TUESDAY, medicationBeginDate));

        medicationBeginDate = new LocalDate(2020, 01, 01); // = WEDNESDAY

        Assert.assertFalse(rangeChecker.isActiveByWeekday(new LocalDate(2022, 9, 21), FrequencyCode.W, Weekday.THURSDAY, medicationBeginDate));
        Assert.assertTrue(rangeChecker.isActiveByWeekday(new LocalDate(2022, 9, 22), FrequencyCode.W, Weekday.THURSDAY, medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByWeekday(new LocalDate(2022, 9, 23), FrequencyCode.W, Weekday.THURSDAY, medicationBeginDate));

    }


    @org.testng.annotations.Test
    public void testIsActiveByDateForMonthlyFrequencies() throws Exception {

        LocalDate medicationBeginDate = new LocalDate(2017, 3, 2);

        RangeChecker rangeChecker = new RangeChecker();

        // before medication date
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2017, 3, 12), FrequencyCode.MV, new LocalDate(2016, 12, 13), medicationBeginDate));
        // first date of medication
        Assert.assertTrue(rangeChecker.isActiveByDate(new LocalDate(2017, 3, 13), FrequencyCode.MV, new LocalDate(2016, 12, 13), medicationBeginDate));
        // day after first day
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2017, 3, 14), FrequencyCode.MV, new LocalDate(2016, 12, 13), medicationBeginDate));

        // day before first frequency hit
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2017, 7, 12), FrequencyCode.MV, new LocalDate(2016, 12, 13), medicationBeginDate));
        // first frequency hit, after 4 months
        Assert.assertTrue(rangeChecker.isActiveByDate(new LocalDate(2017, 7, 13), FrequencyCode.MV, new LocalDate(2016, 12, 13), medicationBeginDate));
        // 1 day after first frequency hit
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2017, 7, 14), FrequencyCode.MV, new LocalDate(2016, 12, 13), medicationBeginDate));

        // days in month after first frequency hit
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2017, 8, 12), FrequencyCode.MV, new LocalDate(2016, 12, 13), medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2017, 8, 13), FrequencyCode.MV, new LocalDate(2016, 12, 13), medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2017, 8, 14), FrequencyCode.MV, new LocalDate(2016, 12, 13), medicationBeginDate));

        // beginmoment year + 1
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2018, 11, 12), FrequencyCode.MV, new LocalDate(2016, 12, 13), medicationBeginDate));
        Assert.assertTrue(rangeChecker.isActiveByDate(new LocalDate(2018, 11, 13), FrequencyCode.MV, new LocalDate(2016, 12, 13), medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2018, 11, 14), FrequencyCode.MV, new LocalDate(2016, 12, 13), medicationBeginDate));

        // beginmoment year + 2
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2019, 11, 12), FrequencyCode.MV, new LocalDate(2016, 12, 13), medicationBeginDate));
        Assert.assertTrue(rangeChecker.isActiveByDate(new LocalDate(2019, 11, 13), FrequencyCode.MV, new LocalDate(2016, 12, 13), medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2019, 11, 14), FrequencyCode.MV, new LocalDate(2016, 12, 13), medicationBeginDate));

    }

    @org.testng.annotations.Test
    public void testIsActiveByDateForYearlyFrequencies() throws Exception {

        LocalDate medicationBeginDate = new LocalDate(2017, 3, 2);

        RangeChecker rangeChecker = new RangeChecker();

        // before medication date
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2017, 11, 12), FrequencyCode.JD, new LocalDate(2016, 11, 13), medicationBeginDate));
        // first date of medication
        Assert.assertTrue(rangeChecker.isActiveByDate(new LocalDate(2017, 11, 13), FrequencyCode.JD, new LocalDate(2016, 11, 13), medicationBeginDate));
        // day after first day
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2017, 11, 14), FrequencyCode.JD, new LocalDate(2016, 11, 13), medicationBeginDate));

        // day before first frequency hit
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2020, 11, 12), FrequencyCode.JD, new LocalDate(2016, 11, 13), medicationBeginDate));
        // first frequency hit, after 3 years
        Assert.assertTrue(rangeChecker.isActiveByDate(new LocalDate(2020, 11, 13), FrequencyCode.JD, new LocalDate(2016, 11, 13), medicationBeginDate));
        // 1 day after first frequency hit
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2020, 11, 14), FrequencyCode.JD, new LocalDate(2016, 11, 13), medicationBeginDate));

        // days in month after first frequency hit
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2020, 12, 13), FrequencyCode.JD, new LocalDate(2016, 11, 13), medicationBeginDate));

        // beginmoment year + 6
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2023, 11, 12), FrequencyCode.JD, new LocalDate(2016, 11, 13), medicationBeginDate));
        Assert.assertTrue(rangeChecker.isActiveByDate(new LocalDate(2023, 11, 13), FrequencyCode.JD, new LocalDate(2016, 11, 13), medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2023, 11, 14), FrequencyCode.JD, new LocalDate(2016, 11, 13), medicationBeginDate));

        // beginmoment year + 9
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2026, 11, 12), FrequencyCode.JD, new LocalDate(2016, 11, 13), medicationBeginDate));
        Assert.assertTrue(rangeChecker.isActiveByDate(new LocalDate(2026, 11, 13), FrequencyCode.JD, new LocalDate(2016, 11, 13), medicationBeginDate));
        Assert.assertFalse(rangeChecker.isActiveByDate(new LocalDate(2026, 11, 14), FrequencyCode.JD, new LocalDate(2016, 11, 13), medicationBeginDate));

    }

    @Test
    public void TestCalculateEndDate() {

        LocalDate startDate = new LocalDate(2016, 5, 31);

        RangeChecker rangeChecker = new RangeChecker();
        Assert.assertEquals(rangeChecker.calculateEndDateForDuration(startDate, createDuration(1, TimeUnit.D)), new LocalDate(2016, 5, 31));
        Assert.assertEquals(rangeChecker.calculateEndDateForDuration(startDate, createDuration(2, TimeUnit.D)), new LocalDate(2016, 6, 1));
        Assert.assertEquals(rangeChecker.calculateEndDateForDuration(startDate, createDuration(1, TimeUnit.MO)), new LocalDate(2016, 6, 29));
        Assert.assertEquals(rangeChecker.calculateEndDateForDuration(startDate, createDuration(1, TimeUnit.A)), new LocalDate(2017, 5, 30));
        Assert.assertEquals(rangeChecker.calculateEndDateForDuration(startDate, createDuration(1, TimeUnit.A)), new LocalDate(2017, 5, 30));

    }

    @Test
    public void testInRange() throws Exception {

        LocalDate takeDate = new LocalDate(2020, 5, 10);

        RangeChecker rangeChecker = new RangeChecker();

        // with enddate
        Assert.assertFalse(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 6), new LocalDate(2020, 5, 8), null));
        Assert.assertFalse(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 6), new LocalDate(2020, 5, 9), null));
        Assert.assertTrue(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 6), new LocalDate(2020, 5, 10), null));
        Assert.assertTrue(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 6), new LocalDate(2020, 5, 11), null));
        Assert.assertTrue(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 6), new LocalDate(2020, 5, 12), null));
        Assert.assertTrue(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 6), new LocalDate(2020, 5, 13), null));
        Assert.assertTrue(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 9), new LocalDate(2020, 5, 13), null));
        Assert.assertTrue(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 10), new LocalDate(2020, 5, 13), null));
        Assert.assertFalse(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 11), new LocalDate(2020, 5, 13), null));
        Assert.assertFalse(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 12), new LocalDate(2020, 5, 13), null));

        // without enddate or duration
        Assert.assertFalse(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 12), null, null));
        Assert.assertFalse(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 11), null, null));
        Assert.assertTrue(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 10), null, null));
        Assert.assertTrue(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 9), null, null));
        Assert.assertTrue(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 8), null, null));

        // with duration
        Assert.assertTrue(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 10), null, createDuration(0, TimeUnit.D)));
        Assert.assertTrue(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 10), null, createDuration(1, TimeUnit.D)));
        Assert.assertTrue(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 9), null, createDuration(2, TimeUnit.D)));
        Assert.assertFalse(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 8), null, createDuration(2, TimeUnit.D)));

        Assert.assertTrue(rangeChecker.inRange(takeDate, new LocalDate(2020, 4, 28), null, createDuration(2, TimeUnit.WK)));
        Assert.assertFalse(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 27), null, createDuration(2, TimeUnit.WK)));

        // with enddate and duration (duration must be taken into account instead of enddate)
        Assert.assertTrue(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 1), new LocalDate(2020, 5, 30), createDuration(10, TimeUnit.D)));
        Assert.assertFalse(rangeChecker.inRange(takeDate, new LocalDate(2020, 5, 1), new LocalDate(2020, 5, 30), createDuration(9, TimeUnit.D)));

    }

    private Duration createDuration(int value, TimeUnit timeUnit) {
        Duration duration = new Duration();
        duration.setUnit(MappingResult.successful(timeUnit));
        duration.setValue(MappingResult.successful(BigInteger.valueOf(value)));
        return duration;
    }


}