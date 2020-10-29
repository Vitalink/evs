package org.imec.ivlab.core.model.evsref;

import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;

import java.io.Serializable;
import java.util.List;

public interface Identifiable extends Serializable {

    void setReference (EVSREF reference);

    EVSREF getReference();

    TransactionType getIdentifiableTransaction();

    List<TransactionType> getAllTransactions();

}
