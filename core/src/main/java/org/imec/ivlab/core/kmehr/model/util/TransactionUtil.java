package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEM;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.LnkType;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.HeadingType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TextWithLayoutType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.exceptions.DataNotFoundException;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransactionUtil {

    public static String getTransactionId(TransactionType transactionType) {
        if (transactionType == null) {
            return null;
        }
        List<IDKMEHR> idKmehrs = IDKmehrUtil.getIDKmehrs(transactionType.getIds(), IDKMEHRschemes.ID_KMEHR);
        if (CollectionsUtil.notEmptyOrNull(idKmehrs) && idKmehrs.get(0) != null) {
            return idKmehrs.get(0).getValue();
        } else {
            return null;
        }
    }

    public static List<ItemType> getItems(TransactionType transactionType) {

        return getTransactionFields(transactionType, ItemType.class);

    }

    public static List<HeadingType> getHeadings(TransactionType transactionType) {

        return getTransactionFields(transactionType, HeadingType.class);

    }

    public static List<TextType> getText(TransactionType transactionType) {

        return getTransactionFields(transactionType, TextType.class);

    }

    public static List<TextWithLayoutType> getTextWithLayout(TransactionType transactionType) {

        return getTransactionFields(transactionType, TextWithLayoutType.class);

    }

    public static List<LnkType> getLinksAndRemoveFromTransaction(TransactionType transactionType) {
        return getTransactionFieldsAndRemoveFromTransaction(transactionType, LnkType.class);
    }

    public static List<TextType> getTextAndRemoveFromTransaction(TransactionType transactionType) {

        return getTransactionFieldsAndRemoveFromTransaction(transactionType, TextType.class);

    }

    public static List<TextWithLayoutType> getTextWithLayoutAndRemoveFromTransaction(TransactionType transactionType) {

        return getTransactionFieldsAndRemoveFromTransaction(transactionType, TextWithLayoutType.class);

    }

    public static List<LnkType> getLinks(TransactionType firstTransaction) {
        return getTransactionFields(firstTransaction, LnkType.class);
    }

    public static List<ItemType> getItems(TransactionType transactionType, CDITEMvalues cdItemTypeFilter) {

        List<ItemType> allItemTypes = getItems(transactionType);
        List<ItemType> itemTypes = new ArrayList<>();

        Iterator<ItemType> itemTypeIterator = allItemTypes.iterator();

        while (itemTypeIterator.hasNext()) {

            ItemType itemType = itemTypeIterator.next();

            if (CollectionUtils.isEmpty(itemType.getCds())) {
                continue;
            }

            for (CDITEM cdItem : itemType.getCds()) {
                if (StringUtils.equalsIgnoreCase(cdItem.getValue(), cdItemTypeFilter.value())) {
                    itemTypes.add(itemType);
                    break;
                }
            }

        }

        if (itemTypes.size() == 0) {
            return null;
        }

        return itemTypes;

    }

    public static List<ItemType> getItemsAndRemoveFromTransaction(TransactionType transactionType, CDITEMvalues cdItemTypeFilter) {

        if (transactionType == null || CollectionsUtil.emptyOrNull(transactionType.getHeadingsAndItemsAndTexts())) {
            return null;
        }

        List<ItemType> itemTypesMatchingFilter = new ArrayList<>();

        Iterator<Serializable> itemTypeIterator = transactionType.getHeadingsAndItemsAndTexts().iterator();

        while (itemTypeIterator.hasNext()) {

            Serializable serializable = itemTypeIterator.next();

            if (serializable instanceof ItemType) {

                ItemType itemType = (ItemType) serializable;

                if (CollectionUtils.isEmpty(itemType.getCds())) {
                    continue;
                }

                for (CDITEM cdItem : itemType.getCds()) {
                    if (StringUtils.equalsIgnoreCase(cdItem.getValue(), cdItemTypeFilter.value())) {
                        itemTypesMatchingFilter.add(itemType);
                        itemTypeIterator.remove();
                        break;
                    }
                }

            }

        }

        if (itemTypesMatchingFilter.size() == 0) {
            return null;
        }

        return itemTypesMatchingFilter;

    }

    public static ItemType getItem(TransactionType transactionType, CDITEMvalues cdItemTypeFilter) {

        List<ItemType> items = getItems(transactionType, cdItemTypeFilter);

        if (CollectionUtils.isEmpty(items)) {
            throw new DataNotFoundException("No item found with CD-ITEM: " + cdItemTypeFilter);
        }

        return items.get(0);

    }

    private static <T> List<T> getTransactionFields(TransactionType transactionType, Class<T> objectType) {

        List<T> transactionFields = new ArrayList<>();

        if (transactionType == null || CollectionUtils.isEmpty(transactionType.getHeadingsAndItemsAndTexts())) {
            return null;
        }

        for (Object object : transactionType.getHeadingsAndItemsAndTexts()) {

            if (objectType.isInstance(object) ) {
                transactionFields.add((T) object);
            }

        }

        return transactionFields;

    }

    private static <T> List<T> getTransactionFieldsAndRemoveFromTransaction(TransactionType transactionType, Class<T> objectType) {

        if (transactionType == null || CollectionsUtil.emptyOrNull(transactionType.getHeadingsAndItemsAndTexts())) {
            return null;
        }

        List<T> matchingItems = new ArrayList<>();

        Iterator<Serializable> itemsIterator = transactionType.getHeadingsAndItemsAndTexts().iterator();

        while (itemsIterator.hasNext()) {

            Serializable serializable = itemsIterator.next();
            if (serializable.getClass().isAssignableFrom(objectType)) {
                T foundItem = (T) serializable;
                matchingItems.add(foundItem);
                itemsIterator.remove();
            }

        }

        return matchingItems;
    }


}
