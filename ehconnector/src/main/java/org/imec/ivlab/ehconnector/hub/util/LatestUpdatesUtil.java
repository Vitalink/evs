package org.imec.ivlab.ehconnector.hub.util;

import be.fgov.ehealth.hubservices.core.v3.LatestUpdateListType;
import be.fgov.ehealth.hubservices.core.v3.Latestupdate;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.ArrayList;
import java.util.List;

public class LatestUpdatesUtil {

    /**
     * Get one or more latestupdate objects from the input list where the value of the cd object equals the input {@code transactionTypeFilter}
     * @param latestUpdateListType
     * @param transactionTypeFilter
     * @return
     */
    public static List<Latestupdate>    getLatestUpdates(LatestUpdateListType latestUpdateListType, TransactionType transactionTypeFilter) {

        List<Latestupdate> latestupdates = new ArrayList<>();

        if (latestUpdateListType == null || CollectionsUtil.emptyOrNull(latestUpdateListType.getLatestupdates())) {
            return latestupdates;
        }

        for (Latestupdate latestupdate : latestUpdateListType.getLatestupdates()) {

            if (latestupdate.getCd() != null && StringUtils.equalsIgnoreCase(latestupdate.getCd().getValue(), transactionTypeFilter.getTransactionTypeValueForGetLatestUpdate())) {
                latestupdates.add(latestupdate);
            }

        }

        return latestupdates;

    }


}
