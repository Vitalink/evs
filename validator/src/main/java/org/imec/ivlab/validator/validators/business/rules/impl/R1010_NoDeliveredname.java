package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDDRUGCNK;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.ContentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.util.List;


public class R1010_NoDeliveredname extends BaseMSEntryRule {


    @Override
    public String getMessage() {
        return "The usage of delivered is not allowed for <medicinalproduct>, unless the content and 'S' attribute value of <intendedcd> and <deliveredcd> are exactly the same";
    }

    @Override
    public String getRuleId() {
        return "1010";
    }

    @Override
    public Level getLevel() {
        return Level.WARNING;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        ItemType medicationItem = TransactionUtil.getItem(msEntry.getMseTransaction(), CDITEMvalues.MEDICATION);

        if (!usesDeliveredFunctionalityCorrectly(medicationItem.getContents())) {
            return failRule(msEntry.getMseTransaction());
        }


        return passRule();
    }


    private boolean usesDeliveredFunctionalityCorrectly(List<ContentType> contentTypes) {

        if (contentTypes == null) {
            return true;
        }

        for (ContentType contentType : contentTypes) {

            if (contentType.getMedicinalproduct() != null) {

                if (!cnksOk(contentType.getMedicinalproduct().getIntendedcds(), contentType.getMedicinalproduct().getDeliveredcds())) {
                    return false;
                }

                if (contentType.getMedicinalproduct().getDeliveredname() != null && CollectionsUtil.emptyOrNull(contentType.getMedicinalproduct().getDeliveredcds())) {
                    return false;
                }

            }

        }

        return true;

    }

    private boolean cnksOk(List<CDDRUGCNK> intendCnks, List<CDDRUGCNK> deliveredCnks) {

        if (deliveredCnks == null) {
            return true;
        }

        for (CDDRUGCNK deliveredCnk : deliveredCnks) {
            if (!hasMatchingIntendendCnk(deliveredCnk, intendCnks)) {
                return false;
            }
        }

        return true;

    }

    private boolean hasMatchingIntendendCnk(CDDRUGCNK deliveredCnk, List<CDDRUGCNK> intendedCnks) {

        if (CollectionsUtil.emptyOrNull(intendedCnks)) {
            return false;
        }

        for (CDDRUGCNK intendedCnk : intendedCnks) {
            if (!StringUtils.equals(deliveredCnk.getValue(), intendedCnk.getValue())) {
                return false;
            }

            if (!StringUtils.equals(deliveredCnk.getValue(), intendedCnk.getValue()) || !StringUtils.equals(deliveredCnk.getS().value(), intendedCnk.getS().value())) {
                return false;
            }

        }

        return true;

    }


}
