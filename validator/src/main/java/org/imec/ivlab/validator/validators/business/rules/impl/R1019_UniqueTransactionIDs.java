package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.util.IDKmehrUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class R1019_UniqueTransactionIDs extends BaseMSEntryRule {

    @Override
    public String getMessage() {
        return "Transaction ID (ID-KMEHR on level of transaction) must exist exactly once per transaction and be unique across the whole kmehr message";
    }

    @Override
    public String getRuleId() {
        return "1019";
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {
        
        Set<String> transactionIDs = new HashSet<String>();

        for (TransactionType transaction : msEntry.getAllTransactions()) {
            List<IDKMEHR> idKmehrs = IDKmehrUtil.getIDKmehrs(transaction.getIds(), IDKMEHRschemes.ID_KMEHR);
            if (CollectionsUtil.emptyOrNull(idKmehrs) || idKmehrs.size() > 1) {
                return failRule(transaction);
            }

            String idKmehrValue = idKmehrs.get(0).getValue();
            if (StringUtils.isBlank(idKmehrValue)) {
                return failRule(transaction);
            }

            if (transactionIDs.contains(idKmehrValue)) {
                return failRule(transaction);
            } else {
                transactionIDs.add(idKmehrValue);
            }
        }


        return passRule();
    }

}
