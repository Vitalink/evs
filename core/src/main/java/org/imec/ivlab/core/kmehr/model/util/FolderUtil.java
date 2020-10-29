package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDMEDIATYPEvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.LnkType;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.exceptions.DataNotFoundException;
import org.imec.ivlab.core.exceptions.MultipleEntitiesFoundException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FolderUtil {

    public static List<LnkType> getLinks(FolderType folderType, CDMEDIATYPEvalues cdmediatypEvalues) {

        List<LnkType> lnkTypes = new ArrayList<>();

        if (CollectionUtils.isEmpty(folderType.getLnks())) {
            return lnkTypes;
        }

        Iterator<LnkType> lnkTypeIterator = folderType.getLnks().iterator();

        while (lnkTypeIterator.hasNext()) {

            LnkType lnkType = lnkTypeIterator.next();


            if (StringUtils.equalsIgnoreCase(lnkType.getMEDIATYPE().value(), cdmediatypEvalues.value())) {
                lnkTypes.add(lnkType);
            }

        }

        return lnkTypes;

    }

    public static List<TransactionType> getTransactions(FolderType folderType, CDTRANSACTIONvalues cdTransactionTypeFilter) {

        List<TransactionType> transactionTypes = new ArrayList<>();

        if (CollectionUtils.isEmpty(folderType.getTransactions())) {
            return transactionTypes;
        }

        Iterator<TransactionType> transactionTypeIterator = folderType.getTransactions().iterator();

        while (transactionTypeIterator.hasNext()) {

            TransactionType transactionType = transactionTypeIterator.next();

            if (CollectionUtils.isEmpty(transactionType.getCds())) {
                continue;
            }

            for (CDTRANSACTION cdtransaction : transactionType.getCds()) {
                if (StringUtils.equalsIgnoreCase(cdtransaction.getValue(), cdTransactionTypeFilter.value())) {
                    transactionTypes.add(transactionType);
                    break;
                }
            }

        }

        return transactionTypes;

    }

    public static TransactionType getTransaction(FolderType folderType, CDTRANSACTIONvalues cdTransactionTypeFilter) {

        List<TransactionType> transactions = getTransactions(folderType, cdTransactionTypeFilter);

        if (CollectionUtils.isEmpty(transactions)) {
            throw new DataNotFoundException("No transaction found with CD-TRANSACTION: " + cdTransactionTypeFilter);
        }

        if (CollectionUtils.size(transactions) > 1) {
            throw new MultipleEntitiesFoundException("Multiple transactions found with CD-TRANSACTION: " + cdTransactionTypeFilter);
        }

        return transactions.get(0);

    }

}
