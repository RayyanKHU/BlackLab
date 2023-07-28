package nl.inl.util;

public final class StringOrdinalUtil {
    private static final String[] ordSuffix = { "th", "st", "nd", "rd" };

    private StringOrdinalUtil() {
    }

    public static String ordinal(int docNumber) {
        int i = docNumber % 10;
        int j = docNumber % 100;
        if (i == 1 && j != 11) {
            return docNumber + "st";
        }
        if (i == 2 && j != 12) {
            return docNumber + "nd";
        }
        if (i == 3 && j != 13) {
            return docNumber + "rd";
        }
        return docNumber + "th";
    }
}
