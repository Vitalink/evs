package org.imec.ivlab.ehconnector.hub.util;

import be.ehealth.business.kmehrcommons.HcPartyUtil;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import java.util.List;
import java.util.stream.Collectors;
import org.imec.ivlab.core.kmehr.model.util.HCPartyUtil;

public class AuthorUtil {

  private static final String CONNECTOR_PROJECT_NAME = "hubservicev3";

  public static void regenerateAuthorBasedOnCertificate(be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType transaction) throws TechnicalConnectorException {
    transaction.getAuthor().getHcparties().clear();
    AuthorType author = HcPartyUtil.createAuthor(CONNECTOR_PROJECT_NAME);
    removeApplicationHcParties(author);
    transaction.setAuthor(author);
  }

  private static void removeApplicationHcParties(AuthorType author) {
    List<HcpartyType> nonApplicationHcParties = author.getHcparties().stream()
        .filter(hcpartyType -> !HCPartyUtil.isApplicationHcParty(hcpartyType))
        .collect(Collectors.toList());

    author.getHcparties().clear();
    author.getHcparties().addAll(nonApplicationHcParties);
  }

}
