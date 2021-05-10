package org.imec.ivlab.core.model.internal.mapper.medication.mapper;

import org.imec.ivlab.core.TestUtil;
import org.imec.ivlab.core.model.internal.mapper.medication.mapper.MedicationMapper;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntryList;
import org.imec.ivlab.core.kmehr.model.AdministrationUnit;
import org.imec.ivlab.core.kmehr.model.FrequencyCode;
import org.imec.ivlab.core.model.internal.mapper.medication.DaynumberOrDateOrWeekday;
import org.imec.ivlab.core.model.internal.mapper.medication.Dayperiod;
import org.imec.ivlab.core.model.internal.mapper.medication.DayperiodOrTime;
import org.imec.ivlab.core.model.internal.mapper.medication.MedicationEntry;
import org.imec.ivlab.core.model.internal.mapper.medication.PosologyOrRegimen;
import org.imec.ivlab.core.model.internal.mapper.medication.Regimen;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenDayperiod;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenWeekday;
import org.imec.ivlab.core.model.internal.mapper.medication.Route;
import org.imec.ivlab.core.model.internal.mapper.medication.Weekday;
import org.imec.ivlab.core.model.upload.msentrylist.MedicationSchemeExtractor;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicationMapperTest {

    @Test
    public void testtoMedicationEntryWithRegimenAndSuspensions() throws Exception {

        KmehrEntryList kmehrEntryList = TestUtil.getKmehrEntryList("1-map-kmehr-to-model-with-suspensions.txt");
        MSEntryList msEntryList = MedicationSchemeExtractor.getMedicationSchemeEntries(kmehrEntryList);

        MedicationEntry medicationEntry = MedicationMapper.transactionToMedicationEntry(msEntryList.getMsEntries().get(0).getMseTransaction(), msEntryList.getMsEntries().get(0).getTsTransactions());

        Assert.assertEquals(medicationEntry.getBeginCondition(), "innemen bij rugpijn 140");
        Assert.assertEquals(medicationEntry.getEndCondition(), "stoppen bij irritatie 140");
        Assert.assertEquals(medicationEntry.getBeginDate(), LocalDate.of(2020, 1, 1));
        Assert.assertEquals(medicationEntry.getEndDate(), null);
        Assert.assertEquals(medicationEntry.getFrequencyCode(), FrequencyCode.W);
        Assert.assertEquals(medicationEntry.getInstructionForPatient(), "140");
        Assert.assertEquals(medicationEntry.getMedicationUse(), "medicationuse 140");
        Assert.assertEquals(medicationEntry.getIdentifier().getName(), "Dafalgan Instant Vanille / Aardbei 500 mg (20 zakjes)");
        Assert.assertEquals(medicationEntry.getIdentifier().getId(), "3142288");
        Assert.assertEquals(medicationEntry.getRoute(), Route.R00060);

        // administrationunit
        PosologyOrRegimen posologyOrRegimen = medicationEntry.getPosologyOrRegimen();
        Regimen regimen = (Regimen) posologyOrRegimen;
        Assert.assertEquals(regimen.getAdministrationUnit(), AdministrationUnit.MG.getValue());

        // weekday
        DaynumberOrDateOrWeekday daynumberOrDateOrWeekday = regimen.getEntries().get(0).getDaynumberOrDateOrWeekday();
        RegimenWeekday regimenWeekday = (RegimenWeekday) daynumberOrDateOrWeekday;
        Assert.assertEquals(regimenWeekday.getWeekday(), Weekday.MONDAY);

        // dayperiod
        DayperiodOrTime dayperiodOrTime = regimen.getEntries().get(0).getDayperiodOrTime();
        RegimenDayperiod regimenDayperiod = (RegimenDayperiod) dayperiodOrTime;
        Assert.assertEquals(regimenDayperiod.getDayperiod(), Dayperiod.DURINGDINNER);

        // quantity
        Assert.assertEquals(regimen.getEntries().get(0).getQuantity(), BigDecimal.valueOf(1001));


        Assert.assertEquals(medicationEntry.getSuspensions().size(), 1);

        Assert.assertEquals(medicationEntry.getSuspensions().get(0).getReason(), "Wegens interactie met andere medicatie. tijdelijk.");
        Assert.assertEquals(medicationEntry.getSuspensions().get(0).getBeginDate(), LocalDate.of(2020, 6, 1));
        Assert.assertEquals(medicationEntry.getSuspensions().get(0).getEndDate(), LocalDate.of(2020, 12, 31));

    }

}