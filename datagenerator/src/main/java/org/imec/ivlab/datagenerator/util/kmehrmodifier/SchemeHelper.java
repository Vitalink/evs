package org.imec.ivlab.datagenerator.util.kmehrmodifier;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.imec.ivlab.core.kmehr.model.util.FolderUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.internal.mapper.medication.DailyScheme;
import org.imec.ivlab.core.model.internal.mapper.medication.GlobalScheme;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.util.DateUtils;

public class SchemeHelper {


    public static GlobalScheme toGlobalScheme(Kmehrmessage kmehrmessage, Patient patient) {

        GlobalScheme globalScheme = new GlobalScheme();

        globalScheme.setPatient(patient);
        if (kmehrmessage != null) {
            globalScheme.setAuthors(getAuthors(kmehrmessage));
            globalScheme.setVersion(getMSTransaction(kmehrmessage).getVersion());
            if (getMSTransaction(kmehrmessage).getDate() != null) {
                globalScheme.setLastModifiedDate(DateUtils.toLocalDate(getMSTransaction(kmehrmessage).getDate()));
            }
            if (getMSTransaction(kmehrmessage).getTime() != null) {
                globalScheme.setLastModifiedTime(DateUtils.toLocalTime(getMSTransaction(kmehrmessage).getTime()));
            }
        }

        return globalScheme;

    }

    private static List<HcpartyType> getAuthors(Kmehrmessage kmehrmessage) {

        TransactionType msTransaction = getMSTransaction(kmehrmessage);
        if (msTransaction.getAuthor() != null) {
            return msTransaction.getAuthor().getHcparties();
        } else {
            return new ArrayList<HcpartyType>();
        }

    }

    private static TransactionType getMSTransaction(Kmehrmessage kmehrmessage) {

        FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessage);
        TransactionType transactionMS = FolderUtil.getTransaction(folderType, CDTRANSACTIONvalues.MEDICATIONSCHEME);
        return transactionMS;

    }


    public static DailyScheme toDailyScheme(Kmehrmessage kmehrmessage, Patient patient, LocalDate medicationSchemeDate) {

        DailyScheme dailyScheme = new DailyScheme();

        dailyScheme.setPatient(patient);
        if (kmehrmessage != null) {
            dailyScheme.setAuthors(getAuthors(kmehrmessage));
            dailyScheme.setVersion(getMSTransaction(kmehrmessage).getVersion());
            if (getMSTransaction(kmehrmessage).getDate() != null) {
                dailyScheme.setLastModifiedDate(DateUtils.toLocalDate(getMSTransaction(kmehrmessage).getDate()));
            }
            if (getMSTransaction(kmehrmessage).getTime() != null) {
                dailyScheme.setLastModifiedTime(DateUtils.toLocalTime(getMSTransaction(kmehrmessage).getTime()));
            }
        }
        dailyScheme.setSchemeDate(medicationSchemeDate);

        return dailyScheme;

    }


}
