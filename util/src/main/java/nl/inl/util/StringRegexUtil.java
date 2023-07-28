package nl.inl.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringRegexUtil {
    private static final Pattern PATT_REGEX_CHARACTERS = Pattern.compile("([|\\\\?*+()\\[\\]\\-^${}.])");

    private StringRegexUtil() {
    }

    public static String escapeRegexCharacters(String termStr) {
        Matcher m = PATT_REGEX_CHARACTERS.matcher(termStr);
        return m.replaceAll("\\\\$1");
    }

    public static String wildcardToRegex(String wildcard) {
        StringBuilder s = new StringBuilder(wildcard.length());
        s.append('^');
        for (int i = 0, is = wildcard.length(); i < is; i++) {
            char c = wildcard.charAt(i);
            switch (c) {
            case '*':
                s.append(".*");
                break;
            case '?':
                s.append(".");
                break;
            // escape special regexp-characters
            case '^': // escape character in cmd.exe
            case '(':
            case ')':
            case '[':
            case ']':
            case '$':
            case '.':
            case '{':
            case '}':
            case '|':
            case '\\':
                s.append("\\");
                s.append(c);
                break;
            default:
                s.append(c);
                break;
            }
        }
        s.append('$');
        return s.toString();
    }
}
