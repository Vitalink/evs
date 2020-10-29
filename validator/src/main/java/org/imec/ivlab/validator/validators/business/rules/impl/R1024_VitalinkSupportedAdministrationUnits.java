package org.imec.ivlab.validator.validators.business.rules.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.AdministrationquantityType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.imec.ivlab.core.kmehr.model.AdministrationUnit;
import org.imec.ivlab.core.kmehr.model.util.RegimenUtil;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.upload.msentrylist.MSEntry;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.validators.business.rules.BaseMSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.MSEntryRule;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.util.List;

public class R1024_VitalinkSupportedAdministrationUnits extends BaseMSEntryRule implements MSEntryRule {

    @Override
    public String getMessage() {
        return "Only administration units as described in the 'Vitalink Medicatieschema Vertaaltabellen' are allowed.";
    }

    @Override
    public String getRuleId() {
        return "1024";
    }

    @Override
    public Level getLevel() {
        return Level.WARNING;
    }

    @Override
    public RuleExecution performValidation(MSEntry msEntry)  {

        for (TransactionType transaction : msEntry.getAllTransactions()) {

            ItemType medicationItem = TransactionUtil.getItem(transaction, CDITEMvalues.MEDICATION);

            List<AdministrationquantityType> quantities = RegimenUtil.getQuantities(medicationItem.getRegimen());

            if (CollectionsUtil.emptyOrNull(quantities)) {
                return passRule();
            }

            for (AdministrationquantityType quantity : quantities) {

                if (quantity.getUnit() == null || quantity.getUnit().getCd() == null) {
                    continue;
                }

                String administrationUnitValue = quantity.getUnit().getCd().getValue();

                try {
                    AdministrationUnit administrationUnit = AdministrationUnit.fromValue(administrationUnitValue);
                    if (administrationUnit.isSupportedByVitalink()) {
                        continue;
                    } else {
                        return failRule(transaction);
                    }
                } catch (IllegalArgumentException e) {
                    // Checking for valid administration units according to the KMEHR standard will be done elsewhere.
                    // here we only check for the vaildity against Vitalink vertaaltabellen.
                    continue;
                }

            }


        }

        return passRule();
    }


}
