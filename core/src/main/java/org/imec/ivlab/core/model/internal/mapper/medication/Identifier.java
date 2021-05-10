package org.imec.ivlab.core.model.internal.mapper.medication;

import java.io.Serializable;

public class Identifier implements Serializable {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Identifier(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Identifier() {
    }
}
