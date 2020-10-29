package org.imec.ivlab.core.kmehr.model.util;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYvalues;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.exceptions.DataNotFoundException;
import org.imec.ivlab.core.exceptions.MultipleEntitiesFoundException;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HCPartyUtil {

    /**
     * Get a list of cd nodes where the S attribute equals the {@code cdHcpartYschemesSchemeFilter}
     * @param hcpartyType
     * @param cdHcpartYschemesSchemeFilter
     * @return
     */
    public List<CDHCPARTY> getCDHcParties(HcpartyType hcpartyType, CDHCPARTYschemes cdHcpartYschemesSchemeFilter) {

        List<CDHCPARTY> cdHcParties = new ArrayList<>();

        if (hcpartyType == null || hcpartyType.getCds() == null) {
            return cdHcParties;
        }

        for (CDHCPARTY cdhcparty : hcpartyType.getCds()) {

            if (StringUtils.equalsIgnoreCase(cdhcparty.getS().value(), cdHcpartYschemesSchemeFilter.value())) {
                cdHcParties.add(cdhcparty);
            }

        }

        return cdHcParties;

    }

    /**
     * Get a list of id nodes where the S attribute equals the {@code idhcpartYschemesFilter}
     * @param hcpartyType
     * @param idhcpartYschemesFilter
     * @return
     */
    public List<IDHCPARTY> getIDHcParties(HcpartyType hcpartyType, IDHCPARTYschemes idhcpartYschemesFilter) {

        List<IDHCPARTY> idhcparties = new ArrayList<>();

        if (hcpartyType == null || hcpartyType.getIds() == null) {
            return idhcparties;
        }

        for (IDHCPARTY idhcparty : hcpartyType.getIds()) {

            if (StringUtils.equalsIgnoreCase(idhcparty.getS().value(), idhcpartYschemesFilter.value())) {
                idhcparties.add(idhcparty);
            }

        }

        return idhcparties;

    }

    public CDHCPARTY getCDHcParty(HcpartyType hcpartyType, CDHCPARTYschemes cdHcpartYschemesSchemeFilter) {

        List<CDHCPARTY> cdHcParties = getCDHcParties(hcpartyType, cdHcpartYschemesSchemeFilter);

        if (CollectionUtils.isEmpty(cdHcParties)) {
            throw new DataNotFoundException("No content found with S: " + cdHcParties);
        }

        if (CollectionUtils.size(cdHcParties) > 1) {
            throw new MultipleEntitiesFoundException("Multiple contents found with S: " + cdHcpartYschemesSchemeFilter);
        }

        return cdHcParties.get(0);

    }

    public static HcpartyType findHubHcParty(List<HcpartyType> hcpartyTypes) {

        if (CollectionsUtil.emptyOrNull(hcpartyTypes)) {
            return null;
        }

        for (HcpartyType hcpartyType : hcpartyTypes) {
            if (isHubHcParty(hcpartyType)) {
                return hcpartyType;
            }
        }

        return null;

    }

    public static HcpartyType findFirstNonHubHcParty(List<HcpartyType> hcpartyTypes) {

        if (CollectionsUtil.emptyOrNull(hcpartyTypes)) {
            return null;
        }

        for (HcpartyType hcpartyType : hcpartyTypes) {
            if (!isHubHcParty(hcpartyType)) {
                return hcpartyType;
            }
        }

        return null;

    }

    public static boolean isHubHcParty(HcpartyType hcpartyType) {

        List<CDHCPARTY> cds = hcpartyType.getCds();

        if (CollectionsUtil.emptyOrNull(cds)) {
            return false;
        }


        for (CDHCPARTY cd : cds) {
            if (CDHCPARTYschemes.CD_HCPARTY.equals(cd.getS()) && StringUtils.equalsIgnoreCase(CDHCPARTYvalues.HUB.value(), (cd.getValue()))) {
                return true;
            }
        }

        return false;

    }

    public static boolean isApplicationHcParty(HcpartyType hcpartyType) {

        List<CDHCPARTY> cds = hcpartyType.getCds();

        if (CollectionsUtil.emptyOrNull(cds)) {
            return false;
        }


        for (CDHCPARTY cd : cds) {
            if (CDHCPARTYschemes.CD_HCPARTY.equals(cd.getS()) && StringUtils.equalsIgnoreCase(CDHCPARTYvalues.APPLICATION.value(), (cd.getValue()))) {
                return true;
            }
        }

        return false;

    }

    public static void removeHubHcParty(List<HcpartyType> hcpartyTypes) {

        if (CollectionsUtil.emptyOrNull(hcpartyTypes)) {
            return;
        }

        Iterator<HcpartyType> iterator = hcpartyTypes.iterator();
        while (iterator.hasNext()) {
            HcpartyType hcpartyType = iterator.next();
            if (isHubHcParty(hcpartyType)) {
                iterator.remove();
            }
        }

    }


}
