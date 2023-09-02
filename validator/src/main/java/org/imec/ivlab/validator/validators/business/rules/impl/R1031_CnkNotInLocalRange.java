package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDDRUGCNK;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDINNCLUSTER;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.ContentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.MedicinalProductType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Substanceproduct;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.imec.ivlab.core.cnk.CnkValidator;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.util.ArrayList;
import java.util.List;


public class R1031_CnkNotInLocalRange extends BaseMSEntryRule implements MSEntryRule {


    @Override
    public String getMessage() {
        return "A used CNK / INNCLUSTER code is in the range 0900-000 -> 0999-999";
    }

    @Override
    public String getRuleId() {
        return "1031";
    }

    @Override
    public Level getLevel() {
        return Level.WARNING;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {


        for (TransactionType transactionType : msEntry.getAllTransactions()) {

            ArrayList<CDDRUGCNK> cnkCodes = new ArrayList<>();
            ArrayList<CDINNCLUSTER> innClusterCodes = new ArrayList<>();

            List<ItemType> items = TransactionUtil.getItems(transactionType, CDITEMvalues.MEDICATION);
            if (CollectionsUtil.emptyOrNull(items)) {
                continue;
            }

            for (ItemType item : items) {

                if (CollectionsUtil.emptyOrNull(item.getContents())) {
                    continue;
                }

                for (ContentType contentType : item.getContents()) {
                    MedicinalProductType medicinalproduct = contentType.getMedicinalproduct();
                    if (medicinalproduct != null && medicinalproduct.getIntendedcds() != null) {
                        cnkCodes.addAll(medicinalproduct.getIntendedcds());
                    }
                    if (medicinalproduct != null && medicinalproduct.getDeliveredcds() != null) {
                        cnkCodes.addAll(medicinalproduct.getDeliveredcds());
                    }
                    Substanceproduct substanceproduct = contentType.getSubstanceproduct();

                    if (substanceproduct != null && substanceproduct.getIntendedcd() != null) {
                        innClusterCodes.add(substanceproduct.getIntendedcd());
                    }
                    if (substanceproduct != null && substanceproduct.getDeliveredcd() != null) {
                        cnkCodes.add(substanceproduct.getDeliveredcd());
                    }
                }

            }

            if (isAnyReservedDrugCode(cnkCodes) || isAnyReservedClusterCode(innClusterCodes)) {
                return failRule(transactionType);
            }

        }

        return passRule();
    }

    private boolean isAnyReservedDrugCode(List<CDDRUGCNK> cddrugcnks) {

        if (CollectionsUtil.emptyOrNull(cddrugcnks)) {
            return false;
        }

        for (CDDRUGCNK cddrugcnk : cddrugcnks) {
            if (CnkValidator.isReservedForLocalUsage(cddrugcnk.getValue())) {
                return true;
            }
        }

        return false;

    }

    private boolean isAnyReservedClusterCode(List<CDINNCLUSTER> cdInnclusterCodes) {

        if (CollectionsUtil.emptyOrNull(cdInnclusterCodes)) {
            return false;
        }

        for (CDINNCLUSTER cddInnClusterCode : cdInnclusterCodes) {
            if (CnkValidator.isReservedForLocalUsage(cddInnClusterCode.getValue())) {
                return true;
            }
        }

        return false;

    }


}
