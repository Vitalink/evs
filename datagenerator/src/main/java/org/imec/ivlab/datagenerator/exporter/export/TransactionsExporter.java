package org.imec.ivlab.datagenerator.exporter.export;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionResponse;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;

import java.io.File;
import java.util.List;

public interface TransactionsExporter {

    List<ExportResult<GetTransactionResponse>> exportTransactions(TransactionType transactionType, Patient patient, String actorId, File outputDirectory, String filename, String extension) throws TechnicalConnectorException, VitalinkException, GatewaySpecificErrorException;

}
