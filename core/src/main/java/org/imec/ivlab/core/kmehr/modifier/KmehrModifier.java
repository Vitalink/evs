package org.imec.ivlab.core.kmehr.modifier;

import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import org.imec.ivlab.core.kmehr.modifier.exception.InvalidKmehrException;
import org.imec.ivlab.core.model.upload.kmehrentrylist.BusinessData;
import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntry;
import org.imec.ivlab.core.kmehr.KmehrMarshaller;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KmehrModifier {

    public static void applyKmehrModifiers(KmehrEntry kmehrEntry, List<KmehrModification> kmehrModifiers) throws InvalidKmehrException {
        List<KmehrEntry> kmehrEntries = new ArrayList<>();
        kmehrEntries.add(kmehrEntry);
        applyKmehrModifiers(kmehrEntries, kmehrModifiers);
    }

    public static void applyKmehrModifiers(List<KmehrEntry> kmehrEntries, List<KmehrModification> kmehrModifiers) throws InvalidKmehrException {

        if (kmehrEntries == null) {
            return;
        }

        Iterator<KmehrEntry> kmehrEntryIterator = kmehrEntries.iterator();

        while (kmehrEntryIterator.hasNext()) {

            KmehrEntry kmehrEntry = kmehrEntryIterator.next();

            Kmehrmessage kmehrMessage = KmehrMarshaller.fromString(kmehrEntry.getBusinessData().getContent());

            for (KmehrModification kmehrModifier : kmehrModifiers) {
                kmehrModifier.modify(kmehrMessage);
            }

            try {

                kmehrEntry.setBusinessData(new BusinessData(KmehrMarshaller.toString(kmehrMessage)));

            } catch (JAXBException e) {
                throw new InvalidKmehrException("Failed to convert Kmehrmessage to string ", e);
            }

        }

    }


}
