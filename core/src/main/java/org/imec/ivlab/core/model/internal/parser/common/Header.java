package org.imec.ivlab.core.model.internal.parser.common;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.imec.ivlab.core.model.internal.parser.sumehr.Recipient;
import org.imec.ivlab.core.model.internal.parser.sumehr.Sender;

public class Header {

  private Sender sender;
  private List<Recipient> recipients = new ArrayList<>();
  private LocalDate date;
  private LocalTime time;

  public Sender getSender() {
    return sender;
  }

  public void setSender(Sender sender) {
    this.sender = sender;
  }

  public List<Recipient> getRecipients() {
    return recipients;
  }

  public void setRecipients(List<Recipient> recipients) {
    this.recipients = recipients;
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

  public LocalDate getDate() {
    return date;
  }

}
