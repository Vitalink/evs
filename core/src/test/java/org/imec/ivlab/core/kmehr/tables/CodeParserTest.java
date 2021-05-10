package org.imec.ivlab.core.kmehr.tables;

import org.imec.ivlab.core.util.CollectionsUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@Test
public class CodeParserTest {

    @Test
    public void testParseWithCodes() throws Exception {

        CodeParser codeParser = new CodeParser();
        List<Code> actualCodes = codeParser.parse(getTestString());
        List<Code> expectedCodes = getExpectedCodes();

        Assert.assertEquals(CollectionsUtil.size(actualCodes), CollectionsUtil.size(expectedCodes));
        Assert.assertTrue(expectedCodes.get(0).equals(actualCodes.get(0)));
        Assert.assertTrue(expectedCodes.get(1).equals(actualCodes.get(1)));
        Assert.assertTrue(expectedCodes.get(2).equals(actualCodes.get(2)));
        Assert.assertTrue(expectedCodes.get(3).equals(actualCodes.get(3)));
        Assert.assertTrue(expectedCodes.get(4).equals(actualCodes.get(4)));
        Assert.assertTrue(expectedCodes.get(5).equals(actualCodes.get(5)));
        Assert.assertTrue(expectedCodes.get(6).equals(actualCodes.get(6)));
        Assert.assertTrue(expectedCodes.get(7).equals(actualCodes.get(7)));

    }

    @Test
    public void testParseNoCodesFound() throws Exception {

        CodeParser codeParser = new CodeParser();
        List<Code> actualCodes = codeParser.parse("");

        Assert.assertEquals(actualCodes.size(), 0);

    }

    private String getTestString() {

        return "            <ns4:dayperiod>\n" +
                "              <ns4:cd S=\"CD-DAYPERIOD\" SV=\"1.1\">duringbreakfast</ns4:cd>\n" +
                "            </ns4:dayperiod>" +
                "        <ns4:temporality>\n" +
                "          <ns4:cd S=\"CD-TEMPORALITY\">chronic</ns4:cd>\n" +
                "        </ns4:temporality>" +
                "        <ns4:cd S=\"CD-ITEM\" L=\"NL\">medication</ns4:cd>" +
                "        <ns4:cd S=\"CD-HCPARTY\" \n SV=\"1.0\">12345678901</ns4:id>" +
                "        </ns4:cd S=\"CD-ITEM-MS\" \n SV=\"1.0\">"  +
                "<intendedcd S=\"CD-INNCLUSTER\" SV=\"2010-07\">1439561</intendedcd>" +
                "<deliveredcd S=\"CD-DRUG-CNK\" SV=\"2010-07\">1439562</deliveredcd>" +
                "</deliveredcd S=\"CD-DRUG-CNK\" SV=\"2010-07\">";

    }

    private List<Code> getExpectedCodes() {
        List<Code> codes = new ArrayList<>();
        codes.add(new Code("CD-DAYPERIOD", "1.1", null, "duringbreakfast"));
        codes.add(new Code("CD-TEMPORALITY", null, null, "chronic"));
        codes.add(new Code("CD-ITEM", null, "NL", "medication"));
        codes.add(new Code("CD-HCPARTY", "1.0", null, "12345678901"));
        codes.add(new Code("CD-ITEM-MS", "1.0", null, null));
        codes.add(new Code("CD-INNCLUSTER", "2010-07", null, "1439561"));
        codes.add(new Code("CD-DRUG-CNK", "2010-07", null, "1439562"));
        codes.add(new Code("CD-DRUG-CNK", "2010-07", null, null));
        return codes;
    }

}