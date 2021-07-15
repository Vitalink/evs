package org.imec.ivlab.core.model.internal.parser.vaccination;

import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.imec.ivlab.core.model.internal.parser.sumehr.HcParty;

@Getter
@Setter
public class EncounterLocation {

  private List<HcParty> authors;
  private List<TextType> textTypes;

}
