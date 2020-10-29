package org.imec.ivlab.core.kmehr.modifier.impl;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.kmehr.modifier.KmehrModification;
import org.imec.ivlab.core.kmehr.model.util.FolderUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;

import java.util.List;

public class GatewayKmehrCompatibleWithConnectorModifier implements KmehrModification {

    private final static Logger log = Logger.getLogger(GatewayKmehrCompatibleWithConnectorModifier.class);

    @Override
    public void modify(Kmehrmessage kmehrmessage) {

        List<TransactionType> transactions = FolderUtil.getTransactions(KmehrMessageUtil.getFolderType(kmehrmessage), CDTRANSACTIONvalues.MEDICATIONSCHEMEELEMENT);

        if (transactions == null) {
            return;
        }

        for (TransactionType transaction : transactions) {
            if (transaction == null) {
                continue;
            }

            if (!hasIdKmehr(transaction)) {
                log.info("Adding kmehr id to transaction");
                IDKMEHR idKmehr = new IDKMEHR();
                idKmehr.setSV("1.0");
                idKmehr.setS(IDKMEHRschemes.ID_KMEHR);
                idKmehr.setValue("100");
                transaction.getIds().add(idKmehr);
            }

        }


    }

    private boolean hasIdKmehr(TransactionType transaction) {
        if (transaction.getIds() == null) {
            return false;
        }

        for (IDKMEHR idkmehr : transaction.getIds()) {
            if (IDKMEHRschemes.ID_KMEHR.equals(idkmehr.getS()) && idkmehr.getValue() != null) {
                return true;
            }
        }

        return false;
    }

}
