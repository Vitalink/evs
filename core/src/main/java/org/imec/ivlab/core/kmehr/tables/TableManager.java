package org.imec.ivlab.core.kmehr.tables;

import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TableManager {

    private Map<String, TableDefinition> tableNames;

    private static Set<String> IGNORE_TABLE_CHECK = new HashSet<>();
    private static Set<String> IGNORE_TABLE_VERSION_CHECK = new HashSet<>();
    private static Set<String> IGNORE_CODE_FOR_TABLE_VERSION_CHECK = new HashSet<>();

    static {
        IGNORE_TABLE_CHECK.add("CD-ATC");
        IGNORE_TABLE_CHECK.add("CD-EAN");
        IGNORE_TABLE_CHECK.add("CD-DRUG-CNK");
        IGNORE_TABLE_CHECK.add("CD-INNCLUSTER");
    }

    static {
        IGNORE_TABLE_VERSION_CHECK.add("CD-ATC");
        IGNORE_TABLE_VERSION_CHECK.add("CD-EAN");
        IGNORE_TABLE_VERSION_CHECK.add("CD-DRUG-CNK");
        IGNORE_TABLE_VERSION_CHECK.add("CD-INNCLUSTER");
    }

    static {
        IGNORE_CODE_FOR_TABLE_VERSION_CHECK.add("CD-ATC");
        IGNORE_CODE_FOR_TABLE_VERSION_CHECK.add("CD-EAN");
        IGNORE_CODE_FOR_TABLE_VERSION_CHECK.add("CD-DRUG-CNK");
        IGNORE_CODE_FOR_TABLE_VERSION_CHECK.add("CD-INNCLUSTER");
    }

    public TableManager(List<TableDefinition> tableDefinitions) {
        tableNames = new HashMap<>();
        if (CollectionsUtil.notEmptyOrNull(tableDefinitions)) {
            for (TableDefinition tableDefinition : tableDefinitions) {
                tableNames.put(tableDefinition.getName(), tableDefinition);
            }

        }
    }

    public boolean isValidTable(String tableName) {
        if (IGNORE_TABLE_CHECK.contains(tableName)) {
            return true;
        }
        return getTableDefinition(tableName) != null;
    }

    public boolean isValidTableVersion(String tableName, String tableVersion) {
        if (IGNORE_TABLE_VERSION_CHECK.contains(tableName)) {
            return true;
        }
        return getCodesForVersion(tableName, tableVersion) != null;
    }

    public boolean isValidCodeForTableVersion(String tableName, String tableVersion, String code) {
        if (IGNORE_CODE_FOR_TABLE_VERSION_CHECK.contains(tableName)) {
            return true;
        }
        return getCode(tableName, tableVersion, code) != null;
    }

    private TableDefinition getTableDefinition(String tableName) {
        if (tableNames.containsKey(tableName)) {
            return tableNames.get(tableName);
        } else {
            return null;
        }
    }

    private List<CodeDefinition> getCodesForVersion(String tableName, String tableVersion) {

        TableDefinition tableDefinition = getTableDefinition(tableName);
        if (tableDefinition == null) {
            return null;
        }

        if (tableDefinition.getCodesPerVersion() != null) {
            if (tableDefinition.getCodesPerVersion().containsKey(tableVersion)) {
                if (tableDefinition.getCodesPerVersion().get(tableVersion) == null) {
                    return new ArrayList<>();
                } else {
                    return tableDefinition.getCodesPerVersion().get(tableVersion);
                }
            }
        }

        return null;
    }

    private CodeDefinition getCode(String tableName, String tableVersion, String code) {

        List<CodeDefinition> codesForVersion = getCodesForVersion(tableName, tableVersion);
        if (codesForVersion == null) {
            return null;
        }

        for (CodeDefinition codeDefinition : codesForVersion) {
            if (codeDefinition != null && StringUtils.equals(codeDefinition.getCode(), code)) {
                return codeDefinition;
            }
        }

        return null;

    }

}
