package org.imec.ivlab.core.model.upload.sumehrlist;

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
import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.List;

public class SumehrListExtractor {

    private final static Logger LOG = Logger.getLogger(SumehrListExtractor.class);

    public static SumehrList getSumehrList(KmehrEntryList kmehrEntryList) {

        SumehrList sumehrList = new SumehrList();

        if (CollectionsUtil.emptyOrNull(kmehrEntryList.getKmehrEntries())) {
            return sumehrList;
        }

        for (KmehrEntry kmehrEntry : kmehrEntryList.getKmehrEntries()) {
            Kmehrmessage kmehrmessage = null;
            try {
                kmehrmessage = KmehrMarshaller.fromString(kmehrEntry.getBusinessData().getContent());
            } catch (TransformationException e) {
                throw new RuntimeException(e);
            }
            if (kmehrEntry != null && kmehrEntry.getBusinessData() != null) {
                sumehrList.getList().add(getSumehr(kmehrmessage));

            }
        }

        return sumehrList;

    }

    public static SumehrList getSumehrList(List<Kmehrmessage> kmehrmessages) {

        SumehrList sumehrList = new SumehrList();

        if (CollectionsUtil.emptyOrNull(kmehrmessages)) {
            return sumehrList;
        }

        for (Kmehrmessage kmehrmessage : kmehrmessages) {
            sumehrList.getList().add(getSumehr(kmehrmessage));
        }

        return sumehrList;

    }


    private static Sumehr getSumehr(Kmehrmessage kmehrmessage) {

        if (kmehrmessage == null) {
            return null;
        }

        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessage);

        List<TransactionType> sumehrTransactions = FolderUtil.getTransactions(folderType, CDTRANSACTIONvalues.SUMEHR);

        if (CollectionsUtil.emptyOrNull(sumehrTransactions)) {
            LOG.error("No SUMEHR transaction found within kmehr!");
            return null;
        }

        return new Sumehr(kmehrmessage);

    }

}
