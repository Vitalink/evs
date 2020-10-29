package org.imec.ivlab.core.model.internal.mapper.medication;

import java.io.Serializable;

public class Posology extends PosologyOrRegimen implements Serializable {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
