package org.imec.ivlab.core.model.internal.parser.common;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.imec.ivlab.core.model.internal.parser.sumehr.HcParty;
import org.imec.ivlab.core.model.internal.parser.sumehr.Patient;

public class TransactionCommon {

  private LocalDate date;
  private LocalTime time;
  private List<HcParty> authors = new ArrayList<>();
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

  public List<HcParty> getAuthors() {
    return authors;
  }

  public void setAuthors(List<HcParty> authors) {
    this.authors = authors;
  }


  public Patient getPerson() {
    return person;
  }

  public void setPerson(Patient person) {
    this.person = person;
  }

}
