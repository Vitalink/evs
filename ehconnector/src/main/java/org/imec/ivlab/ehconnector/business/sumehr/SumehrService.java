package org.imec.ivlab.ehconnector.business.sumehr;

import org.imec.ivlab.core.authentication.model.AuthenticationConfig;
import org.imec.ivlab.core.exceptions.VitalinkException;
import org.imec.ivlab.core.model.patient.model.Patient;
import org.imec.ivlab.core.model.upload.sumehrlist.SumehrList;

public interface SumehrService {

    SumehrList getSumehrList(Patient patient) throws VitalinkException;

    void putTransactions(Patient patient, SumehrList sumehrList) throws VitalinkException;

    void revokeTransactions(Patient patient, SumehrList sumehrList) throws VitalinkException;

    void authenticate(AuthenticationConfig authenticationConfig) throws VitalinkException;

}
