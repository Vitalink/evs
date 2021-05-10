package org.imec.ivlab.viewer.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;

public class SumehrTableFormatter {

    private static final BaseColor SUBTITLE_BACKGROUND_COLOR = new BaseColor(181,199,182);
    private static final BaseColor MAINTITLE_BACKGROUND_COLOR = new BaseColor(233,151,156);
    private static final BaseColor UNPARSEDTITLE_BACKGROUND_COLOR = new BaseColor(239,242,75);
    private static final BaseColor SYNTAX_HIGHLIGHT_ROSE = new BaseColor(242,38,114);
    private static final BaseColor SYNTAX_HIGHLIGHT_YELLOW = new BaseColor(230,219,116);
    private static final BaseColor SYNTAX_HIGHLIGHT_GREEN = new BaseColor(166,226,44);
    private static final BaseColor SYNTAX_HIGHLIGHT_WHITE = new BaseColor(248,248,242);
    private static final BaseColor SYNTAX_HIGHLIGHT_BACKGROUND_DARK = new BaseColor(39,40,34);
    private static final BaseColor SUBTITLE_TEXT_COLOR = BaseColor.BLACK;
    private static final BaseColor MAINTITLE_TEXT_COLOR = BaseColor.BLACK;
    private static final BaseColor UNPARSEDTITLE_TEXT_COLOR = BaseColor.BLACK;

    // color pallette 2017: https://www.w3schools.com/colors/colors_palettes.asp
    /*
        233,151,156
        208,196,205
        164,153,140
        181,199,182
    */

    protected static PdfPCell getCellWithoutBorder() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    protected static PdfPCell getMaintitleCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(MAINTITLE_BACKGROUND_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(2f);
        cell.setPaddingBottom(4f);
        cell.setPaddingLeft(1f);
        cell.setPaddingRight(1f);
        return cell;
    }

    protected static PdfPCell getSubtitleCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(SUBTITLE_BACKGROUND_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(2f);
        cell.setPaddingBottom(4f);
        cell.setPaddingLeft(1f);
        cell.setPaddingRight(1f);
        return cell;
    }

    protected static PdfPCell getUnparsedTitleCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(UNPARSEDTITLE_BACKGROUND_COLOR);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(2f);
        cell.setPaddingBottom(4f);
        cell.setPaddingLeft(1f);
        cell.setPaddingRight(1f);
        return cell;
    }

    protected static PdfPCell getKeyCell() {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setPaddingTop(4f);
        cell.setPaddingBottom(4f);
        return cell;
    }

    protected static PdfPCell getValueCell() {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setPaddingTop(4f);
        cell.setPaddingBottom(4f);
        return cell;
    }

    protected static PdfPCell getUnparsedCell() {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setBackgroundColor(SYNTAX_HIGHLIGHT_BACKGROUND_DARK);
        cell.setPaddingTop(1f);
        cell.setPaddingBottom(6f);
        cell.setPaddingLeft(7f);
        cell.setPaddingRight(7f);
        return cell;
    }

    protected static Font getMaintitleFont() {

        Font font = new Phrase().getFont();
        font.setSize(10);
        font.setStyle(Font.NORMAL);
        font.setColor(MAINTITLE_TEXT_COLOR);

        return font;

    }

    protected static Font getUnparsedtitleFont() {

        Font font = new Phrase().getFont();
        font.setSize(8);
        font.setStyle(Font.NORMAL);
        font.setColor(UNPARSEDTITLE_TEXT_COLOR);

        return font;

    }

    protected static Font getSubtitleFont() {

        Font font = new Phrase().getFont();
        font.setSize(8);
        font.setStyle(Font.NORMAL);
        font.setColor(SUBTITLE_TEXT_COLOR);

        return font;

    }

    protected static Font getSubtitleHighlightFont() {

        Font font = getSubtitleFont();
        font.setStyle(Font.BOLD);

        return font;

    }

    private static Font getSyntaxFont() {
        Font font = new Phrase().getFont();
        font.setSize(8);
        font.setStyle(Font.NORMAL);
        return font;
    }

    protected static Font getSyntaxWhiteFont() {

        Font font = getSyntaxFont();
        font.setColor(SYNTAX_HIGHLIGHT_WHITE);

        return font;

    }

    protected static Font getSyntaxRoseFont() {

        Font font = getSyntaxFont();
        font.setColor(SYNTAX_HIGHLIGHT_ROSE);

        return font;

    }

    protected static Font getSyntaxGreenFont() {

        Font font = getSyntaxFont();
        font.setColor(SYNTAX_HIGHLIGHT_GREEN);

        return font;

    }

    protected static Font getSyntaxYellowFont() {

        Font font = getSyntaxFont();
        font.setColor(SYNTAX_HIGHLIGHT_YELLOW);

        return font;

    }

    protected static Phrase getSubtitlePhrase(String phraseString) {
        return new Phrase(phraseString, getSubtitleFont());
    }

    protected static Phrase getMaintitlePhrase(String phraseString) {
        return new Phrase(phraseString, getMaintitleFont());
    }

    protected static Phrase getUnparsedtitlePhrase(String phraseString) {
        return new Phrase(phraseString, getUnparsedtitleFont());
    }

}
