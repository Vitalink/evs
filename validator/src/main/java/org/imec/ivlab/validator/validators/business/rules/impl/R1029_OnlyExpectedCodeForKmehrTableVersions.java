package org.imec.ivlab.validator.validators.business.rules.impl;

import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.kmehr.tables.Code;
import org.imec.ivlab.core.kmehr.tables.CodeParser;
import org.imec.ivlab.core.kmehr.tables.TableDefinition;
import org.imec.ivlab.core.kmehr.tables.TableDefinitionReader;
import org.imec.ivlab.core.kmehr.tables.TableManager;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.validator.validators.business.rules.BaseKmehrMessageStringRule;
import org.imec.ivlab.validator.validators.business.rules.CustomMessage;
import org.imec.ivlab.validator.validators.business.rules.model.RuleExecution;
import org.imec.ivlab.validator.validators.model.Level;

import java.util.ArrayList;
import java.util.List;

import static org.imec.ivlab.core.constants.CoreConstants.LOCATION_KMEHR_TABLES;
import static org.imec.ivlab.core.constants.CoreConstants.LOCATION_KMEHR_TABLE_VERSIONS;

public class R1029_OnlyExpectedCodeForKmehrTableVersions extends BaseKmehrMessageStringRule implements CustomMessage {

    private String customMessage = null;

    @Override
    public String getMessage() {
        return "An unexpected code of Kmehr table was used: ";
    }

    @Override
    public String getRuleId() {
        return "1029";
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }

    @Override
    public RuleExecution performValidation(String kmehrMessageString) {

        TableDefinitionReader tableDefinitionReader = TableDefinitionReader.getInstance();
        List<TableDefinition> tableDefinitions = tableDefinitionReader.readDefinitions(LOCATION_KMEHR_TABLE_VERSIONS, LOCATION_KMEHR_TABLES);
        TableManager tableManager = new TableManager(tableDefinitions);

        CodeParser codeParser = new CodeParser();
        List<Code> codes = codeParser.parse(kmehrMessageString);

        if (CollectionsUtil.emptyOrNull(codes)) {
            return passRule();
        }

        ArrayList<String> invalidTables = new ArrayList<>();
        for (Code code : codes) {
            if (!tableManager.isValidCodeForTableVersion(code.getS(), code.getSV(), code.getValue())) {
                if (StringUtils.isEmpty(code.getValue())) {
                    invalidTables.add("empty or no value provided for version " + code.getSV() + " of table " + code.getS());
                } else {
                    invalidTables.add(code.getValue() + " is found for version " + code.getSV() + " of table " + code.getS());
                }
            }
        }

        if (CollectionsUtil.notEmptyOrNull(invalidTables)) {
            customMessage = StringUtils.join(invalidTables, ", ");
            return failRule();
        }


        return passRule();

    }



    @Override
    public String getCustomMessage() {
        return customMessage;
    }


}
