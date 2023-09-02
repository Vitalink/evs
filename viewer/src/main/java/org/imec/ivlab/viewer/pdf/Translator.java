package org.imec.ivlab.viewer.pdf;

import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getCommentTitleUnderlineFont;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getMedicationUriFont;
import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getTableDefaultFont;
import static org.imec.ivlab.viewer.pdf.MSWriter.TOO_LARGE_TEXT;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDSEXvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTEMPORALITYvalues;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY;
import be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import com.itextpdf.text.Chunk;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.RangeChecker;
import org.imec.ivlab.core.kmehr.model.AdministrationUnit;
import org.imec.ivlab.core.kmehr.model.Frequency;
import org.imec.ivlab.core.kmehr.model.FrequencyCode;
import org.imec.ivlab.core.kmehr.model.localid.GenericLocalId;
import org.imec.ivlab.core.kmehr.model.localid.LocalId;
import org.imec.ivlab.core.kmehr.model.localid.URI;
import org.imec.ivlab.core.kmehr.model.util.HCPartyUtil;
import org.imec.ivlab.core.model.internal.mapper.medication.Dayperiod;
import org.imec.ivlab.core.model.internal.mapper.medication.Duration;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenDate;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenDaynumber;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenEntry;
import org.imec.ivlab.core.model.internal.mapper.medication.RegimenWeekday;
import org.imec.ivlab.core.model.internal.mapper.medication.Route;
import org.imec.ivlab.core.model.internal.mapper.medication.TimeUnit;
import org.imec.ivlab.core.model.internal.mapper.medication.Weekday;
import org.imec.ivlab.core.util.CollectionsUtil;

public class Translator {

    protected static String translateFrequency(FrequencyCode frequencyCode) {

        if (frequencyCode == null) {
            return "";
        }

        if (frequencyCode.isSupportedByVitalink()) {
            return frequencyCode.getTranslation();
        } else {
            return "Non-Vitalink code found (" + frequencyCode.getValue() + ", " + frequencyCode.getTranslation() + ")";
        }

    }

    protected static String translateRoute(Route route) {
        if (route == null) {
            return "-";
        }

        if (StringUtils.isNotBlank(route.getPatientTranslation())) {
            return route.getPatientTranslation();
        } else {
            return "Ongeldige route (" + route.getValue() + ")";
        }

    }

    protected static String translateAdministrationUnit(String administrationUnitString) {

        if (administrationUnitString == null) {
            return "-";
        }

        try {
            AdministrationUnit administrationUnit = AdministrationUnit.fromValue(administrationUnitString);

            if (administrationUnit.isSupportedByVitalink()) {
                return administrationUnit.getPatientTranslation();
            } else {
                return "Non-Vitalink code found (" + administrationUnitString + ", " + administrationUnit.getDescriptionDutch() + ")";
            }

        } catch (IllegalArgumentException e) {
            return "Non-Kmehr code found (" + administrationUnitString + ")";
        }

    }

    protected static String translateRegimenRepetition(RegimenEntry regimenEntry, FrequencyCode frequencyCode) {

        if (regimenEntry == null) {
            return "";
        }

        if (regimenEntry.getDaynumberOrDateOrWeekday() instanceof RegimenDaynumber) {
            RegimenDaynumber regimenDaynumber = (RegimenDaynumber) regimenEntry.getDaynumberOrDateOrWeekday();
            return " - dag " + regimenDaynumber.getNumber();
        } else if (regimenEntry.getDaynumberOrDateOrWeekday() instanceof RegimenWeekday) {
            RegimenWeekday regimenWeekday = (RegimenWeekday) regimenEntry.getDaynumberOrDateOrWeekday();
            return " - " + translateWeekday(regimenWeekday.getWeekday());
        } else if (regimenEntry.getDaynumberOrDateOrWeekday() instanceof RegimenDate) {
            RegimenDate regimenDate = (RegimenDate) regimenEntry.getDaynumberOrDateOrWeekday();
            if (frequencyCode != null && frequencyCode.getFrequency().equals(Frequency.MONTH)) {
                String pattern = DateTimeFormat.forPattern("d").print(regimenDate.getDate());
                return " - " + pattern + "e";
            } else if (frequencyCode != null && frequencyCode.getFrequency().equals(Frequency.YEAR)) {
                String pattern = DateTimeFormat.forPattern("d MMMM").print(regimenDate.getDate());
                return " - " + pattern;
            } else {
                String pattern = DateTimeFormat.forPattern("dd/MM/yyyy").print(regimenDate.getDate());
                return " - " + pattern;
            }
        }

        return "";

    }

    protected static List<Chunk> toLocalIdChunks(LocalId localId) {

        List<Chunk> uriChunks = new ArrayList<>();

        if (localId == null) {
            return uriChunks;
        }

        if (localId instanceof GenericLocalId) {
            uriChunks.add(new Chunk(String.valueOf(localId.getValue()), getMedicationUriFont()));
        } else if (localId instanceof URI) {
            URI uri = (URI) localId;
            uriChunks.add(new Chunk(String.valueOf(uri.getTransactionId()), getMedicationUriFont()));
            uriChunks.add(new Chunk(" / ", getMedicationUriFont()));
            uriChunks.add(new Chunk(String.valueOf(uri.getTransactionVersion()), getMedicationUriFont()));
        }

        return uriChunks;

    }

    protected static List<Chunk> toCommentHeaderAndValueChunk(String header, String value) {

        List<Chunk> uriChunks = new ArrayList<>();

        if (value == null) {
            return uriChunks;
        }

        Chunk headerChunk = new Chunk(header, getCommentTitleUnderlineFont());
        uriChunks.add(headerChunk);
        Chunk valueChunk = new Chunk(" " + value, getTableDefaultFont());
        uriChunks.add(valueChunk);

        return uriChunks;

    }


    protected static List<Chunk> toHCPartyChunks(List<HcpartyType> hcpartyTypes) {

        List<Chunk> hcPartyChunks = new ArrayList<>();

        if (hcpartyTypes == null) {
            return hcPartyChunks;
        }

        for (HcpartyType hcpartyType : hcpartyTypes) {

            StringBuffer stringBuffer = new StringBuffer();

            hcPartyChunks.add(new Chunk(System.lineSeparator(), MSTableFormatter.getMedicationAuthorFont()));

            String authorName = StringUtils.joinWith(" ", hcpartyType.getFirstname(), hcpartyType.getFamilyname(), hcpartyType.getName());
            hcPartyChunks.add(new Chunk(authorName, MSTableFormatter.getMedicationAuthorBoldFont()));
            hcPartyChunks.add(new Chunk(System.lineSeparator(), MSTableFormatter.getMedicationAuthorFont()));

            String hcPartyDescriptions = stringBuffer.append(StringUtils.joinWith(",", getHCPartyDescriptions(hcpartyType).toArray())).toString();
            hcPartyChunks.add(new Chunk(hcPartyDescriptions, MSTableFormatter.getMedicationAuthorFont()));

            List<String> hcPartyIds = getHCPartyIds(hcpartyType);
            if (CollectionsUtil.notEmptyOrNull(hcPartyIds)) {
                stringBuffer = new StringBuffer();
                stringBuffer.append(" (");
                stringBuffer.append(StringUtils.joinWith(",", hcPartyIds.toArray()));
                stringBuffer.append(")");
                hcPartyChunks.add(new Chunk(stringBuffer.toString(), MSTableFormatter.getMedicationAuthorFont()));
            }

            hcPartyChunks.add(new Chunk(System.lineSeparator(), MSTableFormatter.getMedicationAuthorFont()));

        }

        return hcPartyChunks;

    }

    private static List<String> getHCPartyIds(HcpartyType hcpartyType) {

        List<IDHCPARTY> idHcParties = HCPartyUtil.getIDHcParties(hcpartyType, IDHCPARTYschemes.ID_HCPARTY);

        List<String> hcPartyIds = new ArrayList<>();

        for (IDHCPARTY idhcparty : idHcParties) {
            hcPartyIds.add(idhcparty.getValue());
        }

        return hcPartyIds;

    }

    private static List<String> getHCPartyDescriptions(HcpartyType hcpartyType) {

        HCPartyUtil hcPartyUtil = new HCPartyUtil();

        List<CDHCPARTY> cdhcparties = hcPartyUtil.getCDHcParties(hcpartyType, CDHCPARTYschemes.CD_HCPARTY);

        List<String> hcPartyCds = new ArrayList<>();
        if (cdhcparties == null) {
            return null;
        }

        for (CDHCPARTY cdhcparty : cdhcparties) {
            try {
                CDHCPARTYvalues cdhcpartYvalues = CDHCPARTYvalues.fromValue(cdhcparty.getValue());
                hcPartyCds.add(translateCdHcPartyValue(cdhcpartYvalues));
            } catch (IllegalArgumentException e) {
                hcPartyCds.add(cdhcparty.getValue());
            }
        }

        return hcPartyCds;

    }

    protected static String translateWeekday(Weekday weekday) {
        switch (weekday) {
            case MONDAY: return "ma";
            case TUESDAY: return "di";
            case WEDNESDAY: return "wo";
            case THURSDAY: return "do";
            case FRIDAY: return "vr";
            case SATURDAY: return "za";
            case SUNDAY: return "zo";
            default: throw new RuntimeException("No translation for weekday: " + weekday);
        }
    }

    protected static String conjugateTimeunit(TimeUnit timeUnit, BigInteger number) {

        if (number == null || number.compareTo(BigInteger.ONE) == 0) {
            return timeUnit.getValueForSingle();
        } else {
            return timeUnit.getValueForMultiple();
        }

    }

    protected static String translateDayperiod(Dayperiod dayperiod) {

        switch (dayperiod){
            case AFTERBREAKFAST: return "na het ontbijt";
            case AFTERDINNER: return "na het avondeten";
            case AFTERLUNCH: return "na het middageten";
            case AFTERMEAL: return "na de maaltijd";
            case AFTERNOON: return "in de namiddag";
            case BEFOREBREAKFAST: return "voor het ontbijt";
            case BEFOREDINNER: return "voor het avondeten";
            case BEFORELUNCH: return "voor het middageten";
            case BETWEENBREAKFASTANDLUNCH: return "tussen het ontbijt en het middageten";
            case BETWEENDINNERANDSLEEP: return "tussen het avondeten en het slapen";
            case BETWEENLUNCHANDDINNER: return "tussen het middageten en het avondeten";
            case BETWEENMEALS: return "tussen de maaltijden";
            case EVENING: return "'s avonds";
            case MORNING: return "'s ochtends";
            case NIGHT: return "'s nachts";
            case THEHOUROFSLEEP: return "voor het slapengaan";
            case DURINGBREAKFAST: return "tijdens het ontbijt";
            case DURINGLUNCH: return "tijdens het middageten";
            case DURINGDINNER: return "tijdens het avondeten";

            default: throw new RuntimeException("No translation for dayperiod: " + dayperiod);
        }

    }

    protected static String translateCdHcPartyValue(CDHCPARTYvalues cdhcpartYvalues) {

        if (cdhcpartYvalues == null) {
            return null;
        }

        if (cdhcpartYvalues.value() == null) {
            return cdhcpartYvalues.name();
        }

        switch (cdhcpartYvalues.value()) {

            case "deptanatomopathology": return "anatomo-pathologie";
            case "deptanesthesiology": return "anesthesiologie";
            case "deptbacteriology": return "bacteriologie";
            case "deptcardiology": return "cardiologie";
            case "deptdermatology": return "dermatologie";
            case "deptdietetics": return "diëtetiek";
            case "deptemergency": return "spoeddiensten";
            case "deptgastroenterology": return "gastro-enterologie";
            case "deptgeneralpractice": return "algemene geneeskunde";
            case "deptgenetics": return "genetica";
            case "deptgeriatry": return "geriatrie";
            case "deptgynecology": return "gynaecologie";
            case "depthematology": return "hematologie";
            case "deptintensivecare": return "intensieve zorgen";
            case "deptkinesitherapy": return "kinesitherapie";
            case "deptlaboratory": return "Laboratoriumdienst of -afdeling binnen een organisatie";
            case "deptmedicine": return "interne geneeskunde";
            case "deptmolecularbiology": return "moleculaire biologie";
            case "deptneonatalogy": return "neonatologie";
            case "deptnephrology": return "nefrologie";
            case "deptneurology": return "neurologie";
            case "deptnte": return "oto-rhino-laryngologie";
            case "deptnuclear": return "nucleaire geneeskunde";
            case "deptoncology": return "oncologie";
            case "deptophtalmology": return "oftalmologie";
            case "deptpediatry": return "pediatrie";
            case "deptpharmacy": return "Apotheekdienst of -afdeling binnen een organisatie";
            case "deptphysiotherapy": return "fysiotherapie";
            case "deptpneumology": return "pulmonologie";
            case "deptpsychiatry": return "psychiatrie";
            case "deptradiology": return "radiologie";
            case "deptradiotherapy": return "radiotherapie";
            case "deptrhumatology (deprecated)": return "reumatologie";
            case "deptstomatology": return "stomatologie";
            case "deptsurgery": return "chirurgie";
            case "depttoxicology": return "toxicologie";
            case "depturology": return "urologie";
            case "orghospital": return "ziekenhuis";
            case "orginsurance": return "verzekering";
            case "orglaboratory": return "Onafhankelijk laboratorium";
            case "orgpublichealth": return "openbare gezondheidsdienst";
            case "persbiologist": return "medisch laboratoriumtechnoloog";
            case "persdentist": return "tandarts";
            case "persnurse": return "verpleegkundige";
            case "persparamedical": return "paramedisch personeel";
            case "perspharmacist": return "apotheker";
            case "persphysician": return "arts";
            case "perssocialworker": return "maatschappelijk werker";
            case "perstechnician": return "Technicus medische beeldvorming";
            case "persadministrative": return "administratieve medewerker";
            case "persmidwife": return "vroedvrouw";
            case "application": return "softwaretoepassing";
            case "deptorthopedy": return "orthopedie";
            case "orgpractice": return "praktijk";
            case "orgpharmacy": return "Onafhankelijke apotheek";
            case "deptalgology": return "algologie";
            case "deptcardiacsurgery": return "hartchirurgie";
            case "depthandsurgery": return "handchirurgie";
            case "deptmaxillofacialsurgery": return "mond-, kaak- en aangezichtschirurgie";
            case "deptpediatricsurgery": return "kinderchirurgie";
            case "deptplasticandreparatorysurgery": return "plastische en reconstructieve chirurgie";
            case "deptthoracicsurgery": return "thoraxchirurgie";
            case "deptvascularsurgery": return "vasculaire chirurgie";
            case "deptvisceraldigestiveabdominalsurgery": return "digestieve/viscerale chirurgie/buikchirurgie";
            case "deptdentistry": return "tandheelkunde";
            case "deptdiabetology": return "diabetologie";
            case "deptendocrinology": return "endocrinologie/hormonenleer";
            case "deptoccupationaltherapy": return "ergotherapie";
            case "deptmajorburns": return "brandwondencentrum";
            case "deptinfectiousdisease": return "infectieziekte";
            case "deptspeechtherapy": return "logopedie";
            case "deptsportsmedecine": return "sportgeneeskunde";
            case "deptphysicalmedecine": return "fysische geneeskunde";
            case "depttropicalmedecine": return "tropische geneeskunde";
            case "deptneurosurgery": return "neurologie";
            case "deptnutritiondietetics": return "diëtetiek";
            case "deptobstetrics": return "ergotherapie";
            case "deptchildandadolescentpsychiatry": return "kinder- en jeugdpsychiatrie";
            case "deptpodiatry": return "podiatrie";
            case "deptpsychology": return "psychologie";
            case "deptrevalidation": return "revalidatie";
            case "deptsenology": return "senologie";
            case "deptsocialservice": return "sociale dienst";
            case "deptpediatricintensivecare": return "intensieve zorg voor kinderen";
            case "deptpalliativecare": return "palliatieve zorg";
            case "deptrheumatology": return "reumatologie";
            case "persambulancefirstaid": return "ambulance eerste hulp";
            case "persaudician": return "audicien";
            case "persaudiologist": return "audioloog";
            case "perscaregiver": return "mantelzorger";
            case "persdietician": return "dietist";
            case "perseducator": return "opvoeder";
            case "perslogopedist": return "logopedist";
            case "persoccupationaltherapist": return "ergotherapeut";
            case "persorthopedist (deprecated)": return "orthopedist";
            case "persorthoptist": return "orthoptist";
            case "persoptician": return "opticien";
            case "perspharmacyassistant": return "apothekersassistent";
            case "persphysiotherapist": return "kinesitherapeut";
            case "perspodologist": return "podologist";
            case "perspracticalnurse": return "verzorgende";
            case "perspsychologist": return "psycholoog";
            case "orgprimaryhealthcarecenter": return "medisch huis";
            case "guardpost": return "wachtpost";
            case "groupofphysicianssameaddress": return "groepering van artsen zelfde adres";
            case "groupofphysiciansdifferentaddress": return "Groepering van artsen verschillend adres";
            case "groupofnurses": return "Groepering van verplegers";
            case "certificateholder": return "Houder van certificaat";
            case "perstrussmaker": return "Bandagist";
            case "patient": return "Patient";
            case "persorthotist": return "Orthopedische technieker";
            case "perspatientdriver": return "Ziekenvervoerder (niet urgent)";
            case "orgprevention": return "Preventiedienst";
            case "perspharmacistclinicalbiologist": return "apotheker-klinisch bioloog";
            case "hub": return "hub";

            default: return cdhcpartYvalues.value();

        }


    }

    protected static String translateTemporality(CDTEMPORALITYvalues cdtemporalitYvalues) {
        switch (cdtemporalitYvalues) {
            case CHRONIC: return "Permanente geneesmiddelen";
            case ACUTE: return "Tijdelijke geneesmiddelen";
            case ONESHOT: return "Indien nodig";
            default: return cdtemporalitYvalues.value();
        }

    }

    protected static String translateLifecycle(CDLIFECYCLEvalues lifecycleStatus) {
        if (lifecycleStatus == null) {
            return "";
        }
        switch (lifecycleStatus) {
            case STOPPED: return "definitief";
            case SUSPENDED: return "tijdelijk";
            case ACTIVE: return "actief";
            case INACTIVE: return "inactief";
            default: return lifecycleStatus.value();
        }
    }

    protected static String translateSex(CDSEXvalues cdseXvalues) {
        if (cdseXvalues == null) {
            return "";
        }
        switch (cdseXvalues) {
            case MALE: return "mannelijk";
            case FEMALE: return "vrouwelijk";
            default: return cdseXvalues.value();
        }
    }

    protected static String translateQuantity(BigDecimal x){
        if (x != null && x.compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) >= 0) {
            return TOO_LARGE_TEXT;
        }
        return translateQuantity(x.doubleValue());
    }

    private static String translateQuantity(double x){
        if (x < 0){
            return "-" + translateQuantity(-x);
        }

        double fractions = x - Math.floor(x);
        if (fractions == 0) {
            return String.valueOf((int) x);
        }

        if (x > 1) {
            int roundedX = (int) x;
            double diff = x - roundedX;
            return roundedX + " + " + translateQuantity(diff);
        }

        double tolerance = 1.0E-6;
        double h1=1; double h2=0;
        double k1=0; double k2=1;
        double b = x;
        do {
            double a = Math.floor(b);
            double aux = h1; h1 = a*h1+h2; h2 = aux;
            aux = k1; k1 = a*k1+k2; k2 = aux;
            b = 1/(b-a);
        } while (Math.abs(x-h1/k1) > x*tolerance);


        return (int)h1 + "/" + (int)k1;

    }

    protected static String formatAsDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        }

        return DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").print(localDateTime);

    }

    protected static String formatAsTime(LocalTime localTime) {
        if (localTime == null) {
            return "";
        }

        return DateTimeFormat.forPattern("HH:mm:ss").print(localTime);

    }

    protected static String formatAsTime(DateTime localTime) {
        if (localTime == null) {
            return "";
        }

        return DateTimeFormat.forPattern("HH:mm:ss").print(localTime);
    }

    protected static String formatAsTime(java.time.LocalTime localTime) {
        if (localTime == null) {
            return "";
        }

        return localTime.format(
            java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
        );
    }

    protected static String formatAsDate(LocalDate date) {
        if (date == null) {
            return "";
        }

        return DateTimeFormat.forPattern("dd/MM/yyyy").print(date);
    }

    protected static String durationToString(Duration duration, LocalDate startDate) {
        if (duration == null) {
            return null;
        }

        if (duration.getUnit().isNotSuccessful()) {
            return StringUtils.joinWith(" ", duration.getValue().getMappedOrThrow(), duration.getUnit().getUnmappedOrThrow());
        }

        RangeChecker rangeChecker = new RangeChecker();
        return StringUtils.joinWith(" ", duration.getValue().getMappedOrThrow(), conjugateTimeunit(duration.getUnit().getMappedOrThrow(), duration.getValue().getMappedOrThrow()), "[" + formatAsDate(rangeChecker.calculateEndDateForDuration(startDate, duration)) + "]");
    }


}
