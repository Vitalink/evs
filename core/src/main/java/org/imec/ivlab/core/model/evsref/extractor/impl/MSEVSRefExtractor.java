package org.imec.ivlab.core.model.evsref.extractor.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.constants.CoreConstants;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.evsref.EVSREF;
import org.imec.ivlab.core.model.evsref.Identifiable;
import org.imec.ivlab.core.model.evsref.extractor.AbstractRefExtractor;

public class MSEVSRefExtractor extends AbstractRefExtractor {

    @Override
    public void putEvsReference(Identifiable identifiable, EVSREF evsref) {

        ItemType medicationItem = TransactionUtil.getItem(identifiable.getIdentifiableTransaction(), CDITEMvalues.MEDICATION);

        TextType instructionforpatient = medicationItem.getInstructionforpatient();
        if (instructionforpatient == null) {
            instructionforpatient = new TextType();
            instructionforpatient.setL(CoreConstants.KMEHR_LANGUAGE_L_ATTRIBUTE);
            instructionforpatient.setValue("");
        }

        instructionforpatient.setValue(StringUtils.joinWith("", instructionforpatient.getValue(), evsref.getFormatted()));

        medicationItem.setInstructionforpatient(instructionforpatient);

    }

}
