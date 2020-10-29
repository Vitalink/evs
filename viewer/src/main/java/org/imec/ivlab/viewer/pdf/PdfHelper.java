package org.imec.ivlab.viewer.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.imec.ivlab.core.version.LocalVersionReader;
import org.imec.ivlab.viewer.converter.exceptions.SchemaConversionException;

public class PdfHelper {

  private final static Logger LOG = Logger.getLogger(Writer.class);


  public static void writeToDocument(String fileLocation, PdfPTable generalInfoTable, List<PdfPTable> detailTables) throws SchemaConversionException {
    try {
      File tempPdfFile = File.createTempFile(UUID.randomUUID().toString(), "pdf");

      tempPdfFile.deleteOnExit();

      // step 1
      Document document = new Document(PageSize.A4.rotate(), 0, 0, 25, 30);
      // step 2
      PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tempPdfFile.getAbsolutePath()));

      // step 3
      document.open();
      // step 4
      document.add(generalInfoTable);
      for (PdfPTable pdfPTable : detailTables) {
        document.add(pdfPTable);
      }

      // step 5
      document.close();
      writer.close();


      manipulatePdf(tempPdfFile.getAbsolutePath(), fileLocation);

      LOG.debug("Wrote sumehr overview to: " + fileLocation);

    } catch (Throwable t) {
      throw new SchemaConversionException("Failed to create sumehr overview", t);
    }
  }

  private static void manipulatePdf(String src, String dest) throws IOException, DocumentException {
    PdfReader reader = new PdfReader(src);
    int n = reader.getNumberOfPages();
    PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
    PdfContentByte pagecontent;
    for (int i = 0; i < n; ) {
      pagecontent = stamper.getOverContent(++i);
      ColumnText.showTextAligned(pagecontent, Element.ALIGN_LEFT, new Phrase("IMEC TESTVERSIE - " + LocalVersionReader.getInstalledSoftwareAndVersion()), 20, 13, 0);
      ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT,
          new Phrase(String.format("Pagina %s van %s", i, n)), 820, 13, 0);
    }
    stamper.close();
    reader.close();
  }

}
