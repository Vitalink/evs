package org.imec.ivlab.core.model.evsref.extractor;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.evsref.EVSREF;
import org.imec.ivlab.core.model.evsref.Identifiable;
import org.imec.ivlab.core.model.evsref.ListOfIdentifiables;
import org.imec.ivlab.core.model.upload.msentrylist.exception.IdenticalEVSRefsFoundException;
import org.imec.ivlab.core.model.upload.msentrylist.exception.MissingEVSRefException;
import org.imec.ivlab.core.model.upload.msentrylist.exception.MultipleEVSRefsInTransactionFoundException;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.JAXBUtils;
import org.imec.ivlab.core.util.RandomGenerator;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.imec.ivlab.core.kmehr.model.util.TransactionUtil.getTransactionId;
import static org.imec.ivlab.core.model.evsref.EVSREF.FIND_EVSREF_REGULAR_EXPRESSION;

public abstract class AbstractRefExtractor implements RefExtractor {

    private final static Logger LOG = LogManager.getLogger(AbstractRefExtractor.class);

    @Override
    public void validatePresenceEVSRefs(ListOfIdentifiables listOfIdentifiables) throws MissingEVSRefException {

        if (CollectionsUtil.emptyOrNull(listOfIdentifiables.getIdentifiables())) {
            return;
        }

        for (Identifiable identifiable : listOfIdentifiables.getIdentifiables()) {
            if (identifiable.getReference() == null) {
                String transactionId = TransactionUtil.getTransactionId(identifiable.getIdentifiableTransaction());
                String errorMessage = "EVSRef is missing";
                errorMessage = errorMessage + (StringUtils.isBlank(transactionId) ? "" : " for transaction with id: " + transactionId);
                throw new MissingEVSRefException(errorMessage);
            }
        }

    }

    @Override
    public ListOfIdentifiables merge(ListOfIdentifiables listOne, ListOfIdentifiables listTwo) {

        if (listOne == null && listTwo == null) {
            return null;
        }

        if (listOne == null) {
            return SerializationUtils.clone(listTwo);
        }

        if (listTwo == null) {
            return SerializationUtils.clone(listOne);
        }

        ListOfIdentifiables listOneCopy = SerializationUtils.clone(listOne);
        ListOfIdentifiables listTwoCopy = SerializationUtils.clone(listTwo);
        listOneCopy.getIdentifiables().addAll(listTwoCopy.getIdentifiables());

        return listOneCopy;

    }

    @Override
    public void validateEVSRefUniqueness(ListOfIdentifiables listOfIdentifiables) throws IdenticalEVSRefsFoundException {

        if (CollectionsUtil.emptyOrNull(listOfIdentifiables.getIdentifiables())) {
            return;
        }

        Set<EVSREF> usedRefs = new HashSet<>();

        for (Identifiable identifiable : listOfIdentifiables.getIdentifiables()) {
            if (identifiable != null && identifiable.getReference() != null) {

                if (usedRefs.contains(identifiable.getReference())) {
                    throw new IdenticalEVSRefsFoundException("Identical EVS reference found across different transactions: " + identifiable.getReference().getFormatted());
                }

                usedRefs.add(identifiable.getReference());
            }
        }

    }

    private Set<EVSREF> getEVSRefsInList(ListOfIdentifiables listOfIdentifiables) {

        Set<EVSREF> usedRefs = new HashSet<>();

        if (listOfIdentifiables == null || CollectionsUtil.emptyOrNull(listOfIdentifiables.getIdentifiables())) {
            return usedRefs;
        }

        for (Identifiable identifiable : listOfIdentifiables.getIdentifiables()) {
            if (identifiable != null && identifiable.getReference() != null) {
                usedRefs.add(identifiable.getReference());
            }
        }

        return usedRefs;

    }

    @Override
    public void generateEVSRefsIfMissing(ListOfIdentifiables listOfIdentifiables, ListOfIdentifiables listOfIdentifiablesWithAlreadyTakenRefs) {

        if (CollectionsUtil.emptyOrNull(listOfIdentifiables.getIdentifiables())) {
            return;
        }

        Set<EVSREF> usedRefs = getEVSRefsInList(listOfIdentifiables);
        usedRefs.addAll(getEVSRefsInList(listOfIdentifiablesWithAlreadyTakenRefs));

        for (Identifiable identifiable : listOfIdentifiables.getIdentifiables()) {
            if (identifiable != null && identifiable.getReference() == null) {
                EVSREF newReference = createUniqueEVSRef(usedRefs);
                LOG.info("Created EVS reference " + newReference + " for transaction with id: " + getTransactionId(identifiable.getIdentifiableTransaction()));
                putEvsReference(identifiable, newReference);
                identifiable.setReference(newReference);
            }
        }


    }

    @Override
    public void extractEVSRefs(ListOfIdentifiables listOfIdentifiables) throws MultipleEVSRefsInTransactionFoundException {

        if (CollectionsUtil.emptyOrNull(listOfIdentifiables.getIdentifiables())) {
            return;
        }

        for (Identifiable identifiable : listOfIdentifiables.getIdentifiables()) {
            if (identifiable == null) {
                continue;
            }

            try {
                String transactionString = JAXBUtils.marshal(identifiable.getIdentifiableTransaction());
                List<EVSREF> evsRefs = findEVSREFs(transactionString);

                if (CollectionsUtil.size(evsRefs) > 1) {
                    throw new MultipleEVSRefsInTransactionFoundException("Multiple EVS references found within 1 MSE transaction: " + evsRefs);
                } else if (CollectionsUtil.size(evsRefs) == 1) {
                    identifiable.setReference(evsRefs.get(0));
                }

            } catch (JAXBException e) {
                LOG.error(e);
            }


        }

    }

    private List<EVSREF> findEVSREFs(String kmehrContent) {

        List<EVSREF> evsRefs = new ArrayList<>();

        Pattern pattern = Pattern.compile(FIND_EVSREF_REGULAR_EXPRESSION, Pattern.DOTALL + Pattern.CASE_INSENSITIVE + Pattern.MULTILINE);

        Matcher matcher = pattern.matcher(kmehrContent);

        while (matcher.find()) {
            evsRefs.add(new EVSREF(matcher.group(1)));
        }

        return evsRefs;

    }

    private EVSREF createUniqueEVSRef(Set<EVSREF> exceptList) {

        EVSREF newRef = new EVSREF("g" + RandomGenerator.getBaseNumber(5));
        while (CollectionsUtil.notEmptyOrNull(exceptList) && exceptList.contains(newRef)) {
            newRef = new EVSREF("g" + RandomGenerator.getBaseNumber(5));
        }
        return newRef;

    }

}
