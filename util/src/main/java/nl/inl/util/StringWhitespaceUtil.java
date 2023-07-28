package nl.inl.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringWhitespaceUtil {
    private static final Pattern PATT_WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern PATT_WS_PUNCT_AT_END = Pattern.compile("[\\p{P}\\s]+$");
    private static final Pattern PATT_WS_PUNCT_AT_START = Pattern.compile("^[\\p{P}\\s]+");

    private StringWhitespaceUtil() {
    }

    public static String normalizeWhitespace(String s) {
        Matcher m = PATT_WHITESPACE.matcher(s);
        return m.replaceAll(" ");
    }

    public static String trimWhitespaceAndPunctuation(String input) {
        input = PATT_WS_PUNCT_AT_END.matcher(input).replaceAll("");
        input = PATT_WS_PUNCT_AT_START.matcher(input).replaceAll("");
        return input;
    }
}
