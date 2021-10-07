package org.imec.ivlab.core.model.evsref.extractor.impl;

import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TextWithLayoutType;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.evsref.EVSREF;
import org.imec.ivlab.core.model.evsref.Identifiable;
import org.imec.ivlab.core.model.evsref.extractor.AbstractRefExtractor;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.List;

public class DiaryNoteEVSRefExtractor extends AbstractRefExtractor {

    @Override
    public void putEvsReference(Identifiable identifiable, EVSREF evsref) {

        List<TextType> textFields = TransactionUtil.getText(identifiable.getIdentifiableTransaction());

        if (CollectionsUtil.notEmptyOrNull(textFields)) {
            TextType textType = textFields.get(0);
            textType.setValue(StringUtils.joinWith("", textType.getValue(), evsref.getFormatted()));
            return;
        }

        List<TextWithLayoutType> textWithLayoutTypes = TransactionUtil.getTextWithLayout(identifiable.getIdentifiableTransaction());
        if (CollectionsUtil.notEmptyOrNull(textWithLayoutTypes)) {
            TextWithLayoutType textWithLayoutType;
            textWithLayoutType = textWithLayoutTypes.get(0);
            textWithLayoutType.getContent().add(evsref.getFormatted());
        } else {
            TextWithLayoutType textWithLayoutType = new TextWithLayoutType();
            textWithLayoutType.setL("nl");
            textWithLayoutType.getContent().add(evsref.getFormatted());
            identifiable.getIdentifiableTransaction().getHeadingsAndItemsAndTexts().add(textWithLayoutType);
        }

    }

}
