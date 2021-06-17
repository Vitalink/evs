package org.imec.ivlab.viewer.pdf;

import static org.imec.ivlab.viewer.pdf.MSTableFormatter.getDefaultPhrase;
import static org.imec.ivlab.viewer.pdf.SumehrTableFormatter.getCellWithoutBorder;
import static org.imec.ivlab.viewer.pdf.SumehrTableFormatter.getSubtitleFont;
import static org.imec.ivlab.viewer.pdf.SumehrTableFormatter.getSubtitleHighlightFont;
import static org.imec.ivlab.viewer.pdf.SumehrTableFormatter.getUnparsedCell;
import static org.imec.ivlab.viewer.pdf.SumehrTableFormatter.getUnparsedTitleCell;
import static org.imec.ivlab.viewer.pdf.SumehrTableFormatter.getUnparsedtitlePhrase;
import static org.imec.ivlab.viewer.pdf.Translator.formatAsDate;
import static org.imec.ivlab.viewer.pdf.Translator.formatAsDateTime;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.imec.ivlab.core.model.internal.parser.ParsedItem;
import org.imec.ivlab.core.util.CollectionsUtil;
import org.imec.ivlab.core.util.StringUtils;

public class TableHelper {

  public static List<PdfPTable> toUnparsedContentTables(List<? extends ParsedItem> parsedItems, String topic) {

    if (CollectionsUtil.emptyOrNull(parsedItems)) {
      return Collections.emptyList();
    }

    List<PdfPTable> tables = new ArrayList<>();

    PdfPTable titleTable = initializeUnparsedTable();

    PdfPCell unparsedTitleCell = getUnparsedTitleCell();
    unparsedTitleCell.setPhrase(getUnparsedtitlePhrase(StringUtils.joinWith(" - ", topic, "Unparsed content")));
    titleTable.addCell(unparsedTitleCell);

    tables.add(titleTable);

    PdfPTable contentTable = initializeUnparsedTable();

    for (ParsedItem parsedItem : parsedItems) {
      String unparsedAsString = parsedItem.getUnparsedAsString();
      if (unparsedAsString == null) {
        continue;
      }
      PdfPCell contentCell = getUnparsedCell();
      Phrase phrase = new Phrase();
      phrase.setMultipliedLeading(1.1f);
      phrase.addAll(Highlighter.syntaxHighlightXml(unparsedAsString));
      contentCell.addElement(phrase);
      contentTable.addCell(contentCell);
    }

    if (contentTable.getRows().size() > 0) {
      tables.add(contentTable);
      return tables;
    } else {
      return Collections.emptyList();
    }
  }


  public static PdfPTable combineTables(PdfPTable titleTable, PdfPTable contentTable, List<PdfPTable> unparsedContentTables) {

    List<PdfPTable> contentTables = new ArrayList<>();
    contentTables.add(contentTable);
    return combineTables(titleTable, contentTables, unparsedContentTables);

  }

  public static PdfPTable combineTables(PdfPTable titleTable, Collection<PdfPTable> tablesForDualColumn, Collection<PdfPTable> tablesForSingleColumn) {

    PdfPTable table = new PdfPTable(2);

    table.setKeepTogether(true);
    table.setSpacingAfter(10f);

    if (titleTable != null) {
      PdfPCell titleCell = getCellWithoutBorder();
      titleCell.setColspan(2);
      titleCell.addElement(titleTable);
      table.addCell(titleCell);
    }

    if (CollectionsUtil.notEmptyOrNull(tablesForDualColumn)) {

      for (PdfPTable contentTable : tablesForDualColumn) {
        PdfPCell contentCell = getCellWithoutBorder();
        contentCell.setColspan(1);
        contentCell.addElement(contentTable);
        table.addCell(contentCell);
      }

      if (CollectionsUtil.size(tablesForDualColumn) % 2 == 1) {
        PdfPCell spacerCell = getCellWithoutBorder();
        spacerCell.setColspan(1);
        spacerCell.addElement(getDefaultPhrase(""));
        table.addCell(spacerCell);
      }


    }

    if (CollectionsUtil.notEmptyOrNull(tablesForSingleColumn)) {

      for (PdfPTable contentTable : tablesForSingleColumn) {
        PdfPCell contentCell = getCellWithoutBorder();
        contentCell.setColspan(2);
        contentCell.addElement(contentTable);
        table.addCell(contentCell);
      }

    }

    return table;

  }

  public static PdfPTable initializeUnparsedTable() {
    PdfPTable table = new PdfPTable(1);
    table.setWidthPercentage(100);
    table.setHeaderRows(0);
    table.setHorizontalAlignment(Element.ALIGN_CENTER);
    return table;
  }

  public static PdfPTable createTitleTable(String title) {

    PdfPTable table = initializeTitleTable();

    PdfPCell cell = SumehrTableFormatter.getMaintitleCell();
    cell.setPhrase(SumehrTableFormatter.getMaintitlePhrase(title));
    cell.setColspan(100);
    table.addCell(cell);

    return table;
  }

  public static PdfPTable initializeDetailTable() {
    PdfPTable table = new PdfPTable(100);
    table.setWidthPercentage(100);
    table.setHeaderRows(0);
    return table;
  }

  public static PdfPTable initializeTitleTable() {
    PdfPTable table = new PdfPTable(100);
    table.setWidthPercentage(100);
    table.setHeaderRows(0);
    table.setHorizontalAlignment(Element.ALIGN_CENTER);
    return table;
  }

  public static List<PdfPCell> toDetailRowsIfHasValue(List<Pair> columns) {
    if (columns == null) {
      return null;
    }

    List<PdfPCell> cells = new ArrayList<>();
    for (Pair column : columns) {
      List<PdfPCell> cellsForRow = toDetailRowIfHasValue((String) column.getLeft(), column.getRight());
      if (cellsForRow != null) {
        cells.addAll(cellsForRow);
      }
    }

    return cells;
  }

  public static List<PdfPCell> toDetailRowIfHasValue(String key, Object value) {
    if (value == null) {
      return null;
    }

    if (value instanceof byte[]) {
      return createDetailRow(key, (byte[]) value);
    }

    String valueString;
    if (value instanceof LocalDate) {
      valueString = formatAsDate((LocalDate) value);
    } else if (value instanceof LocalDateTime) {
      valueString = formatAsDateTime((LocalDateTime) value);
    } else if (value instanceof Integer) {
      valueString = String.valueOf( value);
    } else if (value instanceof BigDecimal) {
      valueString = value.toString();
    } else if (value instanceof Boolean) {
      valueString = ((Boolean) value) != null ? ((Boolean) value).toString() : null;
    } else {
      valueString = (String) value;
    }

    if (org.apache.commons.lang3.StringUtils.isEmpty(valueString)) {
      return null;
    }

    return createDetailRow(key, valueString);

  }

  public static void addRow(PdfPTable table, List<PdfPCell> cells) {
    if (CollectionsUtil.notEmptyOrNull(cells)) {
      for (PdfPCell cell : cells) {
        table.addCell(cell);
      }
    }
  }

  public static List<PdfPCell> createDetailHeader(String titlePartHighlight, String titlePartNormal) {

    List<Chunk> titleChunks = new ArrayList<>();

    if (org.apache.commons.lang3.StringUtils.isNotEmpty(titlePartHighlight)) {
      titleChunks.add(new Chunk(titlePartHighlight, getSubtitleHighlightFont()));
    }

    if (org.apache.commons.lang3.StringUtils.isNotEmpty(titlePartNormal)) {
      titleChunks.add(new Chunk(titlePartNormal, getSubtitleFont()));
    }

    if (CollectionsUtil.emptyOrNull(titleChunks)) {
      titleChunks.add(new Chunk(" ", getSubtitleFont()));
    }

    List<PdfPCell> cells = new ArrayList<>();
    PdfPCell cell = SumehrTableFormatter.getSubtitleCell();
    Phrase phrase = new Phrase();
    phrase.addAll(titleChunks);
    cell.setPhrase(phrase);
    cell.setColspan(100);
    cells.add(cell);
    return cells;
  }

  public static List<PdfPCell> createDetailHeader(String titlePartNormal) {
    return createDetailHeader(null, titlePartNormal);
  }

  public static List<PdfPCell> createDetailRow(String key, String value) {
    List<PdfPCell> cells = new ArrayList<>();
    PdfPCell cell = SumehrTableFormatter.getKeyCell();
    cell.setPhrase(getDefaultPhrase(StringUtils.nullToString(key)));
    cell.setColspan(30);
    cells.add(cell);
    cell = SumehrTableFormatter.getValueCell();
    cell.setPhrase(getDefaultPhrase(StringUtils.nullToString(value)));
    cell.setColspan(70);
    cells.add(cell);
    return cells;
  }

  public static List<PdfPCell> createDetailRow(String key, Phrase valuePhrase) {
    List<PdfPCell> cells = new ArrayList<>();
    PdfPCell cell = SumehrTableFormatter.getKeyCell();
    cell.setPhrase(getDefaultPhrase(StringUtils.nullToString(key)));
    cell.setColspan(30);
    cells.add(cell);
    cell = SumehrTableFormatter.getValueCell();
    cell.setPhrase(valuePhrase);
    cell.setColspan(70);
    cells.add(cell);
    return cells;
  }

  public static List<PdfPCell> createDetailRow(String content) {
    List<PdfPCell> cells = new ArrayList<>();
    PdfPCell cell = SumehrTableFormatter.getValueCell();
    cell.setPhrase(getDefaultPhrase(StringUtils.nullToString(content)));
    cell.setColspan(100);
    cells.add(cell);
    return cells;
  }

  public static List<PdfPCell> createDetailRow(String key, byte[] value) {
    List<PdfPCell> cells = new ArrayList<>();
    PdfPCell cell = SumehrTableFormatter.getKeyCell();
    cell.setPhrase(getDefaultPhrase(StringUtils.nullToString(key)));
    cell.setColspan(30);
    cells.add(cell);
    cell = SumehrTableFormatter.getValueCell();
    try {
      cell.setImage(Image.getInstance(value));
    } catch (BadElementException | IOException e) {
      cell.setPhrase(getDefaultPhrase("Failed to render image"));
    }
    cell.setColspan(70);
    cells.add(cell);
    return cells;
  }

}
