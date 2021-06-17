package org.imec.ivlab.core.model.upload.extractor;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import java.util.List;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.kmehr.KmehrMarshaller;
import org.imec.ivlab.core.kmehr.model.util.FolderUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.upload.KmehrWithReference;
import org.imec.ivlab.core.model.upload.KmehrWithReferenceList;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntry;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.util.CollectionsUtil;

public abstract class KmehrWithReferenceExtractor {

    private final Logger LOG = Logger.getLogger(KmehrWithReferenceExtractor.class);

    public KmehrWithReferenceList getKmehrWithReferenceList(KmehrEntryList kmehrEntryList) {

        KmehrWithReferenceList childPreventionList = new KmehrWithReferenceList();

        if (CollectionsUtil.emptyOrNull(kmehrEntryList.getKmehrEntries())) {
            return childPreventionList;
        }

        for (KmehrEntry kmehrEntry : kmehrEntryList.getKmehrEntries()) {
            Kmehrmessage kmehrmessage = KmehrMarshaller.fromString(kmehrEntry.getBusinessData().getContent());
            if (kmehrEntry != null && kmehrEntry.getBusinessData() != null) {
                childPreventionList.getList().add(getKmehrWithReference(kmehrmessage, getCdTransactionValues()));

            }
        }

        return childPreventionList;

    }

    public KmehrWithReferenceList getKmehrWithReferenceList(List<Kmehrmessage> kmehrmessages) {

        KmehrWithReferenceList childPreventionList = new KmehrWithReferenceList();

        if (CollectionsUtil.emptyOrNull(kmehrmessages)) {
            return childPreventionList;
        }

        for (Kmehrmessage kmehrmessage : kmehrmessages) {
            childPreventionList.getList().add(getKmehrWithReference(kmehrmessage, getCdTransactionValues()));
        }

        return childPreventionList;

    }

    protected abstract CDTRANSACTIONvalues getCdTransactionValues();


    private KmehrWithReference getKmehrWithReference(Kmehrmessage kmehrmessage, CDTRANSACTIONvalues cdtransactioNvalues) {

        if (kmehrmessage == null) {
            return null;
        }

        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessage);

        List<TransactionType> diaryNoteTransactions = FolderUtil.getTransactions(folderType, cdtransactioNvalues);

        if (CollectionsUtil.emptyOrNull(diaryNoteTransactions)) {
            LOG.error(String.format("No %s transaction found within kmehr!", cdtransactioNvalues.value()));
            return null;
        }

        return new KmehrWithReference(kmehrmessage);

    }

}
