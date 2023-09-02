package org.imec.ivlab.core.kmehr.tables;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Test
public class TableDefinitionReaderTest {

    @Test
    public void testReadDefinitions() throws Exception {

        TableDefinitionReader tableDefinitionReader = TableDefinitionReader.getInstance();
        List<TableDefinition> tableDefinitions = tableDefinitionReader.readDefinitions("kmehr/tableversions", "kmehr/tables");

        Assert.assertEquals(tableDefinitions.size(), 1);
        Assert.assertEquals(tableDefinitions.get(0).getName(), "CD-ADDRESS");
        Assert.assertEquals(tableDefinitions.get(0).getCodesPerVersion().size(), 4);
        Assert.assertEquals(tableDefinitions.get(0).getCodesPerVersion().get("1.0").size(), 2);
        Assert.assertEquals(tableDefinitions.get(0).getCodesPerVersion().get("1.0").get(0).getCode(), "added_in_1.0_id1");
        Assert.assertEquals(tableDefinitions.get(0).getCodesPerVersion().get("1.0").get(1).getCode(), "added_in_1.0_id2");
        Assert.assertEquals(tableDefinitions.get(0).getCodesPerVersion().get("1.1").size(), 4);
        Assert.assertEquals(tableDefinitions.get(0).getCodesPerVersion().get("1.2").size(), 5);
        Assert.assertEquals(tableDefinitions.get(0).getCodesPerVersion().get("1.2").get(4).getCode(), "added_in_1.2_id1");
        Assert.assertEquals(tableDefinitions.get(0).getCodesPerVersion().get("2").get(5).getCode(), "added_in_2_id1");

    }


}