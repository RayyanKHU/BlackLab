package nl.inl.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public final class StringDiacriticsUtil {
    private static final Pattern PATT_DIACRITICAL_MARKS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\u00ad\u2003]+");

    private StringDiacriticsUtil() {
    }

    public static String removeDiacritics(final String input) {
        if (input == null) {
            return null;
        }
        final StringBuilder decomposed = new StringBuilder(Normalizer.normalize(input, Normalizer.Form.NFD));
        convertRemainingAccentCharacters(decomposed);
        return PATT_DIACRITICAL_MARKS.matcher(decomposed).replaceAll(StringUtils.EMPTY);
    }

    private static void convertRemainingAccentCharacters(StringBuilder decomposed) {
        for (int i = decomposed.length() - 1; i >= 0; i--) {
            char c = decomposed.charAt(i);
            if (Character.getType(c) == Character.NON_SPACING_MARK) {
                decomposed.deleteCharAt(i);
            } else if (c == '\u0141') {
                decomposed.deleteCharAt(i);
                decomposed.insert(i, 'L');
            } else if (c == '\u0142') {
                decomposed.deleteCharAt(i);
                decomposed.insert(i, 'l');
            }
        }
    }
}
