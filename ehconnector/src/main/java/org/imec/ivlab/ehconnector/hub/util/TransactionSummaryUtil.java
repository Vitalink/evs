package org.imec.ivlab.ehconnector.hub.util;

import be.fgov.ehealth.hubservices.core.v3.TransactionSummaryType;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.exceptions.DataNotFoundException;
import org.imec.ivlab.core.exceptions.MultipleEntitiesFoundException;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.ArrayList;
import java.util.List;

public class TransactionSummaryUtil {

    /**
     * Get one or more latestupdate objects from the input list where the S attribute of the cd object equals the input {@code transactionTypeFilter}
     * @param transactionSummaryType
     * @param cdtransactioNschemesFilter
     * @return
     */
    public static List<CDTRANSACTION> getCDTransactions(TransactionSummaryType transactionSummaryType , CDTRANSACTIONschemes cdtransactioNschemesFilter) {

        List<CDTRANSACTION> cdtransactions = new ArrayList<>();

        if (transactionSummaryType == null || CollectionsUtil.emptyOrNull(transactionSummaryType.getCds())) {
            return cdtransactions;
        }

        for (CDTRANSACTION cdtransaction : transactionSummaryType.getCds()) {
            if (StringUtils.equalsIgnoreCase(cdtransaction.getS().value(), cdtransactioNschemesFilter.value())) {
                cdtransactions.add(cdtransaction);
            }
        }

        return cdtransactions;

    }

    public static CDTRANSACTION getCDTransaction(TransactionSummaryType transactionSummaryType , CDTRANSACTIONschemes cdtransactioNschemesFilter) {

        List<CDTRANSACTION> cdtransactions = getCDTransactions(transactionSummaryType, cdtransactioNschemesFilter);

        if (CollectionUtils.isEmpty(cdtransactions)) {
            throw new DataNotFoundException("No transaction found with type: " + cdtransactioNschemesFilter);
        }

        if (CollectionUtils.size(cdtransactions) > 1) {
            throw new MultipleEntitiesFoundException("Multiple transactions found with type: " + cdtransactioNschemesFilter);
        }

        return cdtransactions.get(0);

    }


}
