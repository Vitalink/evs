package org.imec.ivlab.core.model.internal.parser.childprevention.mapper;

import static org.imec.ivlab.core.kmehr.model.util.TransactionUtil.getItems;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ContentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.util.ContentUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.internal.parser.childprevention.BooleanItem;
import org.imec.ivlab.core.model.internal.parser.childprevention.ChildPrevention;
import org.imec.ivlab.core.model.internal.parser.childprevention.DurationItem;
import org.imec.ivlab.core.model.internal.parser.childprevention.TextItem;
import org.imec.ivlab.core.model.internal.parser.common.BaseMapper;
import org.imec.ivlab.core.util.CollectionsUtil;


public class ChildPreventionMapper extends BaseMapper {

    public static ChildPrevention kmehrToChildPrevention(Kmehrmessage kmehrmessage) {

        ChildPrevention entry = new ChildPrevention();

        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessage);
        if (folderType == null || CollectionsUtil.emptyOrNull(folderType.getTransactions())) {
            throw new RuntimeException("No transactions provided in folder, cannot start mapping");
        }

        Kmehrmessage cloneKmehr = SerializationUtils.clone(kmehrmessage);
        FolderType cloneFolder = KmehrMessageUtil.getFolderType(cloneKmehr);
        TransactionType firstTransaction = cloneFolder.getTransactions().get(0);

        markKmehrLevelFieldsAsProcessed(cloneKmehr);
        entry.setHeader(mapHeaderFields(cloneKmehr.getHeader()));
        markKmehrHeaderLevelFieldsAsProcessed(cloneKmehr.getHeader());

        List<ItemType> healthCareElementItems = getItems(firstTransaction, CDITEMvalues.HEALTHCAREELEMENT);

        getItemByName(healthCareElementItems, "refusal_hearing_screening").ifPresent(itemType -> entry.setRefusalHearingScreening(toBooleanContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "pregnancy_CMV_infection").ifPresent(itemType -> entry.setPregnancyCMVInfection(toBooleanContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "bacterial_meningitis").ifPresent(itemType -> entry.setBacterialMeningitis(toBooleanContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "severe_head_trauma").ifPresent(itemType -> entry.setSevereHeadTrauma(toBooleanContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "pregnancy_duration_in_weeks").ifPresent(itemType -> entry.setPregnancyDuration(toDurationContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "result_neonatal_hearing_screening_left").ifPresent(itemType -> entry.setResultHearingScreeningLeft(toTextContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "result_neonatal_hearing_screening_right").ifPresent(itemType -> entry.setResultHearingScreeningRight(toTextContentAndRemove(itemType)));

        entry.getTransactionCommon().setPerson(toPatient(folderType.getPatient()));

        markFolderLevelFieldsAsProcessed(cloneFolder);

        entry.getTransactionCommon().setDate(firstTransaction.getDate().toLocalDate());
        entry.getTransactionCommon().setTime(firstTransaction.getTime());
        if (firstTransaction.getRecorddatetime() != null) {
            entry.getTransactionCommon().setRecordDateTime(firstTransaction.getRecorddatetime().toLocalDateTime());
        }
        entry.getTransactionCommon().setAuthor(mapHcPartyFields(firstTransaction.getAuthor()));
        entry.getTransactionCommon().setRedactor(mapHcPartyFields(firstTransaction.getRedactor()));
        entry.getTransactionCommon().setCdtransactions(new ArrayList<>(firstTransaction.getCds()));

        entry.setChildPreventionFile(firstTransaction.getLnk().stream().findFirst().orElse(null));
        //entry.setChildPreventionFile(getLinksAndRemoveFromTransaction(firstTransaction).stream().findFirst().orElse(null));

        markTransactionAsProcessed(firstTransaction);

        entry.setUnparsed(cloneKmehr);

        return entry;
    }

    private static TextItem toTextContentAndRemove(ItemType itemType) {
        Optional<ContentType> maybeContentType = itemType
            .getContents()
            .stream()
            .findFirst();
        if (maybeContentType.isPresent()) {
            ContentType contentType = maybeContentType.get();
            itemType.getContents().remove(contentType);

            Optional<TextType> maybeTextType = contentType
                .getTexts()
                .stream()
                .findFirst();

            TextItem textContent;
            if (maybeTextType.isPresent()) {
                textContent = new TextItem(maybeTextType.get().getValue());
                contentType.getTexts().remove(maybeTextType.get());
            } else {
                textContent = new TextItem(null);
            }

            textContent.setUnparsed(contentType);
            return textContent;

        } else {
            return null;
        }
    }

    private static DurationItem toDurationContentAndRemove(ItemType itemType) {
        Optional<ContentType> maybeContentType = itemType
            .getContents()
            .stream()
            .findFirst();
        if (maybeContentType.isPresent()) {
            ContentType contentType = maybeContentType.get();
            itemType.getContents().remove(contentType);
            DurationItem pregnancyDurationContent = new DurationItem(contentType
                .getUnsignedInt(), contentType
                .getUnit());
            contentType.setUnit(null);
            contentType.setUnsignedInt(null);
            pregnancyDurationContent.setUnparsed(contentType);
            return pregnancyDurationContent;
        } else {
            return null;
        }
    }

    private static BooleanItem toBooleanContentAndRemove(ItemType itemType) {
        BooleanItem booleanItem = new BooleanItem(null);

        Optional<ContentType> maybeContentType = itemType
            .getContents()
            .stream()
            .findFirst();

        if (maybeContentType.isPresent()) {
            ContentType contentType = maybeContentType.get();
            itemType.getContents().remove(contentType);
            booleanItem = new BooleanItem(contentType.isBoolean());
            contentType.setBoolean(null);
            booleanItem.setUnparsed(contentType);
        }

        return booleanItem;

    }

    private static Optional<ItemType> getItemByName(List<ItemType> itemTypes, String contentCdValue) {
        Optional<ItemType> maybeItem = Optional.ofNullable(itemTypes).orElse(Collections.emptyList())
            .stream()
            .filter(hasMatchingContent(contentCdValue))
            .findFirst();

        maybeItem.ifPresent(itemType -> {
            removeContentByCdValue(itemType, contentCdValue);
            clearCds(itemType);
            clearIds(itemType);
        });

        return maybeItem;

    }

    private static void removeContentByCdValue(ItemType itemType, String contentCdValue) {
        itemType
            .getContents()
            .removeIf(hasLocalContentsWithCdValue(contentCdValue));
    }

    private static Predicate<ContentType> hasLocalContentsWithCdValue(String contentCdValue) {
        return contentType -> ContentUtil
            .getCDContents(contentType, CDCONTENTschemes.LOCAL)
            .stream()
            .anyMatch(hasCdWithValue(contentCdValue));
    }

    private static Predicate<ItemType> hasMatchingContent(String contentCdValue) {
        return itemType -> itemType
            .getContents()
            .stream()
            .flatMap(cdcontent -> ContentUtil
                .getCDContents(cdcontent, CDCONTENTschemes.LOCAL)
                .stream())
            .anyMatch(hasCdWithValue(contentCdValue));
    }

    private static Predicate<CDCONTENT> hasCdWithValue(String contentCdValue) {
        return cdcontent -> StringUtils.equals(cdcontent.getValue(), contentCdValue);
    }

}
