package org.imec.ivlab.core.model.upload.diarylist;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.kmehr.KmehrMarshaller;
import org.imec.ivlab.core.kmehr.model.util.FolderUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntry;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.model.upload.sumehrlist.Sumehr;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.List;

public class DiaryNoteListExtractor {

    private final static Logger LOG = Logger.getLogger(DiaryNoteListExtractor.class);

    public static DiaryNoteList getDiaryList(KmehrEntryList kmehrEntryList) throws TransformationException {

        DiaryNoteList diaryNoteList = new DiaryNoteList();

        if (CollectionsUtil.emptyOrNull(kmehrEntryList.getKmehrEntries())) {
            return diaryNoteList;
        }

        for (KmehrEntry kmehrEntry : kmehrEntryList.getKmehrEntries()) {
            Kmehrmessage kmehrmessage = KmehrMarshaller.fromString(kmehrEntry.getBusinessData().getContent());
            if (kmehrEntry != null && kmehrEntry.getBusinessData() != null) {
                diaryNoteList.getList().add(getDiary(kmehrmessage));

            }
        }

        return diaryNoteList;

    }

    public static DiaryNoteList getDiaryList(List<Kmehrmessage> kmehrmessages) {

        DiaryNoteList diaryNoteList = new DiaryNoteList();

        if (CollectionsUtil.emptyOrNull(kmehrmessages)) {
            return diaryNoteList;
        }

        for (Kmehrmessage kmehrmessage : kmehrmessages) {
            diaryNoteList.getList().add(getDiary(kmehrmessage));
        }

        return diaryNoteList;

    }


    private static DiaryNote getDiary(Kmehrmessage kmehrmessage) {

        if (kmehrmessage == null) {
            return null;
        }

        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessage);

        List<TransactionType> diaryNoteTransactions = FolderUtil.getTransactions(folderType, CDTRANSACTIONvalues.DIARYNOTE);

        if (CollectionsUtil.emptyOrNull(diaryNoteTransactions)) {
            LOG.error("No DIARYNOTE transaction found within kmehr!");
            return null;
        }

        return new DiaryNote(kmehrmessage);

    }

}
