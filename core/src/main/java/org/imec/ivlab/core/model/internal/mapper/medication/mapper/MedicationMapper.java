package org.imec.ivlab.core.model.internal.mapper.medication.mapper;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDADMINISTRATIONUNIT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDDRUGCNK;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDINNCLUSTER;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMMSvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMvalues;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.AdministrationquantityType;
import be.fgov.ehealth.standards.kmehr.schema.v1.AdministrationunitType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ContentType;
import be.fgov.ehealth.standards.kmehr.schema.v1.DayperiodType;
import be.fgov.ehealth.standards.kmehr.schema.v1.DurationType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType.Posology.Takes;
import be.fgov.ehealth.standards.kmehr.schema.v1.MedicinalProductType;
import be.fgov.ehealth.standards.kmehr.schema.v1.RouteType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.exceptions.DataNotFoundException;
import org.imec.ivlab.core.kmehr.mapper.KmehrMapper;
import org.imec.ivlab.core.kmehr.model.FrequencyCode;
import org.imec.ivlab.core.kmehr.model.localid.LocalId;
import org.imec.ivlab.core.kmehr.model.localid.LocalIdParser;
import org.imec.ivlab.core.kmehr.model.util.CompoundPrescriptionUtil;
import org.imec.ivlab.core.kmehr.model.util.ContentUtil;
import org.imec.ivlab.core.kmehr.model.util.IDKmehrUtil;
import org.imec.ivlab.core.kmehr.model.util.ItemUtil;
import org.imec.ivlab.core.kmehr.model.util.RegimenUtil;
import org.imec.ivlab.core.kmehr.model.util.TextTypeUtil;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.internal.mapper.MappingResult;
import org.imec.ivlab.core.model.internal.mapper.medication.Dayperiod;
import org.imec.ivlab.core.model.internal.mapper.medication.Duration;
import org.imec.ivlab.core.model.internal.mapper.medication.Identifier;
import org.imec.ivlab.core.model.internal.mapper.medication.IndividualDayperiod;
import org.imec.ivlab.core.model.internal.mapper.medication.MedicationEntry;
import org.imec.ivlab.core.model.internal.mapper.medication.MedicationEntryBasic;
import org.imec.ivlab.core.model.internal.mapper.medication.Posology;
import org.imec.ivlab.core.model.internal.mapper.medication.Regimen;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenDate;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenDaynumber;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenDayperiod;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenEntry;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenTime;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenWeekday;
import org.imec.ivlab.core.model.internal.mapper.medication.Route;
import org.imec.ivlab.core.model.internal.mapper.medication.Suspension;
import org.imec.ivlab.core.model.internal.mapper.medication.TimeUnit;
import org.imec.ivlab.core.model.internal.mapper.medication.Weekday;
import org.imec.ivlab.core.model.internal.parser.sumehr.MedicationEntrySumehr;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.DateUtils;


public class MedicationMapper {

    private final static Logger LOG = Logger.getLogger(MedicationMapper.class);


    public static MedicationEntry transactionToMedicationEntry(TransactionType medicationTransaction) {
        return transactionToMedicationEntry(medicationTransaction, new ArrayList<>());
    }

    public static <T extends MedicationEntryBasic> T itemTypeToMedicationEntry(ItemType medicationItem, T medicationEntry) {

        medicationEntry.setIdentifier(getMedicationIdentifier(medicationItem.getContents(), medicationItem.getTexts()));

        medicationEntry.setCompoundPrescription(getCompoundPrescription(medicationItem.getContents()));


        if (medicationItem.getInstructionforpatient() != null) {
            medicationEntry.setInstructionForPatient(medicationItem.getInstructionforpatient().getValue());
        }

        if (medicationItem.getInstructionforoverdosing() != null) {
            medicationEntry.setInstructionForOverdosing(medicationItem.getInstructionforoverdosing().getValue());
        }

        if (medicationItem.getRoute() != null) {
            medicationEntry.setRoute(Route.fromCode(getRouteValue(medicationItem.getRoute())));
        }

        if (medicationItem.getBeginmoment() != null) {
            medicationEntry.setBeginDate(DateUtils.toLocalDate(medicationItem.getBeginmoment().getDate()));
        }

        if (medicationItem.getEndmoment() != null) {
            medicationEntry.setEndDate(DateUtils.toLocalDate(medicationItem.getEndmoment().getDate()));
        }

        if (medicationItem.getFrequency() != null && medicationItem.getFrequency().getPeriodicity() != null && medicationItem.getFrequency().getPeriodicity().getCd() != null) {
            medicationEntry.setFrequencyCode(FrequencyCode.fromValue(medicationItem.getFrequency().getPeriodicity().getCd().getValue()));
        }

        Posology posology = mapPosology(medicationItem);
        Regimen regimen = mapRegimen(medicationItem);
        if (medicationEntry instanceof MedicationEntrySumehr) {
            ((MedicationEntrySumehr) medicationEntry).setPosology(posology);
            ((MedicationEntrySumehr) medicationEntry).setRegimen(regimen);
        } else if (medicationEntry instanceof MedicationEntry) {
            if (regimen != null) {
                ((MedicationEntry) medicationEntry).setPosologyOrRegimen(regimen);
            } else if (posology != null) {
                ((MedicationEntry) medicationEntry).setPosologyOrRegimen(posology);
            }
        }

        if (medicationItem.getTemporality() != null && medicationItem.getTemporality().getCd() != null) {
            medicationEntry.setTemporality(medicationItem.getTemporality().getCd().getValue());
        }
        medicationEntry.setDayperiods(getDayperiods(medicationItem));

        medicationEntry.setDuration(convertDuration(medicationItem.getDuration()));

        return medicationEntry;
    }


    public static MedicationEntry transactionToMedicationEntry(TransactionType medicationTransaction, List<TransactionType> suspensionTransactions) {


        ItemType medicationItem = TransactionUtil.getItem(medicationTransaction, CDITEMvalues.MEDICATION);
        MedicationEntry entry = itemTypeToMedicationEntry(medicationItem, new MedicationEntry());

        List<ItemType> healthCareElements = TransactionUtil.getItems(medicationTransaction, CDITEMvalues.HEALTHCAREELEMENT);

        try {
            ItemType beginconditionItem = ItemUtil.getItem(healthCareElements, CDITEMMSvalues.BEGINCONDITION);
            entry.setBeginCondition(getTextContents(beginconditionItem));
        } catch (DataNotFoundException notimportantException) {
        }
        try {
            ItemType endconditionItem = ItemUtil.getItem(healthCareElements, CDITEMMSvalues.ENDCONDITION);
            entry.setEndCondition(getTextContents(endconditionItem));
        } catch (DataNotFoundException notimportantException) {
        }
        try {
            ItemType medicationuseItem = ItemUtil.getItem(healthCareElements, CDITEMMSvalues.MEDICATIONUSE);
            entry.setMedicationUse(getTextContents(medicationuseItem));
        } catch (DataNotFoundException notimportantException) {
        }
        if (medicationTransaction.getAuthor() != null) {
            entry.setAuthors(medicationTransaction.getAuthor().getHcparties());
        }

        entry.setSuspensions(getSuspensions(suspensionTransactions));
        entry.setCreatedDate(DateUtils.toLocalDate(medicationTransaction.getDate()));
        entry.setCreatedTime(DateUtils.toLocalTime(medicationTransaction.getTime()));
        entry.setLocalId(getLocalId(medicationTransaction));

        return entry;

    }

    private static LocalId getLocalId(TransactionType medicationTransaction) {
        IDKmehrUtil idKmehrUtil = new IDKmehrUtil();
        List<IDKMEHR> idKmehrs = idKmehrUtil.getIDKmehrs(medicationTransaction.getIds(), IDKMEHRschemes.LOCAL);
        List<LocalId> localIds = LocalIdParser.parseLocalIds(idKmehrs);
        if (CollectionsUtil.emptyOrNull(localIds)) {
            return null;
        }
        if (localIds.size() > 1) {
            LOG.warn("Found " + localIds.size() + "local id's for transaction. Will select the first one from the list.");
        }
        return localIds.get(0);
    }


    public static Duration convertDuration(DurationType durationType) {

        if (durationType != null && durationType.getDecimal() != null && durationType.getUnit() != null && durationType.getUnit().getCd() != null) {
            Duration duration = new Duration();
            duration.setValue(MappingResult.successful(BigInteger.valueOf(durationType.getDecimal().longValue())));

            String timeunitValue = durationType.getUnit().getCd().getValue();
            try {
                TimeUnit timeUnit = TimeUnit.fromValue(timeunitValue);
                duration.setUnit(MappingResult.successful(timeUnit));
            } catch (IllegalArgumentException e) {
                duration.setUnit(MappingResult.unsuccessful(timeunitValue));
            }
            return duration;
        } else {
            return null;
        }

    }

    private static List<IndividualDayperiod> getDayperiods(ItemType medicationItem) {
        if (medicationItem.getDayperiods() == null) {
            return null;
        }

        List<IndividualDayperiod> dayperiods = new ArrayList<>();
        for (DayperiodType dayperiodType : medicationItem.getDayperiods()) {
            if (dayperiodType == null || dayperiodType.getCd() == null) {
                continue;
            }
            IndividualDayperiod individualDayperiod = new IndividualDayperiod(Dayperiod.fromValue(dayperiodType.getCd().getValue().value()));
            dayperiods.add(individualDayperiod);
        }

        return dayperiods;
    }

    private static Regimen mapRegimen(ItemType medicationItem) {
        ItemType.Regimen regimen = medicationItem.getRegimen();

        if (regimen == null) {
            return null;
        }

        List<org.imec.ivlab.core.kmehr.model.RegimenEntry> regimenEntries = RegimenUtil.getRegimenEntries(regimen);

        if (CollectionsUtil.emptyOrNull(regimenEntries)) {
            return null;
        }

        Regimen regimenOut = new Regimen();
        regimenOut.setEntries(new ArrayList<>());

        for (org.imec.ivlab.core.kmehr.model.RegimenEntry regimenEntry : regimenEntries) {

            RegimenEntry regimenEntryOut = new RegimenEntry();

            if (regimenEntry.getDate() != null) {
                RegimenDate date = new RegimenDate();
                date.setDate(DateUtils.toLocalDate(regimenEntry.getDate()));
                regimenEntryOut.setDaynumberOrDateOrWeekday(date);
            } else if (regimenEntry.getDayNumber() != null) {
                RegimenDaynumber daynumber = new RegimenDaynumber();
                daynumber.setNumber(regimenEntry.getDayNumber().intValue());
                regimenEntryOut.setDaynumberOrDateOrWeekday(daynumber);
            } else if(regimenEntry.getWeekday() != null) {
                RegimenWeekday regimenWeekday = new RegimenWeekday();
                regimenWeekday.setWeekday(Weekday.fromValue(regimenEntry.getWeekday().getCd().getValue().value()));
                regimenEntryOut.setDaynumberOrDateOrWeekday(regimenWeekday);
            }

            if (regimenEntry.getDaytime() != null && regimenEntry.getDaytime().getDayperiod() != null) {
                RegimenDayperiod regimenDayperiod = new RegimenDayperiod();
                regimenDayperiod.setDayperiod(Dayperiod.fromValue(regimenEntry.getDaytime().getDayperiod().getCd().getValue().value()));
                regimenEntryOut.setDayperiodOrTime(regimenDayperiod);
            } else if (regimenEntry.getDaytime() != null && regimenEntry.getDaytime().getTime() != null) {
                RegimenTime regimenTime = new RegimenTime();
                regimenTime.setTime(LocalTime.of(regimenEntry.getDaytime().getTime().get(Calendar.HOUR_OF_DAY), regimenEntry.getDaytime().getTime().get(Calendar.MINUTE), regimenEntry.getDaytime().getTime().get(Calendar.SECOND)));
                regimenEntryOut.setDayperiodOrTime(regimenTime);
            }

            regimenEntryOut.setQuantity(regimenEntry.getQuantity().getDecimal());

            regimenOut.getEntries().add(regimenEntryOut);

        }

        AdministrationquantityType quantity = regimenEntries.get(0).getQuantity();
        if (quantity.getUnit() != null) {
            regimenOut.setAdministrationUnit(regimenEntries.get(0).getQuantity().getUnit().getCd().getValue());
        }

        return regimenOut;
    }

    private static Posology mapPosology(ItemType medicationItem) {
        Posology posologyOut = new Posology();

        ItemType.Posology posologyIn = medicationItem.getPosology();
        if (posologyIn == null) {
            return null;
        }
        posologyOut.setText(Optional.ofNullable(posologyIn.getText()).map(TextType::getValue).orElse(null));

        posologyOut.setPosologyHigh(posologyIn.getHigh());
        posologyOut.setPosologyLow(posologyIn.getLow());

        posologyOut.setAdministrationUnit(Optional
            .ofNullable(posologyIn.getUnit())
            .map(AdministrationunitType::getCd)
            .map(CDADMINISTRATIONUNIT::getValue)
            .orElse(null));

        posologyOut.setTakesHigh(Optional.ofNullable(posologyIn.getTakes()).map(Takes::getHigh).orElse(null));
        posologyOut.setTakesLow(Optional.ofNullable(posologyIn.getTakes()).map(Takes::getLow).orElse(null));

        return posologyOut;
    }

    private static List<Suspension> getSuspensions(List<TransactionType> suspensionTransactions) {


        ArrayList<Suspension> suspensions = new ArrayList<>();

        if (CollectionsUtil.emptyOrNull(suspensionTransactions)) {
            return suspensions;
        }

        for (TransactionType suspensionTransaction : suspensionTransactions) {

            Suspension suspension = new Suspension();

            try {
                ItemType transansactionReasonItem = TransactionUtil.getItem(suspensionTransaction, CDITEMvalues.TRANSACTIONREASON);
                String suspensionReason = getTextContents(transansactionReasonItem);
                suspension.setReason(suspensionReason);
            } catch (DataNotFoundException notimportantexception) {

            }

            ItemType medicationItem = TransactionUtil.getItem(suspensionTransaction, CDITEMvalues.MEDICATION);

            if (suspensionTransaction.getAuthor() != null) {
                suspension.setAuthors(suspensionTransaction.getAuthor().getHcparties());
            }

            suspension.setCreatedDate(DateUtils.toLocalDate(suspensionTransaction.getDate()));
            if (suspensionTransaction.getTime() != null) {
                suspension.setCreatedTime(DateUtils.toLocalTime(suspensionTransaction.getTime()));
            }

            if (medicationItem.getBeginmoment() != null) {
                suspension.setBeginDate(DateUtils.toLocalDate(medicationItem.getBeginmoment().getDate()));
            }

            if (medicationItem.getEndmoment() != null) {
                suspension.setEndDate(DateUtils.toLocalDate(medicationItem.getEndmoment().getDate()));
            }

            suspension.setLifecycle(KmehrMapper.toLifeCycleValues(medicationItem.getLifecycle()));


            suspension.setDuration(convertDuration(medicationItem.getDuration()));

            suspensions.add(suspension);

        }


        return suspensions;

    }

    private static String getRouteValue(RouteType routeType) {

        if (routeType != null && routeType.getCd() != null) {
            return routeType.getCd().getValue();
        }

        return null;

    }

    private static String getTextContents(ItemType itemType) {

        String textContent = "";

        List<ContentType> textContentTypes = ItemUtil.getTextContentTypes(itemType);
        if (textContentTypes == null) {
            return textContent;
        }

        for (ContentType contentType : textContentTypes) {
            if (contentType == null) {
                continue;
            }
            for (TextType textType : contentType.getTexts()) {
                textContent += textType.getValue() + System.lineSeparator();
            }
        }

        textContent = StringUtils.chop(textContent);

        return textContent;

    }

    private static String getName(Object object) {

        if (object == null) {
            return null;
        }

        if (object instanceof ElementNSImpl) {
            ElementNSImpl item = (ElementNSImpl) object;
            if (item.getFirstChild() != null) {
                return item.getFirstChild().getNodeValue();
            }
        }

        return (String) object;

    }

    private static String getCNKIdentifier(List<CDDRUGCNK> cddrugcnks) {

        if (CollectionsUtil.emptyOrNull(cddrugcnks)) {
            return null;
        }

        for (CDDRUGCNK cddrugcnk : cddrugcnks) {
            if (cddrugcnk != null && cddrugcnk.getValue() != null) {
                return cddrugcnk.getValue();
            }
        }

        return null;

    }

    private static String getINNIdentifier(List<CDINNCLUSTER> cdinnclusters) {

        if (CollectionsUtil.emptyOrNull(cdinnclusters)) {
            return null;
        }

        for (CDINNCLUSTER cdinncluster : cdinnclusters) {
            if (cdinncluster.getValue() != null) {
                return cdinncluster.getValue();
            }
        }

        return null;

    }


    private static Identifier findMedicationName(ContentType contentType) {

        if (contentType == null) {
            return null;
        }

        Identifier identifier = new Identifier();

        if (contentType.getMedicinalproduct() != null) {
            MedicinalProductType medicinalproduct = contentType.getMedicinalproduct();

            identifier.setId(getCNKIdentifier(medicinalproduct.getDeliveredcds()));
            if (identifier.getId() == null) {
                identifier.setId(getCNKIdentifier(medicinalproduct.getIntendedcds()));
            }

            String deliveredName = getName(medicinalproduct.getDeliveredname());
            if (deliveredName != null) {
                identifier.setName(deliveredName);
            } else {
                if (medicinalproduct.getIntendedname() != null) {
                    identifier.setName(medicinalproduct.getIntendedname());
                }
            }
            return identifier;
        } else if (contentType.getSubstanceproduct() != null) {
            ContentType.Substanceproduct substanceproduct = contentType.getSubstanceproduct();

            identifier.setId(getCNKIdentifier(Arrays.asList(substanceproduct.getDeliveredcd())));
            if (identifier.getId() == null) {
                identifier.setId(getINNIdentifier(Arrays.asList(substanceproduct.getIntendedcd())));
            }

            String deliveredName = getName(substanceproduct.getDeliveredname());
            if (deliveredName != null) {
                identifier.setName(deliveredName);
            } else {
                String intendedName = getName(substanceproduct.getIntendedname());
                if (intendedName != null) {
                    identifier.setName(intendedName);
                }
            }
            return identifier;
        }

        return null;

    }

    private static String findMedication(ContentType contentType) {

        if (contentType == null) {
            return null;
        }

        if (contentType.getMedicinalproduct() != null) {
            MedicinalProductType medicinalproduct = contentType.getMedicinalproduct();
            String deliveredName = getName(medicinalproduct.getDeliveredname());
            if (deliveredName != null) {
                return deliveredName;
            } else {
                if (medicinalproduct.getIntendedname() != null) {
                    return medicinalproduct.getIntendedname();
                }
            }
        }

        if (contentType.getSubstanceproduct() != null) {
            ContentType.Substanceproduct substanceproduct = contentType.getSubstanceproduct();
            String deliveredName = getName(substanceproduct.getDeliveredname());
            if (deliveredName != null) {
                return deliveredName;
            } else {
                String intendedName = getName(substanceproduct.getIntendedname());
                if (intendedName != null) {
                    return intendedName;
                }
            }

        }

        return null;

    }

    private static String getCompoundPrescription(List<ContentType> contents) {

        if (CollectionsUtil.emptyOrNull(contents)) {
            return null;
        }

        CompoundPrescriptionUtil compoundPrescriptionUtil = new CompoundPrescriptionUtil();

        for (ContentType contentType : contents) {
            if (contentType.getCompoundprescription() != null) {
                List<TextType> compoundText = compoundPrescriptionUtil.getMagistralText(contentType.getCompoundprescription());
                if (CollectionsUtil.notEmptyOrNull(compoundText)) {
                    return org.imec.ivlab.core.util.StringUtils.joinWith(" ", TextTypeUtil.toStrings(compoundText).toArray());
                }
            }
        }

        for (ContentType contentType : contents) {
            if (contentType.getCompoundprescription() != null) {
                List<String> compoundText = compoundPrescriptionUtil.getCompoundText(contentType.getCompoundprescription());
                if (CollectionsUtil.notEmptyOrNull(compoundText)) {
                    return org.imec.ivlab.core.util.StringUtils.joinWith(" ", compoundText.toArray());
                }
            }
        }

        return null;
    }

    public static Identifier getMedicationIdentifier(List<ContentType> contents, List<TextType> texts) {

        if (contents != null) {
            for (ContentType content : contents) {
                Identifier identifier = findMedicationName(content);
                if (identifier != null) {
                    return identifier;
                }
            }
        }

        String ean = getEAN(contents);
        if (ean != null) {
            return new Identifier(ean, null);
        }

        if (CollectionsUtil.notEmptyOrNull(texts) && texts.get(0) != null) {
            return new Identifier(null, texts.get(0).getValue());
        }

        return null;

    }

    private static String getEAN(List<ContentType> contentTypes) {

        if (contentTypes == null) {
            return null;
        }

        ContentUtil contentUtil = new ContentUtil();

        StringBuilder stringBuilder = new StringBuilder();
        for (ContentType contentType : contentTypes) {
            List<CDCONTENT> cdContents = contentUtil.getCDContents(contentType, CDCONTENTschemes.CD_EAN);
            if (cdContents != null) {
                for (CDCONTENT cdContent : cdContents) {
                    stringBuilder.append(cdContent.getValue() + ",");
                }
            }
        }

        if (stringBuilder.length() > 0) {
            return StringUtils.removeEnd(stringBuilder.toString(), ",");
        }

        return null;

    }

}
