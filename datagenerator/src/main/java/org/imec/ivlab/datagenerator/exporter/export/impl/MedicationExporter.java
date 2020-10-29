package org.imec.ivlab.datagenerator.exporter.export.impl;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionSetResponse;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.datagenerator.exporter.export.AbstractTransactionSetExporter;
import org.imec.ivlab.datagenerator.exporter.export.ExportResult;
import org.imec.ivlab.datagenerator.exporter.writer.SetWriter;
import org.imec.ivlab.datagenerator.exporter.writer.TransactionSetWriter;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;

import java.io.File;

public class MedicationExporter extends AbstractTransactionSetExporter {

    @Override
    public ExportResult<GetTransactionSetResponse> exportTransactionSet(TransactionType transactionType, Patient patient, String actorId, File outputDirectory, String filename, String extension) throws TechnicalConnectorException, VitalinkException, GatewaySpecificErrorException {

        return super.exportTransactionSet(transactionType, patient, actorId, outputDirectory, filename, extension);

    }

    @Override
    public SetWriter<GetTransactionSetResponse> getWriter() {
        return new TransactionSetWriter(TransactionType.MEDICATION_SCHEME);
    }

}
