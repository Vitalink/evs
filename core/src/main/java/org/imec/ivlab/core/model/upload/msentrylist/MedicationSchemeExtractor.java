package org.imec.ivlab.core.model.upload.msentrylist;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.kmehr.KmehrConstants;
import org.imec.ivlab.core.kmehr.KmehrHelper;
import org.imec.ivlab.core.kmehr.KmehrMarshaller;
import org.imec.ivlab.core.kmehr.model.localid.URI;
import org.imec.ivlab.core.kmehr.model.util.FolderUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrHeaderUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntry;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.imec.ivlab.core.model.upload.msentrylist.ReferenceDateUtil.getReferenceDate;

public class MedicationSchemeExtractor {

    private final static Logger LOG = LogManager.getLogger(MedicationSchemeExtractor.class);

    public static MSEntryList getMedicationSchemeEntries(KmehrEntryList kmehrEntryList) throws TransformationException {

        MSEntryList msEntryList = new MSEntryList();

        if (CollectionsUtil.emptyOrNull(kmehrEntryList.getKmehrEntries())) {
            return msEntryList;
        }

        for (KmehrEntry kmehrEntry : kmehrEntryList.getKmehrEntries()) {
            Kmehrmessage kmehrmessage = KmehrMarshaller.fromString(kmehrEntry.getBusinessData().getContent());
            msEntryList.getMsEntries().addAll(collectMSEntries(kmehrmessage, kmehrEntry.getUri()));
        }

        return msEntryList;

    }

    public static MSEntryList getMedicationSchemeEntries(Kmehrmessage kmehrmessage) {

        MSEntryList msEntryList = new MSEntryList();

        msEntryList.getMsEntries().addAll(collectMSEntries(kmehrmessage, null));

        return msEntryList;

    }

    private static List<MSEntry> collectMSEntries(Kmehrmessage kmehrmessage, URI uri) {

        List<MSEntry> msEntries = new ArrayList<>();

        if (kmehrmessage == null) {
            return msEntries;
        }

        LocalDate referenceDate = getReferenceDate(kmehrmessage);

        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessage);

        List<TransactionType> mseTransactions = FolderUtil.getTransactions(folderType, CDTRANSACTIONvalues.MEDICATIONSCHEMEELEMENT);
        List<TransactionType> tsTransactions = FolderUtil.getTransactions(folderType, CDTRANSACTIONvalues.TREATMENTSUSPENSION);

        HashMap<TransactionType, List<TransactionType>> mseAndTsTransactions = KmehrHelper.groupMedicationAndSuspensions(mseTransactions, tsTransactions);

        if (mseAndTsTransactions == null || mseAndTsTransactions.size() == 0) {
            return msEntries;
        }

        if (uri != null && StringUtils.equals(KmehrHeaderUtil.getStandard(kmehrmessage.getHeader()), KmehrConstants.KMEHR_STANDARD_CONNECTOR)) {
            KmehrHelper.replaceLocalIds(kmehrmessage, uri.format());
        }

        for (Map.Entry<TransactionType, List<TransactionType>> mseAndTSTransaction : mseAndTsTransactions.entrySet()) {
            MSEntry msEntry = new MSEntry(mseAndTSTransaction.getKey(), mseAndTSTransaction.getValue());
            msEntry.setReferenceDate(referenceDate);
            msEntries.add(msEntry);
        }

        return msEntries;

    }

}
