package org.imec.ivlab.datagenerator.exporter.export;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v3.GetLatestUpdateResponse;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionSetResponse;
import be.fgov.ehealth.hubservices.core.v3.Latestupdate;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDMEDIATYPEvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.LnkType;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.authentication.AuthenticationConfigReader;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.kmehr.model.util.FolderUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.ehconnector.business.HubHelper;
import org.imec.ivlab.ehconnector.hub.exception.GatewaySpecificErrorException;
import org.imec.ivlab.ehconnector.hub.exception.curable.InternalErrorException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.LatestUpdateNotFoundException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.SubjectWithSSINUnknownException;
import org.imec.ivlab.ehconnector.hub.exception.incurable.TransactionNotFoundException;
import org.imec.ivlab.ehconnector.hub.session.SessionManager;
import org.imec.ivlab.ehconnector.hub.util.LatestUpdatesUtil;
import org.imec.ivlab.ehconnector.hubflow.HubFlow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractTransactionSetExporter implements TransactionSetExporter, WritableSet<GetTransactionSetResponse> {

    private final static Logger LOG = LogManager.getLogger(AbstractTransactionSetExporter.class);


    private HubFlow hubFlow;
    private HubHelper hubHelper = new HubHelper();

    public AbstractTransactionSetExporter() {
        hubFlow = new HubFlow();
    }

    @Override
    public ExportResult<GetTransactionSetResponse> exportTransactionSet(TransactionType transactionType, Patient patient, String actorId, File outputDirectory, String filename, String extension) throws TechnicalConnectorException, VitalinkException, GatewaySpecificErrorException {

        SessionManager.connectWith(AuthenticationConfigReader.loadByName(actorId));

        List<Latestupdate> latestUpdates = null;
        try {
            GetLatestUpdateResponse latestUpdate = hubFlow.getLatestUpdate(patient.getId(), transactionType);
            latestUpdates = LatestUpdatesUtil.getLatestUpdates(latestUpdate.getLatestupdatelist(), transactionType);

            hubFlow.setRetryOnCurableError(true);
            GetTransactionSetResponse transactionSet = hubFlow.getTransactionSet(patient.getId(), transactionType);
            return getWriter().write(patient, transactionSet, latestUpdates, outputDirectory, filename, extension);

        } catch (LatestUpdateNotFoundException | TransactionNotFoundException e) {
            return getWriter().write(patient, null, latestUpdates, outputDirectory, filename, extension);
        }

    }

    @Override
    public ExportResult<GetTransactionSetResponse> exportTransactionSetPdf(TransactionType transactionType, Patient patient, String actorId, File outputDirectory, String filename, String extension) throws VitalinkException, TechnicalConnectorException {

        SessionManager.connectWith(AuthenticationConfigReader.loadByName(actorId));

        byte[] pdfContent = getPdfContent(transactionType, patient);

        if (pdfContent != null) {
            return getWriter().writePdf(patient, pdfContent, outputDirectory, filename, extension);
        } else {
            LOG.info("No PDF content received. Will not write pdf.");
            return null;
        }

    }

    public byte[] getPdfContent(TransactionType transactionType, Patient patient) throws VitalinkException {
        GetTransactionSetResponse getTransactionSetResponse = null;
        try {
            List<CDTRANSACTION> cdtransactions = new ArrayList<>();
            cdtransactions.add(hubHelper.getCdtransaction("1.0", CDTRANSACTIONschemes.CD_HUBSERVICE, "pdf"));
            hubFlow.setRetryOnCurableError(false);
            getTransactionSetResponse = hubFlow.getTransactionSet(patient.getId(), transactionType, cdtransactions);
            FolderType folderType = KmehrMessageUtil.getFolderType(getTransactionSetResponse.getKmehrmessage());
            List<LnkType> links = FolderUtil.getLinks(folderType, CDMEDIATYPEvalues.APPLICATION_PDF);
            if(CollectionUtils.isNotEmpty(links)){
                return links.get(0).getValue();
            } else {
                return null;
            }

        } catch (TransactionNotFoundException e) {
            return null;
        } catch (SubjectWithSSINUnknownException e) {
            return null;
        } catch (InternalErrorException e) {
            LOG.error("Error during gateway medication scheme generation: " + ExceptionUtils.getMessage(e));
            return null;
        } catch (Exception e) {
            throw new VitalinkException(e);
        }
    }

}
