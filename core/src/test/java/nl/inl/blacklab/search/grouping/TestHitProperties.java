package nl.inl.blacklab.search.grouping;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import nl.inl.blacklab.forwardindex.Terms;
import nl.inl.blacklab.mocks.MockTerms;
import nl.inl.blacklab.resultproperty.HitProperty;
import nl.inl.blacklab.resultproperty.HitPropertyContextWords;
import nl.inl.blacklab.resultproperty.HitPropertyHitText;
import nl.inl.blacklab.resultproperty.PropertyValueContext;
import nl.inl.blacklab.resultproperty.PropertyValueContextWords;
import nl.inl.blacklab.search.BlackLabIndex;
import nl.inl.blacklab.search.indexmetadata.Annotation;
import nl.inl.blacklab.search.indexmetadata.MatchSensitivity;
import nl.inl.blacklab.search.results.HitGroup;
import nl.inl.blacklab.search.results.HitGroups;
import nl.inl.blacklab.search.results.Hits;
import nl.inl.blacklab.search.results.Results;
import nl.inl.blacklab.testutil.TestIndex;

@RunWith(Parameterized.class)
public class TestHitProperties {

    private final static int NO_TERM = Terms.NO_TERM;

    @Parameterized.Parameters(name = "index type {0}")
    public static Collection<TestIndex> typeToUse() {
        return TestIndex.typesForTests();
    }

    @Parameterized.Parameter
    public TestIndex testIndex;

    private BlackLabIndex index;

    private Terms terms;

    private Annotation wordAnnotation;

    @Before
    public void setUp() {
        index = testIndex.index();
        wordAnnotation = index.mainAnnotatedField().annotation("word");
        terms = index.annotationForwardIndex(wordAnnotation).terms();
    }

    private int term(String word) {
        return terms.indexOf(word);
    }

    @Test
    public void testHitPropHitText() {
        Hits hits = testIndex.find(" 'the' ");
        HitProperty p = new HitPropertyHitText(index, MatchSensitivity.SENSITIVE);
        HitGroups g = hits.group(p, Results.NO_LIMIT);
        HitGroup group = g.get(new PropertyValueContextWords(index, wordAnnotation, MatchSensitivity.SENSITIVE, new int[] { term("the") }, false));
        Assert.assertEquals(3, group.size());
        group = g.get(new PropertyValueContextWords(index, wordAnnotation, MatchSensitivity.SENSITIVE, new int[] { term("The") }, false));
        Assert.assertEquals(1, group.size());
    }

    @Test
    public void testHitPropContextWords() {
        Hits hits = testIndex.find(" 'the' ");
        HitProperty p = new HitPropertyContextWords(index, wordAnnotation, MatchSensitivity.SENSITIVE, "L1-1;H1-2");
        HitGroups g = hits.group(p, Results.NO_LIMIT);
        Assert.assertEquals(4, g.size());
        HitGroup group;
        group = g.get(
                new PropertyValueContextWords(index, wordAnnotation, MatchSensitivity.SENSITIVE, new int[] { NO_TERM, term("The"), NO_TERM }, false));
        Assert.assertEquals(1, group.size());
        group = g.get(
                new PropertyValueContextWords(index, wordAnnotation, MatchSensitivity.SENSITIVE, new int[] { term("over"), term("the"), NO_TERM }, false));
        Assert.assertEquals(1, group.size());
        group = g.get(
                new PropertyValueContextWords(index, wordAnnotation, MatchSensitivity.SENSITIVE, new int[] { term("May"), term("the"), NO_TERM }, false));
        Assert.assertEquals(1, group.size());
        group = g.get(
                new PropertyValueContextWords(index, wordAnnotation, MatchSensitivity.SENSITIVE, new int[] { term("is"), term("the"), NO_TERM }, false));
        Assert.assertEquals(1, group.size());
    }

    @Test
    public void testHitPropContextWordsReverse() {
        Hits hits = testIndex.find(" 'the' 'lazy' ");
        HitProperty p = new HitPropertyContextWords(index, wordAnnotation, MatchSensitivity.SENSITIVE, "L1;H2-1;R1");
        HitGroups g = hits.group(p, Results.NO_LIMIT);
        Assert.assertEquals(1, g.size());
        HitGroup group;
        group = g.get(new PropertyValueContextWords(index, wordAnnotation,
                MatchSensitivity.SENSITIVE, new int[] { term("over"), term("lazy"), term("the"), term("dog") }, false));
        Assert.assertEquals(1, group.size());
    }

    @Test
    public void testTermSerialization() {
        String[] words = { "aap", "~", "~~", "" };
        String[] expected = { "aap", "~~", "~~~", "" };
        MockTerms mockTerms = new MockTerms(words);
        for (int i = 0; i < mockTerms.numberOfTerms(); i++) {
            Assert.assertEquals(expected[i], PropertyValueContext.serializeTerm(mockTerms, i));
            Assert.assertEquals(expected[i], PropertyValueContext.serializeTerm(mockTerms, PropertyValueContext.deserializeToken(mockTerms, expected[i])));
        }
        Assert.assertEquals("~", PropertyValueContext.serializeTerm(mockTerms, Terms.NO_TERM));
        Assert.assertEquals("~", PropertyValueContext.serializeTerm(mockTerms, PropertyValueContext.deserializeToken(mockTerms, "~")));
    }

}
