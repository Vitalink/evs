package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMMSvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLNKvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.LnkType;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ContentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.PersonType;
import org.apache.commons.collections.CollectionUtils;
import org.imec.ivlab.core.exceptions.DataNotFoundException;
import org.imec.ivlab.core.exceptions.MultipleEntitiesFoundException;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {

    public static List<LnkType> getLinks(ItemType itemType, CDLNKvalues cdlnKvaluesFilter) {

        List<LnkType> links = new ArrayList<>();

        if (itemType.getLnks() == null) {
            return links;
        }

        for (LnkType linkType : itemType.getLnks()) {
            if (cdlnKvaluesFilter.equals(linkType.getTYPE())) {
                links.add(linkType);
            }
        }

        return links;
    }

    public static List<ItemType> getItems(List<ItemType> items, CDITEMMSvalues healthcareItemFilter) {

        List<ItemType> matchingItems = new ArrayList<>();

        ContentUtil contentUtil = new ContentUtil();

        if (CollectionUtils.isEmpty(items)) {
            throw new RuntimeException("Empty list of items provided");
        }

        for (ItemType item : items) {

            if (item.getContents() == null) {
                continue;
            }

            for (ContentType contentType : item.getContents()) {
                try {
                    CDCONTENT cdContent = ContentUtil.getCDContent(contentType, CDCONTENTschemes.CD_ITEM_MS);
                    if (cdContent.getValue().equals(healthcareItemFilter.value())) {
                        matchingItems.add(item);
                        break;
                    }
                } catch (DataNotFoundException notimportantException) {

                }
            }

        }

        return matchingItems;

    }

    public static ItemType getItem(List<ItemType> items, CDITEMMSvalues healthcareItemFilter) {

        List<ItemType> matchingItems = getItems(items, healthcareItemFilter);

        if (CollectionUtils.isEmpty(matchingItems)) {
            throw new DataNotFoundException("No item found with CD-ITEM-MS: " + healthcareItemFilter);
        }

        if (CollectionUtils.size(matchingItems) > 1) {
            throw new MultipleEntitiesFoundException("Multiple items found with CD-ITEM-MS: " + healthcareItemFilter);
        }

        return matchingItems.get(0);

    }

    public static List<ContentType> getTextContentTypes(ItemType itemType) {

        List<ContentType> textContentTypes = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(itemType.getContents())) {
            return null;
        }

        for (ContentType contentType : itemType.getContents()) {

            if (contentType.getTexts() != null) {
                textContentTypes.add(contentType);
            }

        }

        return textContentTypes;

    }

    public static List<TextType> collectContentTypeTextTypes(ItemType itemType) {

        List<TextType> textTypes = new ArrayList<>();

        List<ContentType> textContentTypes = getTextContentTypes(itemType);

        if (CollectionsUtil.emptyOrNull(textContentTypes)) {
            return null;
        }

        for (ContentType textContentType : textContentTypes) {
            textTypes.addAll(textContentType.getTexts());
        }

        return textTypes;

    }

    public static List<CDCONTENT> collectContentTypeCds(ItemType itemType) {
        if (CollectionsUtil.notEmptyOrNull(itemType.getContents())) {
            List<CDCONTENT> cdcontents = new ArrayList<>();
            for (ContentType contentType : itemType.getContents()) {
                if (contentType.getCds() != null) {
                    cdcontents.addAll(contentType.getCds());
                }
            }
            return cdcontents;
        }
        return null;
    }

    public static List<HcpartyType> collectHCPartyTypes(ItemType itemType) {
        if (CollectionsUtil.notEmptyOrNull(itemType.getContents())) {
            List<HcpartyType> hcpartyTypes = new ArrayList<>();
            for (ContentType contentType : itemType.getContents()) {
                if (contentType.getHcparty() != null) {
                    hcpartyTypes.add(contentType.getHcparty());
                }
            }
            return hcpartyTypes;
        }
        return null;
    }

    public static List<PersonType> collectPersonTypes(ItemType itemType) {
        if (CollectionsUtil.notEmptyOrNull(itemType.getContents())) {
            List<PersonType> personTypes = new ArrayList<>();
            for (ContentType contentType : itemType.getContents()) {
                if (contentType.getPerson() != null) {
                    personTypes.add(contentType.getPerson());
                }
            }
            return personTypes;
        }
        return null;
    }

}
