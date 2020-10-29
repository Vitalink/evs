package org.imec.ivlab.core.jcommander;

import com.beust.jcommander.IStringConverter;
import org.imec.ivlab.core.model.upload.TransactionType;


public class TransactionTypeConverter implements IStringConverter<TransactionType>
{
    //@Override
    public TransactionType convert(String transactionType) {

        return TransactionType.fromValue(transactionType);

    }

}