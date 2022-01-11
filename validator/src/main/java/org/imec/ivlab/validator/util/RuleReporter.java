package org.imec.ivlab.validator.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.util.ClassesUtil;
import org.imec.ivlab.validator.validators.business.rules.Rule;

import java.util.List;

public class RuleReporter {

    private static Logger log = LogManager.getLogger(RuleReporter.class);

    StringBuilder builder;

    public static void main(String[] args) {
        RuleReporter r = new RuleReporter();
        try {
            log.info(r.getReport());
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public RuleReporter() {
        builder = new StringBuilder();
    }

    public String getReport() throws IllegalAccessException, InstantiationException {

        add("");

        List<Class<Rule>> ruleClasses = ClassesUtil.getClasses("org.imec.ivlab.validator", Rule.class);

        add("<table>");
        add("  <tbody>");
        add("    <tr>");
        add("      <th>ID</th>");
        add("      <th>Naam</th>");
        add("      <th>Level</th>");
        add("      <th>Beschrijving</th>");
        add("    </tr>");

        for (Class<Rule> ruleClass : ruleClasses) {
            Rule rule = ruleClass.newInstance();
            add("    <tr>");
            add("      <td>" + rule.getRuleId() + "</td>");
            add("      <td>" + rule.getClass().getSimpleName() + "</td>");
            add("      <td>" + rule.getLevel() + "</td>");
            add("      <td>" + StringEscapeUtils.escapeHtml4(rule.getMessage()) + "</td>");
            add("    </tr>");
        }

        add("  </tbody>");
        add("</table>");


        return builder.toString();

    }


    private void add(String string) {
        builder.append(string);
        builder.append(System.lineSeparator());
    }


}
