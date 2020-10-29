package org.imec.ivlab.core.kmehr.tables;

import org.imec.ivlab.core.util.CollectionsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeParser {

    private static final String EXPRESSION_CD_TAG = "(<(?:ns\\d+:)?[a-zA-Z]*cd[^>]+>[^>]*>|<\\/(?:ns\\d*:)?[a-zA-Z]*cd[^>]+>)";
    private static final String EXPRESSION_S_ATTRIBUTE = "S=\"([^\\\"]*)\"";
    private static final String EXPRESSION_SV_ATTRIBUTE = "SV=\"([^\\\"]*)\"";
    private static final String EXPRESSION_L_ATTRIBUTE = "L=\"([^\\\"]*)\"";
    private static final String EXPRESSION_VALUE = "<(?:ns\\d*:)?[a-zA-Z]*cd[^>]+>([^<]*)";

    public List<Code> parse(String kmehrContent) {

        List<Code> codes = new ArrayList<>();

        List<String> cdStrings = getCdStrings(kmehrContent);

        if (CollectionsUtil.emptyOrNull(cdStrings)) {
            return codes;
        }

        for (String cdString : cdStrings) {
            Code code = new Code(getAttribute(cdString, EXPRESSION_S_ATTRIBUTE), getAttribute(cdString, EXPRESSION_SV_ATTRIBUTE), getAttribute(cdString, EXPRESSION_L_ATTRIBUTE), getValue(cdString, EXPRESSION_VALUE));
            codes.add(code);
        }

        return codes;

    }

    private String getValue(String cdString, String valueExpression) {
        Pattern pattern = Pattern.compile(valueExpression, Pattern.DOTALL + Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(cdString);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }

    }


    private String getAttribute(String cdString, String attributeExpression) {

        Pattern pattern = Pattern.compile(attributeExpression, Pattern.DOTALL + Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(cdString);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }

    }

    private List<String> getCdStrings(String fileContent) {

        List<String> CDStrings = new ArrayList<>();

        Pattern pattern = Pattern.compile(EXPRESSION_CD_TAG, Pattern.DOTALL + Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(fileContent);

        while (matcher.find()) {
            CDStrings.add(matcher.group(1));
        }

        return CDStrings;

    }

}
