package org.imec.ivlab.core.model.internal.parser;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.imec.ivlab.core.model.internal.parser.common.Header;
import org.imec.ivlab.core.model.internal.parser.common.TransactionCommon;

public abstract class ParsedKmehrItem extends AbstractParsedItem<Kmehrmessage> {

    private Header header = new Header();
    private TransactionCommon transactionCommon = new TransactionCommon();

    public ParsedKmehrItem() {
        super("kmehrmessage");
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public TransactionCommon getTransactionCommon() {
        return transactionCommon;
    }

    public void setTransactionCommon(TransactionCommon transactionCommon) {
        this.transactionCommon = transactionCommon;
    }
}
