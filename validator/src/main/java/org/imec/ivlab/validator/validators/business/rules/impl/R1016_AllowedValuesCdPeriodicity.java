package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.FrequencyCode;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

public class R1016_AllowedValuesCdPeriodicity extends BaseMSEntryRule implements MSEntryRule {

    private static final FrequencyCode[] ALLOWED_PERIODICITY_VALUES = new FrequencyCode[] {FrequencyCode.UH, FrequencyCode.U, FrequencyCode.UT, FrequencyCode.UD, FrequencyCode.UV, FrequencyCode.UZ, FrequencyCode.UA, FrequencyCode.UW, FrequencyCode.D, FrequencyCode.O1, FrequencyCode.DT, FrequencyCode.DD, FrequencyCode.DV, FrequencyCode.DQ, FrequencyCode.DZ, FrequencyCode.DA, FrequencyCode.DN, FrequencyCode.DX, FrequencyCode.DE, FrequencyCode.DW, FrequencyCode.W, FrequencyCode.WT, FrequencyCode.WD, FrequencyCode.WV, FrequencyCode.WQ, FrequencyCode.WZ, FrequencyCode.WS, FrequencyCode.WA, FrequencyCode.WN, FrequencyCode.WX, FrequencyCode.WE, FrequencyCode.WW, FrequencyCode.WP, FrequencyCode.M, FrequencyCode.MT, FrequencyCode.MD, FrequencyCode.MV, FrequencyCode.MQ, FrequencyCode.MZ2, FrequencyCode.MS, FrequencyCode.MA, FrequencyCode.MN, FrequencyCode.MX, FrequencyCode.ME, FrequencyCode.MC, FrequencyCode.JH2, FrequencyCode.J, FrequencyCode.JT, FrequencyCode.JD, FrequencyCode.JV, FrequencyCode.JQ, FrequencyCode.JZ};

    @Override
    public String getMessage() {
        return "CD-PERIODICITY values must be one of the following: " + StringUtils.joinWith(", ", ALLOWED_PERIODICITY_VALUES);
    }

    @Override
    public String getRuleId() {
        return "1016";
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        for (TransactionType transaction : msEntry.getAllTransactions()) {

            ItemType medicationItem = TransactionUtil.getItem(transaction, CDITEMvalues.MEDICATION);

            if (medicationItem.getFrequency() != null && medicationItem.getFrequency().getPeriodicity() != null && medicationItem.getFrequency().getPeriodicity().getCd() != null) {
                FrequencyCode frequencyCode = FrequencyCode.fromValue(medicationItem.getFrequency().getPeriodicity().getCd().getValue());
                if (!ArrayUtils.contains(ALLOWED_PERIODICITY_VALUES, frequencyCode)) {
                    return failRule(transaction);
                }
            }

        }

        return passRule();

    }


}
