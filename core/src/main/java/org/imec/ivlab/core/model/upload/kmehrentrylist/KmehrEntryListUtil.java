package org.imec.ivlab.core.model.upload.kmehrentrylist;

import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.exceptions.DataNotFoundException;

import java.util.Objects;

public class KmehrEntryListUtil {

    public static KmehrEntry getEntryByTransactionID(KmehrEntryList kmehrEntryList, String URITransactionID) {

        if (kmehrEntryList != null) {

            for (KmehrEntry kmehrEntry : kmehrEntryList.getKmehrEntries()) {
                if (Objects.equals(kmehrEntry.getUri().getTransactionId(), URITransactionID)) {
                    return kmehrEntry;
                }
            }

        }

        throw new DataNotFoundException("No kmehr entry found with TransactionID " + URITransactionID);

    }

    public static KmehrEntry getEntryByTextInBusinessData(KmehrEntryList kmehrEntryList, String textToSearch) {

        if (kmehrEntryList != null) {

            for (KmehrEntry kmehrEntry : kmehrEntryList.getKmehrEntries()) {

                if (StringUtils.containsIgnoreCase(kmehrEntry.getBusinessData().getContent(), textToSearch)) {
                    return kmehrEntry;
                }
            }

        }

        throw new DataNotFoundException("No kmehr entry having following matching text in business data: " + textToSearch);

    }

}
