package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.DurationType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.internal.mapper.medication.TimeUnit;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

public class R1021_DurationTimeUnitAllowedCodes extends BaseMSEntryRule {

    @Override
    public String getMessage() {
        return "The timeunit value in a duration is allowed to have only one of these codes: a, mo, wk, d";
    }

    @Override
    public String getRuleId() {
        return "1021";
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        for (TransactionType transaction : msEntry.getAllTransactions()) {

            ItemType suspensionMedicationItem = TransactionUtil.getItem(transaction, CDITEMvalues.MEDICATION);

            DurationType durationType = suspensionMedicationItem.getDuration();

            if (durationType != null) {

                if (durationType.getUnit() == null || durationType.getUnit().getCd() == null || durationType.getUnit().getCd().getValue() == null) {
                    return failRule(transaction);
                }

                TimeUnit timeUnit = TimeUnit.fromValue(durationType.getUnit().getCd().getValue());

                switch (timeUnit) {
                    case A:
                    case MO:
                    case WK:
                    case D:
                        break;
                    default:
                        return failRule(transaction);
                }

            }

        }

        return passRule();
    }


}
