package org.imec.ivlab.core.model.upload.msentrylist;

import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import org.imec.ivlab.core.model.evsref.EVSREF;
import org.imec.ivlab.core.model.evsref.Identifiable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MSEntry implements Serializable, Identifiable {

    private TransactionType mseTransaction;
    private List<TransactionType> tsTransactions = new ArrayList<>();
    private EVSREF reference;
    private LocalDate referenceDate;

    public MSEntry(TransactionType mseTransaction, List<TransactionType> tsTransactions) {
        this.mseTransaction = mseTransaction;
        this.tsTransactions = tsTransactions;
    }

    @Override
    public TransactionType getIdentifiableTransaction() {
        return mseTransaction;
    }

    public TransactionType getMseTransaction() {
        return mseTransaction;
    }

    public void setMseTransaction(TransactionType mseTransaction) {
        this.mseTransaction = mseTransaction;
    }

    public List<TransactionType> getTsTransactions() {
        return tsTransactions;
    }

    public void setTsTransactions(List<TransactionType> tsTransactions) {
        this.tsTransactions = tsTransactions;
    }

    public EVSREF getReference() {
        return reference;
    }


    public void setReference(EVSREF reference) {
        this.reference = reference;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    public List<TransactionType> getAllTransactions() {
        List<TransactionType> allTransactions = new ArrayList<>();
        if (mseTransaction != null) {
            allTransactions.add(mseTransaction);
        }
        if (tsTransactions != null) {
            for (TransactionType tsTransaction : tsTransactions) {
                if (tsTransaction != null) {
                    allTransactions.add(tsTransaction);
                }
            }
        }

        return allTransactions;
    }

}
