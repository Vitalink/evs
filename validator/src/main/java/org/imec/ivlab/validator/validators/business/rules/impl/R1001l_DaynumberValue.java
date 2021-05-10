package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDPERIODICITY;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import org.imec.ivlab.core.kmehr.model.util.FrequencyUtil;
import org.imec.ivlab.core.kmehr.model.util.RegimenUtil;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.math.BigInteger;
import java.util.List;

public class R1001l_DaynumberValue extends BaseMSEntryRule implements MSEntryRule {

    @Override
    public String getMessage() {
        return "Daynumber should be bigger than 0";
    }

    @Override
    public String getRuleId() {
        return "1001l";
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry) {

        ItemType medicationItem = TransactionUtil.getItem(msEntry.getMseTransaction(), CDITEMvalues.MEDICATION);

        List<BigInteger> dayNumbers = RegimenUtil.getDayNumbers(medicationItem.getRegimen());

        CDPERIODICITY dayPeriod = FrequencyUtil.getDayPeriod(medicationItem.getFrequency());

        if (dayPeriod != null && dayNumbers != null) {

            for (BigInteger dayNumber : dayNumbers) {

                if (dayNumber.compareTo(BigInteger.ZERO) < 1) {
                    return failRule(msEntry.getMseTransaction());
                }

            }

        }

        return passRule();
    }


}
