package org.imec.ivlab.core.util;

import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imec.ivlab.core.authentication.AuthenticationConfigReader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static com.inamik.text.tables.Cell.Functions.HORIZONTAL_CENTER;

public class ConsoleUtils {

    private final static Logger log = LogManager.getLogger(AuthenticationConfigReader.class);


    private static final int BOX_LENGTH = 170;

    public static String emphasizeTitle(String message) {

        SimpleTable simpletable = SimpleTable.of();
        simpletable.nextRow()
                .nextCell()
                .addLine("");

        String[] lines = message.split(System.lineSeparator());
        for (int i = 0; i < lines.length; i++) {
            simpletable.addLine(lines[i]).applyToCell(HORIZONTAL_CENTER.withWidth(BOX_LENGTH).withChar(' '));
        }
        simpletable.addLine("");

        GridTable gridTable = simpletable.toGrid();
        gridTable = Border.of(Border.Chars.of('+', '-', '|')).apply(gridTable);
        return tableToString(gridTable);

    }

    private static String tableToString(GridTable table) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        PrintStream outStream = new PrintStream(byteOut);
        Util.print(table, outStream);
        return byteOut.toString();
    }

    public static String createAsciiMessage(String message) {
        BufferedImage image = new BufferedImage(144, 32, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setFont(new Font("Dialog", Font.PLAIN, 24));
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.drawString(message, 6, 24);
        try {
            ImageIO.write(image, "png", new File("text.png"));
        } catch (IOException e) {
            log.error(e);
            return "";
        }

        StringBuffer stringBuffer = new StringBuffer();

        for (int y = 0; y < 32; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < 144; x++)
                sb.append(image.getRGB(x, y) == -16777216 ? " " : image.getRGB(x, y) == -1 ? "#" : "*");
            if (sb.toString().trim().isEmpty()) continue;
            stringBuffer.append(sb.toString() + System.lineSeparator());
        }

        return stringBuffer.toString();
    }

}
