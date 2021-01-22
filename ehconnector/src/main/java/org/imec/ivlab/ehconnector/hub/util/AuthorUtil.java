package org.imec.ivlab.ehconnector.hub.util;

import be.ehealth.business.kmehrcommons.HcPartyUtil;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HeaderType;
import java.util.List;
import java.util.stream.Collectors;
import org.imec.ivlab.core.kmehr.model.util.HCPartyUtil;

public class AuthorUtil {

  private static final String CONNECTOR_PROJECT_NAME = "hubservicev3";

  public static void regenerateTransactionAuthorBasedOnCertificate(be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType transaction) throws TechnicalConnectorException {
    transaction.getAuthor().getHcparties().clear();
    AuthorType author = HcPartyUtil.createAuthor(CONNECTOR_PROJECT_NAME);
    removeApplicationHcParties(author.getHcparties());
    transaction.setAuthor(author);
  }

  public static void regenerateSenderAuthorBasedOnCertificate(HeaderType headerType) throws TechnicalConnectorException {
    headerType.getSender().getHcparties().clear();
    List<HcpartyType> authorHcParties = createAuthorPersonHcParties();
    headerType.getSender().getHcparties().addAll(authorHcParties);
  }

  public static List<HcpartyType> createAuthorPersonHcParties() throws TechnicalConnectorException {
    List<HcpartyType> authorHcParties = HcPartyUtil.createAuthorHcParties(CONNECTOR_PROJECT_NAME);
    removeApplicationHcParties(authorHcParties);
    return authorHcParties;
  }

  private static void removeApplicationHcParties(List<HcpartyType> hcparties) {
    List<HcpartyType> nonApplicationHcParties = hcparties.stream()
        .filter(hcpartyType -> !HCPartyUtil.isApplicationHcParty(hcpartyType))
        .collect(Collectors.toList());

    hcparties.clear();
    hcparties.addAll(nonApplicationHcParties);
  }

}
