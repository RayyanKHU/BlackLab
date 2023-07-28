package nl.inl.util;

// StringUtil will now serve as an interface for Ordinal,Diatrics,CamelCase,Regex,&Whitespace classes to refractor the Multi-faceted abstraction smell.
public final class StringUtil {
    public static final char CHAR_NON_BREAKING_SPACE = '\u00A0';

    private StringUtil() {}

    public static String desensitize(String str) {
        return StringDiacriticsUtil.removeDiacritics(str).toLowerCase();
    }

    public static String escapeRegexCharacters(String termStr) {
        return StringRegexUtil.escapeRegexCharacters(termStr);
    }

    public static String normalizeWhitespace(String s) {
        return StringWhitespaceUtil.normalizeWhitespace(s);
    }

    public static String trimWhitespaceAndPunctuation(String input) {
        return StringWhitespaceUtil.trimWhitespaceAndPunctuation(input);
    }

    public static String camelCaseToDisplayable(String camelCaseString, boolean dashesToSpaces) {
        return StringCamelCaseUtil.camelCaseToDisplayable(camelCaseString, dashesToSpaces);
    }

    public static String ordinal(int docNumber) {
        return StringOrdinalUtil.ordinal(docNumber);
    }

    public static String wildcardToRegex(String wildcard) {
        return StringRegexUtil.wildcardToRegex(wildcard);
    }

    public static String stripAccents(final String input) {
        if (input == null) {
            return null;
        }
        return StringDiacriticsUtil.removeDiacritics(input);
    }
}
