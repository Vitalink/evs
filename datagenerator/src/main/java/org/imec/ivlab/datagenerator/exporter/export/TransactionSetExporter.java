package org.imec.ivlab.datagenerator.exporter.export;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionSetResponse;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;

import java.io.File;

public interface TransactionSetExporter {

    ExportResult<GetTransactionSetResponse> exportTransactionSet(TransactionType transactionType, Patient patient, String actorId, File outputDirectory, String filename, String extension) throws TechnicalConnectorException, VitalinkException, GatewaySpecificErrorException;

    ExportResult<GetTransactionSetResponse> exportTransactionSetPdf(TransactionType transactionType, Patient patient, String actorId, File outputDirectory, String filename, String extension) throws VitalinkException, TechnicalConnectorException;

}
