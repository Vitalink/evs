package org.imec.ivlab.core.model.internal.parser.sumehr;

public class ContactPerson extends AbstractPerson {
    private String relation;

    public ContactPerson() {
        super("person");
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
