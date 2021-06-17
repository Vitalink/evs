package org.imec.ivlab.datagenerator.exporter.export.impl;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionResponse;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;
import java.io.File;
import java.util.List;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.datagenerator.exporter.export.AbstractTransactionsExporter;
import org.imec.ivlab.datagenerator.exporter.export.ExportResult;
import org.imec.ivlab.datagenerator.exporter.writer.TransactionWriter;
import org.imec.ivlab.datagenerator.exporter.writer.Writer;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;

public class PopulationBasedScreeningExporter extends AbstractTransactionsExporter {

    @Override
    public List<ExportResult<GetTransactionResponse>> exportTransactions(TransactionType transactionType, Patient patient, String actorId, File outputDirectory, String filename, String extension) throws TechnicalConnectorException, VitalinkException, GatewaySpecificErrorException {

        return super.exportTransactions(transactionType, patient, actorId, outputDirectory, filename, extension);

    }

    @Override
    public Writer<GetTransactionResponse> getWriter() {
        return new TransactionWriter(TransactionType.POPULATION_BASED_SCREENING, CDTRANSACTIONvalues.POPULATION_BASED_SCREENING);
    }

}
