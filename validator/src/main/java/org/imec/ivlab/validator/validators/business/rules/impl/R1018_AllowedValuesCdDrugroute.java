package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.internal.mapper.medication.Route;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

public class R1018_AllowedValuesCdDrugroute extends BaseMSEntryRule implements MSEntryRule {


    @Override
    public String getMessage() {
        return "CD-DRUGROUTE value is not supported";
    }

    @Override
    public String getRuleId() {
        return "1018";
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        for (TransactionType transaction : msEntry.getAllTransactions()) {

            ItemType medicationItem = TransactionUtil.getItem(transaction, CDITEMvalues.MEDICATION);

            if (medicationItem.getRoute() != null && medicationItem.getRoute().getCd() != null) {

                try {
                    Route route = Route.fromCode(medicationItem.getRoute().getCd().getValue());
                    if (!route.isSupportedByVitalink()) {
                        return failRule(transaction);
                    }
                } catch (RuntimeException e) {
                    return failRule(transaction);
                }

            }

        }

        return passRule();

    }


}
