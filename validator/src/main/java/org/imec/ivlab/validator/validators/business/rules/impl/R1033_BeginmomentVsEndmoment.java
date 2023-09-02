package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import org.imec.ivlab.core.RangeChecker;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.internal.mapper.medication.mapper.MedicationMapper;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.util.DateUtils;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import org.joda.time.LocalDate;


public class R1033_BeginmomentVsEndmoment extends BaseMSEntryRule implements MSEntryRule {

    @Override
    public String getMessage() {
        return "The beginmoment of a transaction MSE should not come after the (calculated) endmoment";
    }

    @Override
    public String getRuleId() {
        return "1033";
    }

    @Override
    public Level getLevel() {
        return Level.WARNING;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        ItemType medicationItem = TransactionUtil.getItem(msEntry.getMseTransaction(), CDITEMvalues.MEDICATION);


        if (medicationItem.getBeginmoment() == null || medicationItem.getBeginmoment().getDate() == null) {
            return passRule();
        }
        LocalDate medicationBeginMomentLocalDate = medicationItem.getBeginmoment().getDate().toLocalDate();

        LocalDate medicationEndMomentLocalDate = null;
        if (medicationItem.getEndmoment() != null && medicationItem.getEndmoment().getDate() != null) {
            medicationEndMomentLocalDate = medicationItem.getEndmoment().getDate().toLocalDate();
        }

        RangeChecker rangeChecker = new RangeChecker();
        LocalDate calculatedEndDate = rangeChecker.calculateEndDate(medicationBeginMomentLocalDate, medicationEndMomentLocalDate, MedicationMapper.convertDuration(medicationItem.getDuration()));

        if (calculatedEndDate == null) {
            return passRule();
        }

        if (calculatedEndDate.isBefore(medicationBeginMomentLocalDate)) {
            return failRule(msEntry.getMseTransaction());
        }

        return passRule();

    }


}
