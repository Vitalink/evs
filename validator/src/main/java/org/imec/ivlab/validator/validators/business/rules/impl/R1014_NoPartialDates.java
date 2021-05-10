package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.ContentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.MomentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.imec.ivlab.core.kmehr.model.util.FolderUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.validator.validators.business.rules.BaseKmehrMessageRule;
import org.imec.ivlab.validator.validators.business.rules.KmehrMessageRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.util.List;

public class R1014_NoPartialDates extends BaseKmehrMessageRule implements KmehrMessageRule {

    private static final int MAX_LENGTH_FREE_TEXT = 340;

    @Override
    public String getMessage() {
        return "Partial dates (yearmonth and year) are not allowed. Use Date instead";
    }

    @Override
    public String getRuleId() {
        return "1014";
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }

    @Override
    public RuleExecution performValidation(Kmehrmessage kmehrmessage)  {

        List<TransactionType> transactions = FolderUtil.getTransactions(KmehrMessageUtil.getFolderType(kmehrmessage), CDTRANSACTIONvalues.MEDICATIONSCHEMEELEMENT);
        transactions.addAll(FolderUtil.getTransactions(KmehrMessageUtil.getFolderType(kmehrmessage), CDTRANSACTIONvalues.TREATMENTSUSPENSION));

        if (KmehrMessageUtil.getFolderType(kmehrmessage).getPatient() != null) {
            if (usesPartialDate(KmehrMessageUtil.getFolderType(kmehrmessage).getPatient().getBirthdate())) {
                return failRule();
            }

            if (usesPartialDate(KmehrMessageUtil.getFolderType(kmehrmessage).getPatient().getDeathdate())) {
                return failRule();
            }
        }

        for (TransactionType transaction : transactions) {

            List<ItemType> items = TransactionUtil.getItems(transaction);

            if (items == null) {
                continue;
            }

            for (ItemType item : items) {

                if (usesPartialDate(item.getBeginmoment())) {
                    return failRule(transaction);
                }

                if (usesPartialDate(item.getEndmoment())) {
                    return failRule(transaction);
                }

                if (item.getContents() != null) {
                    for (ContentType contentType : item.getContents()) {
                        if (contentType.getPerson() != null) {
                            if (contentType.getPerson().getBirthdate() != null) {
                                return failRule(transaction);
                            }

                            if (contentType.getPerson().getDeathdate() != null) {
                                return failRule(transaction);
                            }
                        }
                    }

                }

            }

        }

        return passRule();

    }

    private boolean usesPartialDate(MomentType momentType) {

        if (momentType == null) {
            return false;
        }

        if (momentType.getYearmonth() != null || momentType.getYear() != null) {
            return true;
        }

        return false;

    }

}
