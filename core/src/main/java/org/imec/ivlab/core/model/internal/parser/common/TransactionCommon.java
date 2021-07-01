package org.imec.ivlab.core.model.internal.parser.common;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.imec.ivlab.core.model.internal.parser.sumehr.HcParty;
import org.imec.ivlab.core.model.internal.parser.sumehr.Patient;

@Getter
@Setter
public class TransactionCommon {

  private LocalDate date;
  private LocalTime time;
  private LocalDateTime recordDateTime;
  private List<HcParty> author = new ArrayList<>();
  private List<HcParty> redactor = new ArrayList<>();
  private Patient person;
  private List<IDKMEHR> idkmehrs = new ArrayList<>();
  private List<CDTRANSACTION> cdtransactions = new ArrayList<>();

}
