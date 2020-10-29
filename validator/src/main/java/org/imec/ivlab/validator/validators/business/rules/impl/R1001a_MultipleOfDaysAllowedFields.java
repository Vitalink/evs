package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDPERIODICITY;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.WeekdayType;
import org.apache.commons.collections.CollectionUtils;
import org.imec.ivlab.core.kmehr.model.Frequency;
import org.imec.ivlab.core.kmehr.model.FrequencyCode;
import org.imec.ivlab.core.kmehr.model.util.FrequencyUtil;
import org.imec.ivlab.core.kmehr.model.util.RegimenUtil;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.util.Calendar;
import java.util.List;

public class R1001a_MultipleOfDaysAllowedFields extends BaseMSEntryRule implements MSEntryRule {

    @Override
    public String getMessage() {
        return "When using a multiplier of days, only the daynumber can be used";
    }

    @Override
    public String getRuleId() {
        return "1001a";
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry) {

        ItemType medicationItem = TransactionUtil.getItem(msEntry.getMseTransaction(), CDITEMvalues.MEDICATION);

        List<Calendar> dates = RegimenUtil.getDates(medicationItem.getRegimen());
        List<WeekdayType> weekdays = RegimenUtil.getWeekdays(medicationItem.getRegimen());
        CDPERIODICITY dayPeriod = FrequencyUtil.getDayPeriod(medicationItem.getFrequency());

        if (dayPeriod != null) {

            FrequencyCode frequencyCode = FrequencyCode.fromValue(dayPeriod.getValue());

            if (frequencyCode.hasFrequency(Frequency.DAY)) {

                if (CollectionUtils.isNotEmpty(weekdays) || CollectionUtils.isNotEmpty(dates)) {
                    return failRule(msEntry.getMseTransaction());
                }

            }

        }


        return passRule();
    }


}
