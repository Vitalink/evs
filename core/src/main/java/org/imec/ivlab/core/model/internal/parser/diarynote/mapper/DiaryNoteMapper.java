package org.imec.ivlab.core.model.internal.parser.diarynote.mapper;

import static org.imec.ivlab.core.kmehr.model.util.TransactionUtil.getLinksAndRemoveFromTransaction;
import static org.imec.ivlab.core.kmehr.model.util.TransactionUtil.getTextAndRemoveFromTransaction;
import static org.imec.ivlab.core.kmehr.model.util.TransactionUtil.getTextWithLayoutAndRemoveFromTransaction;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.SerializationUtils;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.internal.parser.common.BaseMapper;
import org.imec.ivlab.core.model.internal.parser.diarynote.DiaryNote;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.DateUtils;


public class DiaryNoteMapper extends BaseMapper {

    public static DiaryNote kmehrToDiaryNote(Kmehrmessage kmehrmessage) {

        DiaryNote entry = new DiaryNote();

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
        entry.setCdDiaryValues(getCdDiaryValues(firstTransaction));
        entry.setCdLocalEntries(getCdLocalEntries(firstTransaction));
        entry.getTransactionCommon().setPerson(toPatient(folderType.getPatient()));
        markFolderLevelFieldsAsProcessed(cloneFolder);

        entry.getTransactionCommon().setDate(DateUtils.toLocalDate(firstTransaction.getDate()));
        entry.getTransactionCommon().setTime(DateUtils.toLocalTime(firstTransaction.getTime()));
        entry.getTransactionCommon().setRecordDateTime(DateUtils.toLocalDateTime(firstTransaction.getRecorddatetime()));
        entry.getTransactionCommon().setAuthor(mapHcPartyFields(firstTransaction.getAuthor()));
        entry.getTransactionCommon().setRedactor(mapHcPartyFields(firstTransaction.getRedactor()));
        entry.getTransactionCommon().setCdtransactions(new ArrayList<>(firstTransaction.getCds()));
        entry.setTextTypes(getTextAndRemoveFromTransaction(firstTransaction));
        entry.setTextWithLayoutTypes(getTextWithLayoutAndRemoveFromTransaction(firstTransaction));
        entry.setLinkTypes(getLinksAndRemoveFromTransaction(firstTransaction));
        markTransactionAsProcessed(firstTransaction);

        entry.setUnparsed(cloneKmehr);

        return entry;
    }

    private static List<String> getCdDiaryValues(TransactionType firstTransaction) {
        return filterCdsForScheme(firstTransaction.getCds(), CDTRANSACTIONschemes.CD_DIARY)
            .stream()
            .map(CDTRANSACTION::getValue)
            .collect(Collectors.toList());
    }

    private static List<CDTRANSACTION> getCdLocalEntries(TransactionType firstTransaction) {
        return filterCdsForScheme(firstTransaction.getCds(), CDTRANSACTIONschemes.LOCAL);
    }

    private static List<CDTRANSACTION> filterCdsForScheme(List<CDTRANSACTION> cdtransactions, CDTRANSACTIONschemes cdtransactioNschemesFilter) {
        return cdtransactions.stream()
            .filter(cdtransaction -> cdtransaction.getS().equals(cdtransactioNschemesFilter))
            .collect(Collectors.toList());
    }

}
