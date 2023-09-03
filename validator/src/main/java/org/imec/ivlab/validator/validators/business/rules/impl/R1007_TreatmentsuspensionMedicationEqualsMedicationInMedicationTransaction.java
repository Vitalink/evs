package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.ContentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.JAXBUtils;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import javax.xml.bind.JAXBException;
import java.util.List;


public class R1007_TreatmentsuspensionMedicationEqualsMedicationInMedicationTransaction extends BaseMSEntryRule {


    @Override
    public String getMessage() {
        return "The medication specified in a treatment suspension must be equal to the medication specified in the corresponding medication scheme element";
    }

    @Override
    public String getRuleId() {
        return "1007";
    }

    @Override
    public Level getLevel() {
        return Level.WARNING;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        if (CollectionsUtil.emptyOrNull(msEntry.getTsTransactions())) {
            return passRule();
        }

        ItemType medicationItem = TransactionUtil.getItem(msEntry.getMseTransaction(), CDITEMvalues.MEDICATION);

        for (TransactionType treatmentSuspensionTransaction : msEntry.getTsTransactions()) {

            ItemType suspensionMedicationItem = TransactionUtil.getItem(treatmentSuspensionTransaction, CDITEMvalues.MEDICATION);

            if (!contentTypeListsEqual(medicationItem.getContents(), suspensionMedicationItem.getContents())) {
                return failRule(treatmentSuspensionTransaction);
            }

        }

        return passRule();
    }

    private boolean contentTypeListsEqual(List<ContentType> contentTypesA, List<ContentType> contentTypesB) {

        if ((contentTypesA == null && contentTypesB != null) || (contentTypesA != null && contentTypesB == null)) {
            return false;
        }

        if ((contentTypesA == null)) {
            return true;
        }

        if (CollectionUtils.size(contentTypesA) != CollectionUtils.size(contentTypesB)) {
            return false;
        }

        for (int i = 0; i < contentTypesA.size(); i++) {

            ContentType contentTypeA = contentTypesA.get(i);
            @SuppressWarnings("null")
            ContentType contentTypeB = contentTypesB.get(i);

            try {
                String medicationContentTypeA = JAXBUtils.marshal(contentTypeA, "");
                String medicationContentTypeB = JAXBUtils.marshal(contentTypeB, "");

                if (!StringUtils.equalsIgnoreCase(medicationContentTypeA, medicationContentTypeB)) {
                    return false;
                }

            } catch (JAXBException e) {
                throw new RuntimeException("Failed to marshal content type object", e);
            }

        }

        return true;

    }

}
