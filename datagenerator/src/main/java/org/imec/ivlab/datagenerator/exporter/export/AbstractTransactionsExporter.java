package org.imec.ivlab.datagenerator.exporter.export;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.GetLatestUpdateResponse;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionResponse;
import be.fgov.ehealth.hubservices.core.v3.Latestupdate;
import org.imec.ivlab.core.authentication.AuthenticationConfigReader;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.LatestUpdateNotFoundException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.TransactionNotFoundException;
import org.imec.ivlab.ehconnector.hub.session.SessionManager;
import org.imec.ivlab.ehconnector.hub.util.LatestUpdatesUtil;
import org.imec.ivlab.ehconnector.hubflow.HubFlow;

import java.io.File;
import java.util.List;

public abstract class AbstractTransactionsExporter implements TransactionsExporter, Writable<GetTransactionResponse> {

    private HubFlow hubFlow;

    public AbstractTransactionsExporter() {
        hubFlow = new HubFlow();
    }

    @Override
    public List<ExportResult<GetTransactionResponse>> exportTransactions(TransactionType transactionType, Patient patient, String actorId, File outputDirectory, String filename, String extension) throws TechnicalConnectorException, VitalinkException, GatewaySpecificErrorException {

        SessionManager.connectWith(AuthenticationConfigReader.loadByName(actorId));

        List<Latestupdate> latestUpdates = null;
        try {
            GetLatestUpdateResponse latestUpdate = hubFlow.getLatestUpdate(patient.getId(), transactionType);
            latestUpdates = LatestUpdatesUtil.getLatestUpdates(latestUpdate.getLatestupdatelist(), transactionType);

            List<GetTransactionResponse> transactions = hubFlow.getIndividualTransactions(patient.getId(), transactionType);
            return getWriter().write(patient, transactions, latestUpdates, outputDirectory, filename, extension);
        } catch (LatestUpdateNotFoundException | TransactionNotFoundException e) {
            return getWriter().write(patient, null, latestUpdates, outputDirectory, filename, extension);
        }

    }

}
