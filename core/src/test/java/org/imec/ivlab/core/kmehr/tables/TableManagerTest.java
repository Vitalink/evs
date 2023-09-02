package org.imec.ivlab.core.kmehr.tables;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.List;

@Test
public class TableManagerTest {

    private TableManager tableManager;

    @BeforeSuite
    public void loadTableDefinitions() {
        TableDefinitionReader tableDefinitionReader = TableDefinitionReader.getInstance();
        List<TableDefinition> tableDefinitions = tableDefinitionReader.readDefinitions("kmehr/tableversions", "kmehr/tables");
        tableManager = new TableManager(tableDefinitions);

    }

    @Test
    public void testIsValidTable() throws Exception {
        Assert.assertTrue(tableManager.isValidTable("CD-ADDRESS"));
        Assert.assertFalse(tableManager.isValidTable("cd-address"));
    }

    @Test
    public void testIsValidTableForIgnoredTable() throws Exception {
        Assert.assertTrue(tableManager.isValidTable("CD-DRUG-CNK"));
        Assert.assertTrue(tableManager.isValidTable("CD-INNCLUSTER"));
    }

    @Test
    public void testIsValidTableVersion() throws Exception {
        Assert.assertTrue(tableManager.isValidTableVersion("CD-ADDRESS", "1.0"));
        Assert.assertTrue(tableManager.isValidTableVersion("CD-ADDRESS", "1.1"));
        Assert.assertTrue(tableManager.isValidTableVersion("CD-ADDRESS", "1.2"));
        Assert.assertFalse(tableManager.isValidTableVersion("CD-ADDRESS", "1.3"));
        Assert.assertFalse(tableManager.isValidTableVersion("cd-address", "1.0"));
    }

    @Test
    public void testIsValidTableVersionIgnoredTable() throws Exception {
        Assert.assertTrue(tableManager.isValidTableVersion("CD-EAN", "10.0"));
        Assert.assertTrue(tableManager.isValidTableVersion("CD-DRUG-CNK", "10.0"));
        Assert.assertTrue(tableManager.isValidTableVersion("CD-INNCLUSTER", "10.0"));
    }

    @Test
    public void testIsValidCodeForTableVersionOfIgnoredTable() throws Exception {
        Assert.assertTrue(tableManager.isValidCodeForTableVersion("CD-EAN", "10.0", "100"));
        Assert.assertTrue(tableManager.isValidCodeForTableVersion("CD-DRUG-CNK", "10.0", "100"));
        Assert.assertTrue(tableManager.isValidCodeForTableVersion("CD-INNCLUSTER", "10.0", "100"));
    }

    @Test
    public void testIsValidCodeForTableVersion() throws Exception {
        Assert.assertTrue(tableManager.isValidCodeForTableVersion("CD-ADDRESS", "1.0", "added_in_1.0_id1"));
        Assert.assertTrue(tableManager.isValidCodeForTableVersion("CD-ADDRESS", "1.0", "added_in_1.0_id2"));
        Assert.assertFalse(tableManager.isValidCodeForTableVersion("CD-ADDRESS", "1.0", "added_in_1.1_id1"));

        Assert.assertTrue(tableManager.isValidCodeForTableVersion("CD-ADDRESS", "1.1", "added_in_1.0_id1"));
        Assert.assertTrue(tableManager.isValidCodeForTableVersion("CD-ADDRESS", "1.1", "added_in_1.0_id2"));
        Assert.assertTrue(tableManager.isValidCodeForTableVersion("CD-ADDRESS", "1.1", "added_in_1.1_id1"));
        Assert.assertTrue(tableManager.isValidCodeForTableVersion("CD-ADDRESS", "1.1", "added_in_1.1_id2"));
        Assert.assertFalse(tableManager.isValidCodeForTableVersion("CD-ADDRESS", "1.1", "added_in_1.2_id1"));

        Assert.assertTrue(tableManager.isValidCodeForTableVersion("CD-ADDRESS", "1.2", "added_in_1.0_id1"));
        Assert.assertTrue(tableManager.isValidCodeForTableVersion("CD-ADDRESS", "1.2", "added_in_1.2_id1"));

        Assert.assertTrue(tableManager.isValidCodeForTableVersion("CD-ADDRESS", "2", "added_in_2_id1"));
    }

    @Test
    public void testIsValidCodeForTableVersionIgnoredTable() throws Exception {
        Assert.assertTrue(tableManager.isValidCodeForTableVersion("CD-ATC", "100.0", "added_in_1.0_id1"));
    }

}