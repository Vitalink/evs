package org.imec.ivlab.validator.report;

import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;
import org.imec.ivlab.validator.validators.model.AbstractValidationItem;
import org.imec.ivlab.validator.validators.model.ValidationResult;
import org.imec.ivlab.validator.validators.business.rules.model.ExecutionStatus;
import org.imec.ivlab.validator.validators.business.rules.model.RuleResult;
import org.imec.ivlab.validator.validators.xsd.model.XsdFailure;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Objects;

import static com.inamik.text.tables.Cell.Functions.HORIZONTAL_CENTER;

public class KmehrValidationFormatter {

    public static String getOutput(ValidationResult validationResult) {
        StringBuffer sb = new StringBuffer();
        sb.append(createRuleSummaryTable(validationResult));
        sb.append(System.lineSeparator());
        sb.append(createValidationDetailsTable(validationResult));
        return sb.toString();
    }

    private static String createRuleSummaryTable(ValidationResult validationResult) {

        SimpleTable s = SimpleTable.of();

        s.nextRow()
                .nextCell()
                    .addLine("Pass").applyToCell(HORIZONTAL_CENTER.withWidth(10).withChar(' '))
                .nextCell()
                    .addLine("Fail")
                .nextCell()
                    .addLine("Interrupted");
        s.nextRow()
                .nextCell()
                    .addLine(String.valueOf(validationResult.getPassedList().size()))
                .nextCell()
                    .addLine(String.valueOf(validationResult.getFailedList().size()))
                .nextCell()
                    .addLine(String.valueOf(validationResult.getInterruptedList().size()));


        GridTable gridTable = s.toGrid();
        gridTable = Border.of(Border.Chars.of('+', '-', '|')).apply(gridTable);
        return tableToString(gridTable);

    }

    private static String tableToString(GridTable table) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        PrintStream outStream = new PrintStream(byteOut);
        Util.print(table, outStream);
        return byteOut.toString();
    }

    private static String createValidationDetailsTable(ValidationResult validationResult) {

        SimpleTable s = SimpleTable.of();

        s.nextRow()
                .nextCell()
                .addLine("RuleId").applyToCell(HORIZONTAL_CENTER.withWidth(10).withChar(' '))
                .nextCell()
                .addLine("Level")
                .nextCell()
                .addLine("Message")
                .nextCell()
                .addLine("ExecutionStatus");


        validationResultToRows(validationResult, s);

        GridTable gridTable = s.toGrid();
        gridTable = Border.of(Border.Chars.of('+', '-', '|')).apply(gridTable);
        return tableToString(gridTable);

    }

    private static void validationResultToRows(ValidationResult validationResult, SimpleTable s) {

        for (AbstractValidationItem validationItem : validationResult.getFailedList()) {

            s.nextRow();

            if (validationItem.getClass().isAssignableFrom(RuleResult.class)) {
                RuleResult ruleResult = (RuleResult) validationItem;

                s.nextCell().addLine(ruleResult.getRuleId())
                        .nextCell().addLine(ruleResult.getLevel().name())
                        .nextCell().addLine(ruleResult.getMessage())
                        .nextCell().addLine(ruleResult.getExecutionStatus().name());

            }

            if (validationItem.getClass().isAssignableFrom(XsdFailure.class)) {
                XsdFailure xsdFailure = (XsdFailure) validationItem;

                s.nextCell().addLine("")
                        .nextCell().addLine(xsdFailure.getLevel().name())
                        .nextCell().addLine(xsdFailure.getMessage())
                        .nextCell().addLine(ExecutionStatus.FAIL.name());

            }


        }

        for (RuleResult ruleResult : validationResult.getInterruptedList()) {

            s.nextRow();

            s.nextCell().addLine(Objects.toString(ruleResult.getRuleId(), ""))
                    .nextCell().addLine(ruleResult.getLevel().name())
                    .nextCell().addLine(ruleResult.getMessage())
                    .nextCell().addLine(ruleResult.getExecutionStatus().name());


        }

    }

}
