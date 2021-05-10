package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTEMPORALITYvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

public class R1017_AllowedValuesCdTemporality extends BaseMSEntryRule implements MSEntryRule {

    private static final CDTEMPORALITYvalues[] ALLOWED_TEMPORALITY_VALUES = new CDTEMPORALITYvalues[] {CDTEMPORALITYvalues.ACUTE, CDTEMPORALITYvalues.CHRONIC, CDTEMPORALITYvalues.ONESHOT};

    @Override
    public String getMessage() {
        return "CD-TEMPORALITY values must be one of the following: " + StringUtils.joinWith(", ", ALLOWED_TEMPORALITY_VALUES);
    }

    @Override
    public String getRuleId() {
        return "1017";
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        for (TransactionType transaction : msEntry.getAllTransactions()) {

            ItemType medicationItem = TransactionUtil.getItem(transaction, CDITEMvalues.MEDICATION);

            if (medicationItem.getTemporality() != null && medicationItem.getTemporality().getCd() != null) {

                if (!ArrayUtils.contains(ALLOWED_TEMPORALITY_VALUES, medicationItem.getTemporality().getCd().getValue())) {
                    return failRule(transaction);
                }

            }

        }

        return passRule();

    }


}
