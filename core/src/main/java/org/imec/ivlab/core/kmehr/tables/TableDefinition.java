package org.imec.ivlab.core.kmehr.tables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableDefinition {

    private String name;

    private Map<String, List<CodeDefinition>> codesPerVersion = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, List<CodeDefinition>> getCodesPerVersion() {
        return codesPerVersion;
    }

    public void setCodesPerVersion(Map<String, List<CodeDefinition>> codesPerVersion) {
        this.codesPerVersion = codesPerVersion;
    }

}
