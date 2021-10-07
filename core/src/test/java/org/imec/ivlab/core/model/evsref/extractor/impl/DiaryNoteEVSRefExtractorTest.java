package org.imec.ivlab.core.model.evsref.extractor.impl;

import static org.assertj.core.api.Assertions.assertThat;

import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TextWithLayoutType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.evsref.EVSREF;
import org.imec.ivlab.core.model.upload.KmehrWithReference;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test
public class DiaryNoteEVSRefExtractorTest extends TestCase {

  private EVSREF evsref = new EVSREF("TESTREF");
  private KmehrWithReference kmehr;

  @BeforeMethod
  private void init() {
    this.kmehr = initKmehr();
  }

  @Test
  public void addEvsRefToNewTextWithLayoutIfNoTextFieldExists() {
    new DiaryNoteEVSRefExtractor().putEvsReference(kmehr, evsref);

    List<TextWithLayoutType> textWithLayoutTypes = TransactionUtil.getTextWithLayout(kmehr.getIdentifiableTransaction());
    assertThat(textWithLayoutTypes).isNotEmpty();
    assertThat(textWithLayoutTypes.get(0).getL()).isEqualTo("nl");
    assertThat(textWithLayoutTypes.get(0).getContent()).isEqualTo(Collections.singletonList("===EVSREF:TESTREF==="));
  }

  @Test
  public void addEvsRefToExistingTextIfExists() {
    TextType textType = new TextType();
    textType.setValue("existing content");
    kmehr.getIdentifiableTransaction().getHeadingsAndItemsAndTexts().add(textType);

    new DiaryNoteEVSRefExtractor().putEvsReference(kmehr, evsref);

    List<TextType> textTypes = TransactionUtil.getText(kmehr.getIdentifiableTransaction());
    assertThat(textTypes).isNotEmpty();
    assertThat(textTypes.get(0).getValue()).isEqualTo("existing content" + evsref.getFormatted());
  }

  @Test
  public void addEvsRefToExistingTextWithLayoutIfExists() {
    TextWithLayoutType textWithLayoutType = new TextWithLayoutType();
    textWithLayoutType.getContent().add("existing");
    textWithLayoutType.getContent().add("content");
    kmehr.getIdentifiableTransaction().getHeadingsAndItemsAndTexts().add(textWithLayoutType);

    new DiaryNoteEVSRefExtractor().putEvsReference(kmehr, evsref);

    List<TextWithLayoutType> textWithLayoutTypes = TransactionUtil.getTextWithLayout(kmehr.getIdentifiableTransaction());
    assertThat(textWithLayoutTypes).isNotEmpty();
    List<Object> contentList = textWithLayoutTypes
        .get(0)
        .getContent();
    assertThat(contentList).hasSize(3);
    assertThat(contentList.get(0)).isEqualTo("existing");
    assertThat(contentList.get(1)).isEqualTo("content");
    assertThat(contentList.get(2)).isEqualTo(evsref.getFormatted());
  }


  private KmehrWithReference initKmehr() {
    Kmehrmessage kmehrMessage = getEmptyKmehr();
    KmehrWithReference kmehrWithReference = new KmehrWithReference(kmehrMessage);
    return kmehrWithReference;
  }

  private Kmehrmessage getEmptyKmehr() {
    Kmehrmessage kmehrmessage = new Kmehrmessage();
    FolderType folder = new FolderType();
    TransactionType transaction = new TransactionType();
    folder.getTransactions().add(transaction);
    kmehrmessage.getFolders().add(folder);
    return kmehrmessage;
  }

}