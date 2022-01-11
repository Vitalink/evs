package org.imec.ivlab.ehconnector.util;

import be.fgov.ehealth.hubservices.core.v3.FolderTypeUnbounded;
import be.fgov.ehealth.hubservices.core.v3.GetTransactionListResponse;
import be.fgov.ehealth.hubservices.core.v3.KmehrHeaderGetTransactionList;
import be.fgov.ehealth.hubservices.core.v3.TransactionSummaryType;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.config.EVSConfig;
import org.imec.ivlab.core.config.EVSProperties;
import org.imec.ivlab.core.model.upload.TransactionType;
import org.imec.ivlab.ehconnector.hub.util.TransactionSummaryUtil;
import org.joda.time.format.DateTimeFormat;

public class TransactionHelper {
  private final static Logger LOG = LogManager.getLogger(TransactionHelper.class);

  private static boolean mustFilterOutTransactionsWithoutAccess() {
    return Optional.ofNullable(EVSConfig.getInstance().getPropertyOrNull(EVSProperties.FILTER_OUT_TRANSACTIONS_HAVING_PATIENT_ACCESS_NO))
        .map(value -> StringUtils.equalsIgnoreCase(value, "true"))
        .orElse(false);
  }

  private static boolean hasPatientAccess(List<CDTRANSACTION> cdTransactions) {
    boolean noAccessFound = cdTransactions.stream()
        .filter(cdTransaction -> Objects.equals(cdTransaction.getS(), CDTRANSACTIONschemes.LOCAL))
        .filter(cdTransaction -> StringUtils.equalsIgnoreCase(cdTransaction.getSL(), "PatientAccess"))
        .anyMatch(cdTransaction -> StringUtils.equalsIgnoreCase(cdTransaction.getValue(), "no"));
    return !noAccessFound;
  }

  private static Predicate<TransactionSummaryType> hasTransactionCdWithSchemeAndValue(CDTRANSACTIONschemes scheme, String value) {
    return transactionSummaryType -> {
      List<CDTRANSACTION> cdTransactions = TransactionSummaryUtil.getCDTransactions(transactionSummaryType, scheme);
      return cdTransactions.stream().map(CDTRANSACTION::getValue).anyMatch(cdValue -> StringUtils.equalsIgnoreCase(cdValue, value));
    };
  }

  public static Optional<TransactionSummaryType> findLatestTransaction(GetTransactionListResponse transactionListResponse, TransactionType transactionType) {
    Comparator<TransactionSummaryType> compareByRecordDateTime = Comparator.comparing(TransactionSummaryType::getRecorddatetime);

    List<TransactionSummaryType> transactionSummaries = Optional.ofNullable(transactionListResponse)
        .map(GetTransactionListResponse::getKmehrheader)
        .map(KmehrHeaderGetTransactionList::getFolder)
        .map(FolderTypeUnbounded::getTransactions)
        .orElse(Collections.emptyList());

    transactionSummaries.forEach(transactionSummaryType -> LOG.info("Found transaction: " + getTransactionDetails(transactionSummaryType)));

    Optional<TransactionSummaryType> latestTransaction = transactionSummaries.stream()
        .filter(hasTransactionCdWithSchemeAndValue(CDTRANSACTIONschemes.CD_TRANSACTION, transactionType.getTransactionTypeValueForGetTransactionList()))
        .filter(transactionSummaryType -> !mustFilterOutTransactionsWithoutAccess() || hasPatientAccess(transactionSummaryType.getCds()))
        .max(compareByRecordDateTime);

    if (latestTransaction.isPresent()) {
      LOG.info("Most recent transaction: " + getTransactionDetails(latestTransaction.get()));
    } else {
      LOG.info("Did not find a suitable transaction");
    }

    return latestTransaction;

  }

  public static String getTransactionDetails(TransactionSummaryType transactionSummaryType) {
    String author = transactionSummaryType.getAuthor().getHcparties().stream()
        .map(hcpartyType -> StringUtils.joinWith(" ", hcpartyType.getName(), hcpartyType.getFirstname(), hcpartyType.getFamilyname()))
        .collect(Collectors.joining(" - "));
    String recordDateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(transactionSummaryType.getRecorddatetime());
    return recordDateTime + " " + author;
  }
}