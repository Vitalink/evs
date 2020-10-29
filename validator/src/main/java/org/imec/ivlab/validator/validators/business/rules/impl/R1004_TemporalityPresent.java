package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

public class R1004_TemporalityPresent extends BaseMSEntryRule implements MSEntryRule {

    @Override
    public String getMessage() {
        return "Temporality must be specified";
    }

    @Override
    public String getRuleId() {
        return "1004";
    }

    @Override
    public Level getLevel() {
        return Level.WARNING;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        ItemType medicationItem = TransactionUtil.getItem(msEntry.getMseTransaction(), CDITEMvalues.MEDICATION);

        if (medicationItem.getTemporality() == null || medicationItem.getTemporality().getCd() == null || medicationItem.getTemporality().getCd().getValue() == null) {
            return failRule(msEntry.getMseTransaction());
        }

        return passRule();
    }


}
