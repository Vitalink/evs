package org.imec.ivlab.core.model.evsref.extractor;

import org.imec.ivlab.core.model.evsref.EVSREF;
import org.imec.ivlab.core.model.evsref.Identifiable;
import org.imec.ivlab.core.model.evsref.ListOfIdentifiables;
import org.imec.ivlab.core.model.upload.msentrylist.exception.IdenticalEVSRefsFoundException;
import org.imec.ivlab.core.model.upload.msentrylist.exception.MissingEVSRefException;
import org.imec.ivlab.core.model.upload.msentrylist.exception.MultipleEVSRefsInTransactionFoundException;

public interface RefExtractor {

    void extractEVSRefs(ListOfIdentifiables listOfIdentifiables) throws MultipleEVSRefsInTransactionFoundException;

    void generateEVSRefsIfMissing(ListOfIdentifiables listOfIdentifiables, ListOfIdentifiables listOfIdentifiablesWithTakenRefs);

    void validateEVSRefUniqueness(ListOfIdentifiables listOfIdentifiables) throws IdenticalEVSRefsFoundException;

    void validatePresenceEVSRefs(ListOfIdentifiables listOfIdentifiables) throws MissingEVSRefException;

    void putEvsReference(Identifiable identifiable, EVSREF evsref);

    ListOfIdentifiables merge(ListOfIdentifiables listOne, ListOfIdentifiables listTwo);

}
