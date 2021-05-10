package org.imec.ivlab.core.model.upload.vaccinationentry;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONvalues;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import java.util.List;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.exceptions.TransformationException;
import org.imec.ivlab.core.kmehr.KmehrMarshaller;
import org.imec.ivlab.core.kmehr.model.util.FolderUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntry;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
import org.imec.ivlab.core.util.CollectionsUtil;

public class VaccinationListExtractor {

  private final static Logger LOG = Logger.getLogger(VaccinationListExtractor.class);

  public static VaccinationList getVaccinationList(KmehrEntryList kmehrEntryList)  {

    VaccinationList vaccinationList = new VaccinationList();

    if (CollectionsUtil.emptyOrNull(kmehrEntryList.getKmehrEntries())) {
      return vaccinationList;
    }

    for (KmehrEntry kmehrEntry : kmehrEntryList.getKmehrEntries()) {
      Kmehrmessage kmehrmessage = null;
      try {
        kmehrmessage = KmehrMarshaller.fromString(kmehrEntry
            .getBusinessData()
            .getContent());
      } catch (TransformationException e) {
        throw new RuntimeException(e);
      }

      if (kmehrEntry != null && kmehrEntry.getBusinessData() != null) {
        vaccinationList
            .getList()
            .add(getVaccination(kmehrmessage));

      }
    }

    return vaccinationList;

  }

  public static VaccinationList getVaccinationList(List<Kmehrmessage> kmehrmessages) {

    VaccinationList vaccinationList = new VaccinationList();

    if (CollectionsUtil.emptyOrNull(kmehrmessages)) {
      return vaccinationList;
    }

    for (Kmehrmessage kmehrmessage : kmehrmessages) {
      vaccinationList
          .getList()
          .add(getVaccination(kmehrmessage));
    }

    return vaccinationList;

  }

  private static Vaccination getVaccination(Kmehrmessage kmehrmessage) {

    if (kmehrmessage == null) {
      return null;
    }

    FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessage);

    List<TransactionType> diaryNoteTransactions = FolderUtil.getTransactions(folderType, CDTRANSACTIONvalues.VACCINATION);

    if (CollectionsUtil.emptyOrNull(diaryNoteTransactions)) {
      LOG.error("No VACCINATION transaction found within kmehr!");
      return null;
    }

    return new Vaccination(kmehrmessage);
  }

}
