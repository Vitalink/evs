package org.imec.ivlab.core.kmehr.model.util;

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

import java.util.List;
import java.util.stream.Collectors;

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

        return transactionType.getItem();

    }

    public static List<HeadingType> getHeadings(TransactionType transactionType) {

        return transactionType.getHeading();

    }

    public static List<TextType> getText(TransactionType transactionType) {

        return transactionType.getText();

    }

    public static List<TextWithLayoutType> getTextWithLayout(TransactionType transactionType) {

        return transactionType.getTextWithLayout();

    }

    public static List<LnkType> getLinks(TransactionType transactionType) {
        return transactionType.getLnk();
    }

    public static List<ItemType> getItems(TransactionType transactionType, CDITEMvalues cdItemTypeFilter) {

        return transactionType
            .getItem()
            .stream()
            .filter(item -> item.getCds().stream().anyMatch(cd -> StringUtils.equalsIgnoreCase(cd.getValue(), cdItemTypeFilter.value()) ) )
            .collect(Collectors.toList());
    }

    public static ItemType getItem(TransactionType transactionType, CDITEMvalues cdItemTypeFilter) {

        List<ItemType> items = getItems(transactionType, cdItemTypeFilter);

        if (CollectionUtils.isEmpty(items)) {
            throw new DataNotFoundException("No item found with CD-ITEM: " + cdItemTypeFilter);
        }

        return items.get(0);

    }

}
