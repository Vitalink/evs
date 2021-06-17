package org.imec.ivlab.core.model.internal.parser.vaccination.mapper;

import static org.imec.ivlab.core.kmehr.model.util.TransactionUtil.getItemsAndRemoveFromTransaction;
import static org.imec.ivlab.core.kmehr.model.util.TransactionUtil.getLinksAndRemoveFromTransaction;
import static org.imec.ivlab.core.kmehr.model.util.TransactionUtil.getTextAndRemoveFromTransaction;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.ContentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.QuantityType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import be.fgov.ehealth.standards.kmehr.schema.v1.UnitType;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.SerializationUtils;
import org.imec.ivlab.core.kmehr.mapper.KmehrMapper;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.internal.parser.common.BaseMapper;
import org.imec.ivlab.core.model.internal.parser.vaccination.EncounterLocation;
import org.imec.ivlab.core.model.internal.parser.vaccination.Vaccination;
import org.imec.ivlab.core.model.internal.parser.vaccination.VaccinationItem;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.DateUtils;


public class VaccinationMapper extends BaseMapper {

    public static Vaccination kmehrToVaccination(Kmehrmessage kmehrmessage) {

        Vaccination entry = new Vaccination();

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

        entry.getTransactionCommon().setPerson(toPatient(folderType.getPatient()));
        markFolderLevelFieldsAsProcessed(cloneFolder);

        entry.getTransactionCommon().setDate(DateUtils.toLocalDate(firstTransaction.getDate()));
        entry.getTransactionCommon().setTime(DateUtils.toLocalTime(firstTransaction.getTime()));
        entry.getTransactionCommon().setRecordDateTime(DateUtils.toLocalDateTime(firstTransaction.getRecorddatetime()));
        entry.getTransactionCommon().setAuthor(mapHcPartyFields(firstTransaction.getAuthor()));
        entry.getTransactionCommon().setRedactor(mapHcPartyFields(firstTransaction.getRedactor()));
        entry.setVaccinationItems(toVaccinations(getItemsAndRemoveFromTransaction(firstTransaction, CDITEMvalues.VACCINE)));
        entry.setEncounterLocations(toEncounterLocations(getItemsAndRemoveFromTransaction(firstTransaction, CDITEMvalues.ENCOUNTERLOCATION)));
        entry.setTextTypes(getTextAndRemoveFromTransaction(firstTransaction));
        entry.setLinkTypes(getLinksAndRemoveFromTransaction(firstTransaction));
        markTransactionAsProcessed(firstTransaction);

        entry.setUnparsed(cloneKmehr);

        return entry;
    }

    private static List<EncounterLocation> toEncounterLocations(List<ItemType> itemTypes) {
        return Optional
            .ofNullable(itemTypes)
            .orElse(Collections.emptyList())
            .stream()
            .map(VaccinationMapper::toEncounterLocation)
            .collect(Collectors.toList());
    }

    private static List<VaccinationItem> toVaccinations(List<ItemType> itemTypes) {
        return Optional
            .of(itemTypes)
            .orElse(Collections.emptyList())
            .stream()
            .map(VaccinationMapper::toVaccination)
            .collect(Collectors.toList());
    }

    private static EncounterLocation toEncounterLocation(ItemType itemType) {

        ItemType clone = SerializationUtils.clone(itemType);

        EncounterLocation encounterLocation = new EncounterLocation();

        clearIds(clone);
        clearCds(clone);

        encounterLocation.setAuthors(mapHcPartyFields(itemType.getAuthor()));

        encounterLocation.setTextTypes(itemType.getTexts());
        clone.getTexts().clear();

        return encounterLocation;
    }

    private static VaccinationItem toVaccination(ItemType itemType) {

        ItemType clone = SerializationUtils.clone(itemType);

        VaccinationItem vaccination = new VaccinationItem();

        clearIds(clone);
        clearCds(clone);

        if (itemType.getBeginmoment() != null) {
            vaccination.setBeginMoment(DateUtils.toLocalDate(itemType.getBeginmoment().getDate()));
            clone.getBeginmoment().setDate(null);
        }

        vaccination.setMedicinalProductTypes(itemType.getContents().stream().map(ContentType::getMedicinalproduct).collect(Collectors.toList()));
        vaccination.setSubstanceproducts(itemType.getContents().stream().map(ContentType::getSubstanceproduct).collect(Collectors.toList()));
        vaccination.setCdcontents(itemType.getContents().stream().flatMap(entry -> entry.getCds().stream()).collect(Collectors.toList()));
        vaccination.setTextTypes(itemType.getTexts());
        clearMedicinalProductAndSubstanceProduct(clone);
        clearContentTypeCds(clone);
        clearContentTypeTextTypes(clone);
        clearTextTypes(clone);

        vaccination.setLifecycle(KmehrMapper.toLifeCycleValues(itemType.getLifecycle()));
        clone.setLifecycle(null);

        vaccination.setBatch(itemType.getBatch());
        clone.setBatch(null);

        vaccination.setQuantity(Optional.of(itemType).map(ItemType::getQuantity).map(QuantityType::getDecimal).orElse(null));
        vaccination.setQuantityUnit(Optional.of(itemType).map(ItemType::getQuantity).map(QuantityType::getUnit).map(UnitType::getCd).orElse(null));

        clone.setQuantity(null);

        vaccination.setUnparsed(clone);

        return vaccination;
    }

}
