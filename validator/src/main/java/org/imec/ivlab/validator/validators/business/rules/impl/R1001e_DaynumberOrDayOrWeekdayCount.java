package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.AdministrationquantityType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.WeekdayType;
import org.imec.ivlab.core.kmehr.model.util.RegimenUtil;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;
import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.List;

public class R1001e_DaynumberOrDayOrWeekdayCount extends BaseMSEntryRule {

    @Override
    public String getMessage() {
        return "Only of one of the following can be used in a regimen: daynumber, day, weekday or none. They can't be used together.";
    }

    @Override
    public String getRuleId() {
        return "1001e";
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        ItemType medicationItem = TransactionUtil.getItem(msEntry.getMseTransaction(), CDITEMvalues.MEDICATION);

        List<BigInteger> dayNumbers = RegimenUtil.getDayNumbers(medicationItem.getRegimen());
        List<DateTime> dates = RegimenUtil.getDates(medicationItem.getRegimen());
        List<WeekdayType> weekdays = RegimenUtil.getWeekdays(medicationItem.getRegimen());

        List<AdministrationquantityType> quantities = RegimenUtil.getQuantities(medicationItem.getRegimen());
        int amountOfDayNumbersOrDatesOrWeekdays = CollectionsUtil.size(dayNumbers) + CollectionsUtil.size(dates) + CollectionsUtil.size(weekdays);

        if (amountOfDayNumbersOrDatesOrWeekdays == 0) {
            // OK, no daynumber/day/weekday specified
        } else if (amountOfDayNumbersOrDatesOrWeekdays == CollectionsUtil.size(quantities)) {
            // OK,
            // sum of daynumber/day/weekday equals total
            // let's check if the chosen option (daynumber/day/weekday) is used exclusively
            if (CollectionsUtil.size(dayNumbers) == CollectionsUtil.size(quantities) || CollectionsUtil.size(dates) == CollectionsUtil.size(quantities) || CollectionsUtil.size(weekdays) == CollectionsUtil.size(quantities)) {
                // OK, one of the options is chosen exclusively
            } else {
                // Not OK, none of the options is used exclusively
                return failRule(msEntry.getMseTransaction());
            }
        } else {
            // Not OK,
            // sum of daynumber/day/weekday does not equal total
            return failRule(msEntry.getMseTransaction());

        }

        return passRule();
    }


}
