package org.imec.ivlab.core.model.internal.parser.populationbasedscreening.mapper;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.model.util.ContentUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.internal.parser.childprevention.BooleanItem;
import org.imec.ivlab.core.model.internal.parser.childprevention.DateItem;
import org.imec.ivlab.core.model.internal.parser.childprevention.TextItem;
import org.imec.ivlab.core.model.internal.parser.childprevention.YearItem;
import org.imec.ivlab.core.model.internal.parser.common.BaseMapper;
import org.imec.ivlab.core.model.internal.parser.populationbasedscreening.PopulationBasedScreening;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.DateUtils;


public class PopulationBasedScreeningMapper extends BaseMapper {

    public static PopulationBasedScreening kmehrToPopulationBasedScreening(Kmehrmessage kmehrmessage) {

        PopulationBasedScreening entry = new PopulationBasedScreening();

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

        getItemByName(healthCareElementItems, "screening_type").ifPresent(itemType -> entry.setScreeningType(fromCDContentToTextContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "screening_year").ifPresent(itemType -> entry.setScreeningYear(toYearContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "invitation_type").ifPresent(itemType -> entry.setInvitationType(fromTextToTextContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "invitation_date").ifPresent(itemType -> entry.setInvitationDate(toDateContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "invitation_location_name").ifPresent(itemType -> entry.setInvitationLocationName(fromTextToTextContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "invitation_location_address").ifPresent(itemType -> entry.setInvitationLocationAddress(fromTextToTextContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "participation_date").ifPresent(itemType -> entry.setParticipationDate(toDateContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "participation_location_name").ifPresent(itemType -> entry.setParticipationLocationName(fromTextToTextContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "participation_location_address").ifPresent(itemType -> entry.setParticipationLocationAddress(fromTextToTextContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "participation_result").ifPresent(itemType -> entry.setParticipationResult(fromTextToTextContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "followup_needed").ifPresent(itemType -> entry.setFollowupNeeded(toBooleanContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "followup_advice").ifPresent(itemType -> entry.setFollowupAdvice(fromTextToTextContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "followup_approved").ifPresent(itemType -> entry.setFollowupApproved(toBooleanContentAndRemove(itemType)));
        getItemByName(healthCareElementItems, "next_invitation_indication").ifPresent(itemType -> entry.setNextInvitationIndication(fromTextToTextContentAndRemove(itemType)));

        entry.getTransactionCommon().setPerson(toPatient(folderType.getPatient()));

        markFolderLevelFieldsAsProcessed(cloneFolder);

        entry.getTransactionCommon().setDate(DateUtils.toLocalDate(firstTransaction.getDate()));
        entry.getTransactionCommon().setTime(DateUtils.toLocalTime(firstTransaction.getTime()));
        entry.getTransactionCommon().setRecordDateTime(DateUtils.toLocalDateTime(firstTransaction.getRecorddatetime()));
        entry.getTransactionCommon().setAuthor(mapHcPartyFields(firstTransaction.getAuthor()));
        entry.getTransactionCommon().setRedactor(mapHcPartyFields(firstTransaction.getRedactor()));
        entry.getTransactionCommon().setIdkmehrs(new ArrayList<>(firstTransaction.getIds()));
        entry.getTransactionCommon().setCdtransactions(new ArrayList<>(firstTransaction.getCds()));

        markTransactionAsProcessed(firstTransaction);

        entry.setUnparsed(cloneKmehr);

        return entry;
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

    private static TextItem fromTextToTextContentAndRemove(ItemType itemType) {
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

    private static TextItem fromCDContentToTextContentAndRemove(ItemType itemType) {
        Optional<ContentType> maybeContentType = itemType
            .getContents()
            .stream()
            .findFirst();
        if (maybeContentType.isPresent()) {
            ContentType contentType = maybeContentType.get();
            itemType.getContents().remove(contentType);

            Optional<CDCONTENT> maybeCdContent = contentType
                .getCds()
                .stream()
                .findFirst();

            TextItem textContent;
            if (maybeCdContent.isPresent()) {
                textContent = new TextItem(maybeCdContent.get().getValue());
                contentType.getCds().remove(maybeCdContent.get());
            } else {
                textContent = new TextItem(null);
            }

            textContent.setUnparsed(contentType);
            return textContent;

        } else {
            return null;
        }
    }

    private static YearItem toYearContentAndRemove(ItemType itemType) {
        Optional<ContentType> maybeContentType = itemType
            .getContents()
            .stream()
            .findFirst();

        if (maybeContentType.isPresent()) {
            ContentType contentType = maybeContentType.get();
            itemType.getContents().remove(contentType);

            Integer year = Optional.ofNullable(contentType.getYear()).map(XMLGregorianCalendar::getYear).orElse(null);

            YearItem yearItem;
            if (year != null) {
                yearItem = new YearItem(year);
                contentType.setYear(null);
            } else {
                yearItem = new YearItem(null);
            }

            yearItem.setUnparsed(contentType);
            return yearItem;

        } else {
            return null;
        }
    }
    private static DateItem toDateContentAndRemove(ItemType itemType) {
        Optional<ContentType> maybeContentType = itemType
            .getContents()
            .stream()
            .findFirst();

        if (maybeContentType.isPresent()) {
            ContentType contentType = maybeContentType.get();
            itemType.getContents().remove(contentType);

            LocalDate date = Optional.ofNullable(contentType.getDate()).map(DateUtils::toLocalDate).orElse(null);

            DateItem dateItem;
            if (date != null) {
                dateItem = new DateItem(date);
                contentType.setDate(null);
            } else {
                dateItem = new DateItem(null);
            }

            dateItem.setUnparsed(contentType);
            return dateItem;

        } else {
            return null;
        }
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
