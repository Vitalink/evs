package org.imec.ivlab.core.model.evsref.extractor.impl;

import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TextWithLayoutType;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.evsref.EVSREF;
import org.imec.ivlab.core.model.evsref.Identifiable;
import org.imec.ivlab.core.model.evsref.extractor.AbstractRefExtractor;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.w3c.dom.Text;

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
            TextWithLayoutType textWithLayoutType = textWithLayoutTypes.get(0);

            if (hasNonStrikeContent(textWithLayoutType)) {
                appendReferenceToContent(evsref, textWithLayoutType);
            } else {
                Optional<ElementNSImpl> strikeTroughContent = findStrikeContent(textWithLayoutType);
                if (strikeTroughContent.isPresent()) {
                    appendReferenceToElement(evsref, strikeTroughContent);
                } else {
                    appendReferenceToContent(evsref, textWithLayoutType);
                }
            }

        } else {
            identifiable.getIdentifiableTransaction().getHeadingsAndItemsAndTexts().add(createNewTextWithLayout(evsref));
        }

    }

    private void appendReferenceToElement(EVSREF evsref, Optional<ElementNSImpl> strikeTroughContent) {
        Text textNode = strikeTroughContent
            .get()
            .getOwnerDocument()
            .createTextNode(evsref.getFormatted());
        strikeTroughContent
            .get().appendChild(textNode);
    }

    private boolean hasNonStrikeContent(TextWithLayoutType textWithLayoutType) {
        return textWithLayoutType
            .getContent()
            .stream()
            .filter(content -> !parseStrikeElement(content).isPresent())
            .filter(Objects::nonNull)
            .anyMatch(content -> !(StringUtils.trimToNull(content.toString()) == null));
    }

    private Optional<ElementNSImpl> findStrikeContent(TextWithLayoutType textWithLayoutType) {
        return textWithLayoutType
            .getContent()
            .stream()
            .map(this::parseStrikeElement)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }

    private Optional<ElementNSImpl> parseStrikeElement(Object content) {
        if (content instanceof ElementNSImpl) {
            ElementNSImpl element = (ElementNSImpl) content;
            if (StringUtils.equalsIgnoreCase(element.getLocalName(), "strike")) {
                return Optional.of(element);
            }
        }
        return Optional.empty();
    }

    private void appendReferenceToContent(EVSREF evsref, TextWithLayoutType textWithLayoutType) {
        textWithLayoutType
            .getContent()
            .add(evsref.getFormatted());
    }

    private TextWithLayoutType createNewTextWithLayout(EVSREF evsref) {
        TextWithLayoutType textWithLayoutType = new TextWithLayoutType();
        textWithLayoutType.setL("nl");
        appendReferenceToContent(evsref, textWithLayoutType);
        return textWithLayoutType;
    }

}
