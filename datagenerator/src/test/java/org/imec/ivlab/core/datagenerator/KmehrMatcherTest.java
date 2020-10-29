//package org.imec.ivlab.core.datagenerator;
//
//
//import org.apache.commons.lang3.StringUtils;
//import org.imec.ivlab.core.exceptions.DataNotFoundException;
//import org.imec.ivlab.core.model.upload.kmehrentrylist.BusinessData;
//import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntry;
//import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrEntryList;
//import org.imec.ivlab.core.model.evsref.EVSREF;
//import org.imec.ivlab.core.model.uri.util.URIBuilder;
//import org.imec.ivlab.core.model.uri.util.URIWriter;
//import org.imec.ivlab.core.model.upload.kmehrentrylist.KmehrExtractor;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//
//import java.util.HashMap;
//
//@Test
//public class KmehrMatcherTest {
//
//    @Test
//    public void testUpdateKmehrURIsByURILookup() throws Exception {
//
//        KmehrEntryList uploadKmehrList = new KmehrEntryList();
//
//        uploadKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData(""), URIBuilder.fromString("/subject/84072536717/medication-scheme/00001")));
//        uploadKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData(""), URIBuilder.fromString("/subject/84072536717/medication-scheme/00002/1")));
//        uploadKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData(""), URIBuilder.fromString("/subject/84072536717/medication-scheme/00004/5")));
//
//        KmehrEntryList vaultKmehrList = new KmehrEntryList();
//
//        vaultKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData(""), URIBuilder.fromString("/subject/84072536717/medication-scheme/00001/2")));
//        vaultKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData(""), URIBuilder.fromString("/subject/84072536717/medication-scheme/00002/2")));
//        vaultKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData(""), URIBuilder.fromString("/subject/84072536717/medication-scheme/00003/2")));
//        vaultKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData(""), URIBuilder.fromString("/subject/84072536717/medication-scheme/00004/2")));
//
//        KmehrMatcher.correlateKmehrsByURILookup(uploadKmehrList, vaultKmehrList);
//
//        Assert.assertEquals(URIWriter.toString(uploadKmehrList.getKmehrEntries().get(0).getUri()), "/subject/84072536717/medication-scheme/00001/new/2");
//        Assert.assertEquals(URIWriter.toString(uploadKmehrList.getKmehrEntries().get(1).getUri()), "/subject/84072536717/medication-scheme/00002/new/2");
//        Assert.assertEquals(URIWriter.toString(uploadKmehrList.getKmehrEntries().get(2).getUri()), "/subject/84072536717/medication-scheme/00004/new/2");
//
//    }
//
//    @Test(expectedExceptions = DataNotFoundException.class)
//    public void testUpdateKmehrURIsByURILookupFailsIfURINotFound() throws Exception {
//
//        KmehrEntryList uploadKmehrList = new KmehrEntryList();
//
//        uploadKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData(""), URIBuilder.fromString("/subject/84072536717/medication-scheme/00009")));
//
//        KmehrEntryList vaultKmehrList = new KmehrEntryList();
//
//        vaultKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData(""), URIBuilder.fromString("/subject/84072536717/medication-scheme/00001/2")));
//
//        KmehrMatcher.correlateKmehrsByURILookup(uploadKmehrList, vaultKmehrList);
//
//    }
//
//    @Test
//    public void testUpdateKmehrURIsByTestIDLookup() throws Exception {
//
//        KmehrEntryList uploadKmehrList = new KmehrEntryList();
//
//        uploadKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData("Lorem ipsum dolor " + new EVSREF("ab1").getFormatted() + " sit amet, consectetur"), null));
//        uploadKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData("Lorem ipsum dolor " + new EVSREF("ab2").getFormatted() + " sit amet, consectetur"), URIBuilder.fromString("/subject/84072536717/medication-scheme/00010/1")));
//        uploadKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData("Lorem ipsum dolor " + new EVSREF("ab4with20characters!").getFormatted() + " sit amet, consectetur"), null));
//
//        KmehrEntryList vaultKmehrList = new KmehrEntryList();
//
//        vaultKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData("adipiscing elit, " + new EVSREF("ab1").getFormatted() + " sed do eiusmod tempor"), URIBuilder.fromString("/subject/84072536717/medication-scheme/00001/2")));
//        vaultKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData("adipiscing elit, " + new EVSREF("ab2").getFormatted() + " sed do eiusmod tempor"), URIBuilder.fromString("/subject/84072536717/medication-scheme/00002/2")));
//        vaultKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData("adipiscing elit, " + new EVSREF("ab3").getFormatted() + " sed do eiusmod tempor"), URIBuilder.fromString("/subject/84072536717/medication-scheme/00003/2")));
//        vaultKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData("adipiscing elit, " + new EVSREF("ab4with20characters!").getFormatted() + " sed do eiusmod tempor"), URIBuilder.fromString("/subject/84072536717/medication-scheme/00004/2")));
//
//        KmehrMatcher.correlateKmehrsByEVSReferenceLookup(uploadKmehrList, vaultKmehrList);
//
//        Assert.assertEquals(URIWriter.toString(uploadKmehrList.getKmehrEntries().get(0).getUri()), "/subject/84072536717/medication-scheme/00001/new/2");
//        Assert.assertEquals(URIWriter.toString(uploadKmehrList.getKmehrEntries().get(1).getUri()), "/subject/84072536717/medication-scheme/00002/new/2");
//        Assert.assertEquals(URIWriter.toString(uploadKmehrList.getKmehrEntries().get(2).getUri()), "/subject/84072536717/medication-scheme/00004/new/2");
//
//    }
//
//    @Test(expectedExceptions = DataNotFoundException.class)
//    public void testUpdateKmehrURIsByTestIDLookupFailsIfTestIdNotFound() throws Exception {
//
//        KmehrEntryList uploadKmehrList = new KmehrEntryList();
//
//        uploadKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData("Lorem ipsum dolor " + new EVSREF("999").getFormatted() + " sit amet, consectetur"), null));
//
//        KmehrEntryList vaultKmehrList = new KmehrEntryList();
//
//        vaultKmehrList.getKmehrEntries().add(new KmehrEntry(new BusinessData("adipiscing elit, " + new EVSREF("ab1").getFormatted() + " sed do eiusmod tempor"), URIBuilder.fromString("/subject/84072536717/medication-scheme/00001/2")));
//
//        KmehrMatcher.correlateKmehrsByEVSReferenceLookup(uploadKmehrList, vaultKmehrList);
//
//    }
//
//    @Test
//    public void testUpdateSchemeREFAction() {
//
//
//        KmehrEntryList uploadKmehrList = loadKmehrEntryList("updateSchemeRefAction-input.txt");
//        KmehrEntryList vaultKmehrList = loadKmehrEntryList("updateSchemeRefAction-vault.txt");
//
//        HashMap<KmehrMatcher.UpdateSchemeREFAction, KmehrEntryList> actions = KmehrMatcher.calculateActions(uploadKmehrList, vaultKmehrList);
//
//        Assert.assertEquals(actions.size(), 4);
//
//        KmehrEntryList toAddList = actions.get(KmehrMatcher.UpdateSchemeREFAction.ADD);
//        Assert.assertEquals(toAddList.getKmehrEntries().size(), 1);
//        Assert.assertTrue(StringUtils.contains(toAddList.getKmehrEntries().get(0).getBusinessData().getContent(), "86831"));
//
//        KmehrEntryList toRemoveList = actions.get(KmehrMatcher.UpdateSchemeREFAction.REMOVE);
//        Assert.assertEquals(toRemoveList.getKmehrEntries().size(), 1);
//        Assert.assertTrue(StringUtils.contains(toRemoveList.getKmehrEntries().get(0).getBusinessData().getContent(), "97193"));
//
//        KmehrEntryList toUpdateList = actions.get(KmehrMatcher.UpdateSchemeREFAction.UPDATE);
//        Assert.assertEquals(toUpdateList.getKmehrEntries().size(), 1);
//        Assert.assertTrue(StringUtils.contains(toUpdateList.getKmehrEntries().get(0).getBusinessData().getContent(), "41115"));
//
//        KmehrEntryList nothingList = actions.get(KmehrMatcher.UpdateSchemeREFAction.NOTHING);
//        Assert.assertEquals(nothingList.getKmehrEntries().size(), 1);
//        Assert.assertTrue(StringUtils.contains(nothingList.getKmehrEntries().get(0).getBusinessData().getContent(), "73407"));
//
//
//    }
//
//    private KmehrEntryList loadKmehrEntryList(String filename) {
//        String fileContent = TemplateReader.read(filename);
//        return KmehrExtractor.getKmehrEntryList(fileContent);
//    }
//
//}