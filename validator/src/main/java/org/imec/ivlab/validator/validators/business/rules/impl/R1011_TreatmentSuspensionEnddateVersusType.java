package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.DurationType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.LifecycleType;
import be.fgov.ehealth.standards.kmehr.schema.v1.MomentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

public class R1011_TreatmentSuspensionEnddateVersusType extends BaseMSEntryRule {

    @Override
    public String getMessage() {
        return "The combination of 'STOPPED with endmoment' or 'SUSPENDED without endmoment' can cause different interpretations";
    }

    @Override
    public String getRuleId() {
        return "1011";
    }

    @Override
    public Level getLevel() {
        return Level.WARNING;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        for (TransactionType treatmentSuspensionTransaction : msEntry.getTsTransactions()) {

            ItemType suspensionMedicationItem = TransactionUtil.getItem(treatmentSuspensionTransaction, CDITEMvalues.MEDICATION);

            LifecycleType lifecycle = suspensionMedicationItem.getLifecycle();

            MomentType endMomentType = suspensionMedicationItem.getEndmoment();
            DurationType durationType = suspensionMedicationItem.getDuration();

            if (lifecycle != null && lifecycle.getCd() != null) {

                if (lifecycle.getCd().getValue().equals(CDLIFECYCLEvalues.SUSPENDED)) {
                    if ((endMomentType == null || endMomentType.getDate() == null) && durationType == null) {
                        return failRule(msEntry.getMseTransaction());
                    }
                } else if (lifecycle.getCd().getValue().equals(CDLIFECYCLEvalues.STOPPED)) {
                    if (endMomentType != null || durationType != null) {
                        return failRule(msEntry.getMseTransaction());
                    }
                }

            }

        }

        return passRule();
    }


}
