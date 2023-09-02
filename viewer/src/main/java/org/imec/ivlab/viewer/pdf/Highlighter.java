package org.imec.ivlab.viewer.pdf;

import com.itextpdf.text.Chunk;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.imec.ivlab.viewer.pdf.SumehrTableFormatter.getSyntaxGreenFont;
import static org.imec.ivlab.viewer.pdf.SumehrTableFormatter.getSyntaxYellowFont;
import static org.imec.ivlab.viewer.pdf.SumehrTableFormatter.getSyntaxWhiteFont;
import static org.imec.ivlab.viewer.pdf.SumehrTableFormatter.getSyntaxRoseFont;

public class Highlighter {

    //private final static Logger LOG = LogManager.getLogger(Highlighter.class);


    private static final char CHAR_ATTRIBUTE_SPLITTER = '=';
    private static final char CHAR_ATTRIBUTE_SPACER = ' ';
    private static final char CHAR_OPEN_TAG = '<';
    private static final char CHAR_CLOSE_TAG = '>';
    private static final char CHAR_SLASH = '/';
    private static final char CHAR_QUOTE = '\"';
    //private static final char END_ATTRIBUTE_VALUE = '\"';


    public static void main(String[] args) {
        Highlighter.syntaxHighlightXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><a attr1Key=\"attr1Value\"> <b>" +
                "<ns3:c>bla</ns3:c>");
    }

    public static List<Chunk> syntaxHighlightXml(String xmlString) {

        return parseXml(xmlString);

    }


    private static List<Chunk> parseXml(String xml) {

        List<Chunk> chunks = new ArrayList<>();

        //Context context = Context.CLOSE_TAG;

        ParseResult result = new ParseResult(null, xml, Context.CLOSE_TAG);

        do {
            result = parse(result.getContext(), result.getRemainder());
            if (result != null) {
                Chunk chunk = chunkForBracket(result);
                chunk.setCharacterSpacing(0.5f);
                chunk.setWordSpacing(0.7f);
                chunks.add(chunk);
            }

        } while (result != null);

        return chunks;
    }

    // regexp for all tags, traversing lines
    // per start/open

    private static ParseResult parse(Context oldContext, String oldRemainder) {

        int posOfMatch = -1;
        String match = null;
        String remainder = null;
        Context nextContext = null;

        switch (oldContext) {

            case TAG_TEXT_CONTENT:
                posOfMatch = getPosOfFirstMatchingChar(oldRemainder, new char[]{CHAR_OPEN_TAG});

                if (posOfMatch < 0) {
                    return null;
                }

                if (posOfMatch == 0) {
                    match = StringUtils.substring(oldRemainder, 0, posOfMatch + 1);
                    remainder = StringUtils.substring(oldRemainder, posOfMatch + 1);
                    nextContext = Context.OPEN_TAG;
                } else {
                    throw new RuntimeException("Content found between TAG_TEXT_CONTENT and CHAR_OPEN_TAG. This should never happen");
                }

                break;

            case CLOSE_TAG:
                posOfMatch = getPosOfFirstMatchingChar(oldRemainder, new char[]{CHAR_OPEN_TAG});

                if (posOfMatch < 0) {
                    return null;
                }

                if (posOfMatch == 0) {
                    match = StringUtils.substring(oldRemainder, 0, posOfMatch + 1);
                    remainder = StringUtils.substring(oldRemainder, posOfMatch + 1);
                    nextContext = Context.OPEN_TAG;
                    break;
                } else {
                    match = StringUtils.substring(oldRemainder, 0, posOfMatch);
                    remainder = StringUtils.substring(oldRemainder, posOfMatch);
                    nextContext = Context.TAG_TEXT_CONTENT;
                    break;
                }

            case OPEN_TAG: case TAG_SLASH: case TAG_NAME: case ATTRIBUTE_VALUE:
                posOfMatch = getPosOfFirstMatchingChar(oldRemainder, new char[]{CHAR_CLOSE_TAG, CHAR_ATTRIBUTE_SPACER, CHAR_SLASH});

                if (posOfMatch < 0) {
                    return null;
                }

                if (oldRemainder.charAt(posOfMatch) == CHAR_SLASH) {
                    match = StringUtils.substring(oldRemainder, 0, posOfMatch + 1);
                    remainder = StringUtils.substring(oldRemainder, posOfMatch + 1);
                    nextContext = Context.TAG_SLASH;
                    break;
                }

                // tag name found
                if (posOfMatch > 0) {
                    match = StringUtils.substring(oldRemainder, 0, posOfMatch);
                    remainder = StringUtils.substring(oldRemainder, posOfMatch);
                    nextContext = Context.TAG_NAME;
                    break;
                }

                if (oldRemainder.charAt(posOfMatch) == CHAR_CLOSE_TAG) {
                    match = StringUtils.substring(oldRemainder, 0, posOfMatch + 1);
                    remainder = StringUtils.substring(oldRemainder, posOfMatch + 1);
                    nextContext = Context.CLOSE_TAG;
                    break;
                } else if (oldRemainder.charAt(posOfMatch) == CHAR_ATTRIBUTE_SPACER) {
                    match = StringUtils.substring(oldRemainder, 0, posOfMatch + 1);
                    remainder = StringUtils.substring(oldRemainder, posOfMatch + 1);
                    nextContext = Context.SPACES_IN_TAG;
                    break;
                }

                return null;

            case SPACES_IN_TAG:
                posOfMatch = getPosOfFirstMatchingChar(oldRemainder, new char[]{CHAR_ATTRIBUTE_SPLITTER});

                if (posOfMatch < 0) {
                    return null;
                }

                if (posOfMatch > 0) {
                    match = StringUtils.substring(oldRemainder, 0, posOfMatch);
                    remainder = StringUtils.substring(oldRemainder, posOfMatch);
                    nextContext = Context.ATTRIBUTE_KEY;
                    break;
                } else {
                    match = StringUtils.substring(oldRemainder, 0, posOfMatch + 1);
                    remainder = StringUtils.substring(oldRemainder, posOfMatch + 1);
                    nextContext = Context.ATTRIBUTE_SPLITTER;
                    break;
                }

            case ATTRIBUTE_KEY:
                posOfMatch = getPosOfFirstMatchingChar(oldRemainder, new char[]{CHAR_ATTRIBUTE_SPLITTER});

                if (posOfMatch < 0) {
                    return null;
                }

                match = StringUtils.substring(oldRemainder, 0, posOfMatch + 1);
                remainder = StringUtils.substring(oldRemainder, posOfMatch + 1);
                nextContext = Context.ATTRIBUTE_SPLITTER;

                break;

            case ATTRIBUTE_SPLITTER:
                posOfMatch = getPosOfSecondMatchingChar(oldRemainder, new char[]{CHAR_QUOTE});

                if (posOfMatch < 0) {
                    return null;
                }

                match = StringUtils.substring(oldRemainder, 0, posOfMatch  + 2);
                remainder = StringUtils.substring(oldRemainder, posOfMatch + 2);
                nextContext = Context.ATTRIBUTE_VALUE;
                break;

            default:
                throw new NotImplementedException("Context " + oldContext.name() + " not supported yet");
        }

        return new ParseResult(match, remainder, nextContext);

    }

    private enum Context {
        OPEN_TAG, CLOSE_TAG, SPACES_IN_TAG, TAG_TEXT_CONTENT, ATTRIBUTE_KEY, ATTRIBUTE_SPLITTER, ATTRIBUTE_VALUE, TAG_NAME, TAG_SLASH
    }

    private static class ParseResult {

        private String match;
        private String remainder;
        private Context context;

        public ParseResult(String match, String remainder, Context context) {
            this.match = match;
            this.remainder = remainder;
            this.context = context;
        }

        public String getMatch() {
            return match;
        }

        public String getRemainder() {
            return remainder;
        }

        public Context getContext() {
            return context;
        }


        @Override
        public String toString() {
            return "ParseResult{" +
                    "match='" + match + '\'' +
                    ", remainder='" + remainder + '\'' +
                    ", context=" + context +
                    '}';
        }
    }

    private static Chunk chunkForBracket(ParseResult parseResult) {

        switch (parseResult.getContext()) {

            case ATTRIBUTE_KEY: return new Chunk(parseResult.getMatch(), getSyntaxGreenFont());
            case ATTRIBUTE_VALUE: return new Chunk(parseResult.getMatch(), getSyntaxYellowFont());
            case OPEN_TAG: case CLOSE_TAG: case TAG_SLASH: case SPACES_IN_TAG: case ATTRIBUTE_SPLITTER: case TAG_TEXT_CONTENT: return new Chunk(parseResult.getMatch(), getSyntaxWhiteFont());
            case TAG_NAME: return new Chunk(parseResult.getMatch(), getSyntaxRoseFont());
            default: return new Chunk(parseResult.getMatch(), getSyntaxWhiteFont());
        }

    }

    private static int getPosOfFirstMatchingChar(String completeString, char[] chars) {

        return StringUtils.indexOfAny(completeString, chars);

    }

    private static int getPosOfSecondMatchingChar(String completeString, char[] chars) {

        int posOfFirsOccurrence = StringUtils.indexOfAny(completeString, chars);
        if (posOfFirsOccurrence < 0) {
            return posOfFirsOccurrence;
        }
        return StringUtils.indexOfAny(StringUtils.substring(completeString, posOfFirsOccurrence + 1), chars);

    }


}
