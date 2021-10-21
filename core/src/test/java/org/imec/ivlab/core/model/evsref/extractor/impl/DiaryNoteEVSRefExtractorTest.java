package org.imec.ivlab.core.model.evsref.extractor.impl;

import static org.assertj.core.api.Assertions.assertThat;

import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import be.fgov.ehealth.standards.kmehr.schema.v1.FolderType;
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage;
import be.fgov.ehealth.standards.kmehr.schema.v1.TextWithLayoutType;
import be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import junit.framework.TestCase;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.imec.ivlab.core.TestResourceReader;
import org.imec.ivlab.core.kmehr.KmehrMarshaller;
import org.imec.ivlab.core.kmehr.model.util.FolderUtil;
import org.imec.ivlab.core.kmehr.model.util.KmehrMessageUtil;
import org.imec.ivlab.core.kmehr.model.util.TransactionUtil;
import org.imec.ivlab.core.model.evsref.EVSREF;
import org.imec.ivlab.core.model.upload.KmehrWithReference;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
@Log4j
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
  public void addEvsRefToStrikedTextifOnlyStrikedTextExists() {

    Kmehrmessage kmehrmessage = readTemplate("100-diarynote-everything-striked.xml");
    TransactionType firstTransaction = getTransactionType(kmehrmessage);
    List<TextWithLayoutType> textWithLayoutTypes = TransactionUtil.getTextWithLayout(firstTransaction);

    assertThat(getNonEmptyContent(textWithLayoutTypes)).hasSize(1);

    new DiaryNoteEVSRefExtractor().putEvsReference( new KmehrWithReference(kmehrmessage),evsref);

    assertThat(getNonEmptyContent(textWithLayoutTypes)).hasSize(1);

  }

  @Test
  public void addEvsRefAtTheEndStrikedTextifNotOnlyStrikedTextExists() {

    Kmehrmessage kmehrmessage = readTemplate("101-diarynote-almost-everything-striked.xml");
    TransactionType firstTransaction = getTransactionType(kmehrmessage);
    List<TextWithLayoutType> textWithLayoutTypes = TransactionUtil.getTextWithLayout(firstTransaction);
    assertThat(textWithLayoutTypes).isNotEmpty();

    assertThat(getNonEmptyContent(textWithLayoutTypes)).hasSize(2);

    new DiaryNoteEVSRefExtractor().putEvsReference( new KmehrWithReference(kmehrmessage),evsref);

    assertThat(getNonEmptyContent(textWithLayoutTypes)).hasSize(3);

  }

  private List<Object> getNonEmptyContent(List<TextWithLayoutType> textWithLayoutTypes) {
    return textWithLayoutTypes
        .get(0)
        .getContent()
        .stream()
        .filter(content -> StringUtils.trimToNull(content.toString()) != null)
        .collect(Collectors.toList());
  }


  private TransactionType getTransactionType(Kmehrmessage kmehrmessage) {
    FolderType folderType = KmehrMessageUtil.getFolderType(kmehrmessage);
    TransactionType firstTransaction = FolderUtil.getFirstTransaction(folderType);
    return firstTransaction;
  }

  private Kmehrmessage readTemplate(String templateName) {
    String kmehrText = TestResourceReader.read(templateName);
    return KmehrMarshaller.fromString(kmehrText);
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