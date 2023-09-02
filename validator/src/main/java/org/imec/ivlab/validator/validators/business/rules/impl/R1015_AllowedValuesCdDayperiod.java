package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDDAYPERIODvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Daytime;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.util.RegimenUtil;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.util.List;

public class R1015_AllowedValuesCdDayperiod extends BaseMSEntryRule {

    private static final CDDAYPERIODvalues[] ALLOWED_DAYPERIOD_VALUES = new CDDAYPERIODvalues[] {CDDAYPERIODvalues.AFTERBREAKFAST, CDDAYPERIODvalues.AFTERDINNER, CDDAYPERIODvalues.AFTERLUNCH, CDDAYPERIODvalues.BEFOREBREAKFAST, CDDAYPERIODvalues.BEFOREDINNER, CDDAYPERIODvalues.BEFORELUNCH, CDDAYPERIODvalues.BETWEENBREAKFASTANDLUNCH, CDDAYPERIODvalues.BETWEENDINNERANDSLEEP, CDDAYPERIODvalues.BETWEENLUNCHANDDINNER, CDDAYPERIODvalues.MORNING, CDDAYPERIODvalues.THEHOUROFSLEEP, CDDAYPERIODvalues.DURINGBREAKFAST, CDDAYPERIODvalues.DURINGLUNCH, CDDAYPERIODvalues.DURINGDINNER};

    @Override
    public boolean enabled() {
        return false;
    }

    @Override
    public String getMessage() {
        return "CD-DAYPERIOD values must be one of the following: " + StringUtils.joinWith(", ", ALLOWED_DAYPERIOD_VALUES);
    }

    @Override
    public String getRuleId() {
        return "1015";
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        for (TransactionType transaction : msEntry.getAllTransactions()) {

            ItemType medicationItem = TransactionUtil.getItem(transaction, CDITEMvalues.MEDICATION);

            List<Daytime> daytimes = RegimenUtil.getDaytimes(medicationItem.getRegimen());

            if (daytimes != null) {

                for (Daytime daytime : daytimes) {
                    if (daytime.getDayperiod() != null && daytime.getDayperiod().getCd() != null) {

                        if (!ArrayUtils.contains(ALLOWED_DAYPERIOD_VALUES, daytime.getDayperiod().getCd().getValue())) {
                            return failRule(transaction);
                        }

                    }
                }

            }

        }

        return passRule();

    }


}
