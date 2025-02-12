package nl.inl.blacklab.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import nl.inl.util.StringUtil;

/**
 * Lowercases and/or removes any accents from the input.
 *
 * NOTE: Lucene includes ASCIIFoldingFilter, but this works with non-ASCII
 * characters too.
 *
 * Uses Normalizer, so Java 1.6+ is needed. If this is not available, use an
 * approach such as RemoveDutchAccentsFilter.
 */
public class DesensitizeFilter extends TokenFilter {

    private final CharTermAttribute termAtt;

    private final boolean lowerCase;

    private final boolean removeAccents;

    /**
     * @param input the token stream to desensitize
     * @param lowerCase whether to lower case tokens
     * @param removeAccents whether to remove accents
     */
    public DesensitizeFilter(TokenStream input, boolean lowerCase, boolean removeAccents) {
        super(input);
        this.lowerCase = lowerCase;
        this.removeAccents = removeAccents;
        termAtt = addAttribute(CharTermAttribute.class);
    }

    @Override
    final public boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }

        String t = new String(termAtt.buffer(), 0, termAtt.length());
        t = processToken(t);
        // Updated the termAtt object with the desensitized token to accomodate process Token
        termAtt.setEmpty().append(t);
        return true;
    }

    // Added processToken to carry out the accent and lowercase functionality for incrementToken method
    private String processToken(String token) {
        if (removeAccents) {
            token = StringUtil.stripAccents(token);
        }
        if (lowerCase) {
            token = token.toLowerCase();
        }
        return token;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (lowerCase ? 1231 : 1237);
        result = prime * result + (removeAccents ? 1231 : 1237);
        result = prime * result + ((termAtt == null) ? 0 : termAtt.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DesensitizeFilter other = (DesensitizeFilter) obj;
        if (lowerCase != other.lowerCase)
            return false;
        if (removeAccents != other.removeAccents)
            return false;
        if (termAtt == null) {
            if (other.termAtt != null)
                return false;
        } else if (!termAtt.equals(other.termAtt))
            return false;
        return true;
    }
}
