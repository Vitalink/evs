package org.imec.ivlab.viewer.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;

public class MSTableFormatter {

    private static final BaseColor DARK_GREY_COLOR = new BaseColor(64, 70, 71);
    private static final BaseColor IMEC_BLUE_COLOR = new BaseColor(55, 141, 181);
    private static final BaseColor OBSOLETE_ORANGE_COLOR = new BaseColor(244, 191, 66);

    private static final BaseColor QUANTITY_CELL_COLOR = DARK_GREY_COLOR;

    protected static PdfPCell getCenteredCell() {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(4f);
        cell.setPaddingBottom(4f);
        cell.setPaddingLeft(2f);
        cell.setPaddingRight(2f);
        return cell;
    }

    protected static PdfPCell getObsoleteMedicationCellNotObsolete() {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setRotation(90);
        return cell;
    }

    protected static PdfPCell getObsoleteMedicationCellObsolete() {
        PdfPCell cell = getObsoleteMedicationCellNotObsolete();
        cell.setBackgroundColor(OBSOLETE_ORANGE_COLOR);
        return cell;
    }

    protected static PdfPCell getQuantityWithValueCell() {
        PdfPCell cell = getCenteredCell();
        cell.setBackgroundColor(QUANTITY_CELL_COLOR);
        cell.setPaddingTop(4f);
        cell.setPaddingBottom(4f);
        cell.setPaddingLeft(2f);
        cell.setPaddingRight(2f);
        return cell;
    }

    protected static PdfPCell getMedicationHeaderCell() {
        PdfPCell cell = getCenteredCell();
        cell.setBackgroundColor(IMEC_BLUE_COLOR);
        cell.setBorderColorLeft(BaseColor.WHITE);
        cell.setBorderColorRight(BaseColor.WHITE);
        cell.setBorderColorTop(BaseColor.WHITE);
        cell.setBorderColorBottom(BaseColor.WHITE);
        return cell;
    }

    protected static PdfPCell getSuspensionHeaderCell() {
        PdfPCell cell = getCenteredCell();
        cell.setBackgroundColor(new BaseColor(248, 79, 69));
        cell.setBorderColorLeft(BaseColor.WHITE);
        cell.setBorderColorRight(BaseColor.WHITE);
        cell.setBorderColorTop(BaseColor.WHITE);
        cell.setBorderColorBottom(BaseColor.WHITE);
        return cell;
    }

    protected static PdfPCell getMedicationSubHeaderCell() {
        PdfPCell cell = getCenteredCell();
        BaseColor bgcolor = new BaseColor(64, 70, 71);
        cell.setBackgroundColor(bgcolor);
        cell.setBorderColorLeft(bgcolor);
        cell.setBorderColorRight(bgcolor);
        cell.setBorderColorTop(bgcolor);
        cell.setBorderColorBottom(bgcolor);
        return cell;
    }

    protected static Font getTableDefaultFont() {

        Font font = new Phrase().getFont();
        font.setSize(7);

        return font;

    }

    protected static Font getMedicationAuthorFont() {

        Font font = new Phrase().getFont();
        font.setSize(6);

        return font;

    }

    protected static Font getMedicationUriFont() {

        Font font = new Phrase().getFont();
        font.setSize(7);

        return font;

    }

    protected static Font getMedicationAuthorBoldFont() {

        Font font = new Phrase().getFont();
        font.setSize(6);
        font.setStyle(Font.BOLD);

        return font;

    }

    protected static Font getCommentTitleUnderlineFont() {

        Font font = new Phrase().getFont();
        font.setSize(7);
        font.setStyle(Font.UNDERLINE);

        return font;

    }

    protected static Font getTableDefaultBoldFont() {

        Font font = new Phrase().getFont();
        font.setSize(7);
        font.setStyle(Font.BOLD);

        return font;

    }

    protected static Font getFrontPageHeaderFont() {

        Font font = new Phrase().getFont();
        font.setSize(16);

        return font;

    }

    protected static Font getMedicationHeaderFont() {

        Font font = new Phrase().getFont();
        font.setSize(10);
        font.setColor(BaseColor.WHITE);
        font.setStyle(Font.BOLD);

        return font;

    }

    protected static Font getObsoleteFont() {

        Font font = new Phrase().getFont();
        font.setSize(10);
        font.setColor(BaseColor.BLACK);
        return font;

    }

    protected static Font getQuantityFont() {

        Font font = new Phrase().getFont();
        font.setSize(7);
        font.setColor(BaseColor.WHITE);
        return font;

    }

    protected static Font getSuspensionHeaderFont() {

        Font font = new Phrase().getFont();
        font.setSize(7);
        font.setColor(BaseColor.WHITE);
        font.setStyle(Font.BOLD);

        return font;

    }


    protected static Font getMedicationSubHeaderFont() {

        Font font = new Phrase().getFont();
        font.setSize(7);
        font.setStyle(Font.NORMAL);
        font.setColor(BaseColor.WHITE);

        return font;

    }

    protected static Phrase getDefaultPhrase(String phraseString) {
        return new Phrase(phraseString, getTableDefaultFont());
    }

    protected static Phrase getDefaultPhraseBold(String phraseString) {
        return new Phrase(phraseString, getTableDefaultBoldFont());
    }

    protected static Phrase getQuantityPhrase(String phraseString) {
        return new Phrase(phraseString, getQuantityFont());
    }

    protected static Phrase getFrontPageHeaderPhrase(String phraseString) {
        return new Phrase(phraseString, getFrontPageHeaderFont());
    }

    protected static Phrase getMedicationHeaderPhrase(String phraseString) {
        return new Phrase(phraseString, getMedicationHeaderFont());
    }

    protected static Phrase getSuspensionHeaderPhrase(String phraseString) {
        return new Phrase(phraseString, getSuspensionHeaderFont());
    }

    protected static Phrase getMedicationSubHeaderPhrase(String phraseString) {
        return new Phrase(phraseString, getMedicationSubHeaderFont());
    }

    protected static Phrase getMedicationObsoletePhrase(String phraseString) {
        return new Phrase(phraseString, getObsoleteFont());
    }

}
