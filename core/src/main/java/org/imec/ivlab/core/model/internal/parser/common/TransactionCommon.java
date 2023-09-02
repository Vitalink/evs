package org.imec.ivlab.core.model.internal.parser.common;

import be.ehealth.technicalconnector.adapter.XmlTimeNoTzAdapter;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.imec.ivlab.core.model.internal.parser.sumehr.HcParty;
import org.imec.ivlab.core.model.internal.parser.sumehr.Patient;

@Getter
@Setter
public class TransactionCommon {

  private LocalDate date;
  @XmlJavaTypeAdapter(XmlTimeNoTzAdapter.class)
  private DateTime time;
  private LocalDateTime recordDateTime;
  private List<HcParty> author = new ArrayList<>();
  private List<HcParty> redactor = new ArrayList<>();
  private Patient person;
  private List<IDKMEHR> idkmehrs = new ArrayList<>();
  private List<CDTRANSACTION> cdtransactions = new ArrayList<>();

}
