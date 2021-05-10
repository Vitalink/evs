package org.imec.ivlab.core.model.evsref.extractor.impl;

import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.evsref.EVSREF;
import org.imec.ivlab.core.model.evsref.Identifiable;
import org.imec.ivlab.core.model.evsref.extractor.AbstractRefExtractor;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.List;

import static org.imec.ivlab.core.constants.CoreConstants.KMEHR_LANGUAGE_L_ATTRIBUTE;

public class SumehrEVSRefExtractor extends AbstractRefExtractor {

    @Override
    public void putEvsReference(Identifiable identifiable, EVSREF evsref) {

        TextType textType;
        List<TextType> textFields = TransactionUtil.getText(identifiable.getIdentifiableTransaction());
        if (CollectionsUtil.notEmptyOrNull(textFields)) {
            textType = textFields.get(0);
            textType.setValue(StringUtils.joinWith("", textType.getValue(), evsref.getFormatted()));
        } else {
            textType = new TextType();
            textType.setValue(evsref.getFormatted());
            textType.setL(KMEHR_LANGUAGE_L_ATTRIBUTE);
            identifiable.getIdentifiableTransaction().getHeadingsAndItemsAndTexts().add(textType);
        }

    }

}
