package nl.inl.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public final class StringCamelCaseUtil {
    private static final Pattern lcaseUcase = Pattern.compile("(\\p{Ll})(\\p{Lu})");

    private StringCamelCaseUtil() {
    }

    public static String camelCaseToDisplayable(String camelCaseString, boolean dashesToSpaces) {
        String spaceified = camelCaseString;
        spaceified = lcaseUcase.matcher(spaceified).replaceAll("$1 $2");
        if (dashesToSpaces)
            spaceified = spaceified.replaceAll("[\\-_]", " ");
        return StringUtils.capitalize(spaceified.toLowerCase());
    }
}
