package org.imec.ivlab.core.model.internal.parser.vaccination;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDUNIT;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.ItemType;
import be.fgov.ehealth.standards.kmehr.schema.v1.MedicinalProductType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Substanceproduct;
import java.math.BigDecimal;
import org.joda.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.imec.ivlab.core.model.internal.parser.AbstractParsedItem;

@Getter
@Setter
public class VaccinationItem extends AbstractParsedItem<ItemType> {

    private List<MedicinalProductType> medicinalProductTypes = new ArrayList<>();
    private List<Substanceproduct> substanceproducts = new ArrayList<>();
    private List<CDCONTENT> cdcontents = new ArrayList<>();
    private List<TextType> textTypes = new ArrayList<>();
    private LocalDate beginMoment;
    private CDLIFECYCLEvalues lifecycle;
    private BigDecimal quantity;
    private CDUNIT quantityUnit;
    private String batch;

    public VaccinationItem() {
        super("item");
    }

}
