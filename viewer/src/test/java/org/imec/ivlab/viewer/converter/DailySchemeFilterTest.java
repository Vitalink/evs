package org.imec.ivlab.viewer.converter;

import org.imec.ivlab.core.kmehr.model.FrequencyCode;
import org.imec.ivlab.core.model.internal.mapper.medication.Dayperiod;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenDayperiod;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenEntry;
import org.imec.ivlab.core.model.internal.mapper.medication.Suspension;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailySchemeFilterTest {

    @Test
    public void testCalculateDateForDaynumberOffsetting() throws Exception {

        LocalDate takeDate = LocalDate.of(2020, 10, 20);
        LocalDate medicationBeginDate = LocalDate.of(2020, 01, 01);

        List<Suspension> suspensions = createSuspensions();

        Assert.assertEquals(DailySchemeFilter.calculateDateForDaynumberOffsetting(FrequencyCode.D, medicationBeginDate, takeDate, suspensions), LocalDate.of(2020, 10, 19));
        Assert.assertEquals(DailySchemeFilter.calculateDateForDaynumberOffsetting(FrequencyCode.DA, medicationBeginDate, takeDate, null), medicationBeginDate);
        Assert.assertEquals(DailySchemeFilter.calculateDateForDaynumberOffsetting(FrequencyCode.WD, medicationBeginDate, takeDate, suspensions), medicationBeginDate);

    }

    @Test
    public void testRegimenEntryAppliesToDate() {

        LocalDate medicationBeginDate = LocalDate.of(2017, 3, 2);

        Assert.assertFalse(DailySchemeFilter.regimenEntryAppliesToDate(LocalDate.of(2017, 4, 1), createRegimenEntryWithoutDay(), FrequencyCode.M, medicationBeginDate, null));
        Assert.assertTrue(DailySchemeFilter.regimenEntryAppliesToDate(LocalDate.of(2017, 4, 2), createRegimenEntryWithoutDay(), FrequencyCode.M, medicationBeginDate, null));
        Assert.assertFalse(DailySchemeFilter.regimenEntryAppliesToDate(LocalDate.of(2017, 4, 3), createRegimenEntryWithoutDay(), FrequencyCode.M, medicationBeginDate, null));

    }

    private RegimenEntry createRegimenEntryWithoutDay() {

        RegimenEntry regimenEntry = new RegimenEntry();
        RegimenDayperiod regimenDayperiod = new RegimenDayperiod();
        regimenDayperiod.setDayperiod(Dayperiod.AFTERDINNER);
        regimenEntry.setDayperiodOrTime(regimenDayperiod);
        regimenEntry.setQuantity(BigDecimal.TEN);

        return regimenEntry;

    }

    private List<Suspension> createSuspensions() {

        List<Suspension> suspensions = new ArrayList<>();

        Suspension suspension1 = new Suspension();
        suspension1.setBeginDate(LocalDate.of(2020, 02, 20));
        suspension1.setEndDate(LocalDate.of(2020, 10, 18));
        suspensions.add(suspension1);

        Suspension suspension2 = new Suspension();
        suspension2.setBeginDate(LocalDate.of(2020, 03, 20));
        suspensions.add(suspension2);

        Suspension suspension3 = new Suspension();
        suspension3.setBeginDate(LocalDate.of(2020, 02, 20));
        suspension3.setEndDate(LocalDate.of(2020, 10, 03));
        suspensions.add(suspension3);

        Suspension suspension4 = new Suspension();
        suspension4.setBeginDate(LocalDate.of(2020, 10, 30));
        suspension4.setEndDate(LocalDate.of(2020, 12, 10));
        suspensions.add(suspension4);

        return suspensions;

    }

}