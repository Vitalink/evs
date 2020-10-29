package org.imec.ivlab.core.model.internal.parser.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.imec.ivlab.core.model.internal.parser.sumehr.HcParty;
import org.imec.ivlab.core.model.internal.parser.sumehr.Patient;

public class TransactionCommon {

  private LocalDate date;
  private LocalTime time;
  private LocalDateTime recordDateTime;
  private List<HcParty> author = new ArrayList<>();
  private List<HcParty> redactor = new ArrayList<>();
  private Patient person;

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public LocalTime getTime() {
    return time;
  }

  public void setTime(LocalTime time) {
    this.time = time;
  }

  public List<HcParty> getAuthor() {
    return author;
  }

  public void setAuthor(List<HcParty> author) {
    this.author = author;
  }


  public Patient getPerson() {
    return person;
  }

  public void setPerson(Patient person) {
    this.person = person;
  }

  public List<HcParty> getRedactor() {
    return redactor;
  }

  public void setRedactor(List<HcParty> redactor) {
    this.redactor = redactor;
  }

  public LocalDateTime getRecordDateTime() {
    return recordDateTime;
  }

  public void setRecordDateTime(LocalDateTime recordDateTime) {
    this.recordDateTime = recordDateTime;
  }
}
