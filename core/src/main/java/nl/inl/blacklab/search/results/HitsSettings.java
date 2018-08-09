package nl.inl.blacklab.search.results;

import nl.inl.blacklab.search.ConcordanceType;

public final class HitsSettings {
    
    public static HitsSettings defaults() {
        return new HitsSettings();
    }

    /** When setting how many hits to retrieve/count, this means "no limit". */
    private static final int UNLIMITED_HITS = -1;
    
    public static final int DEFAULT_MAX_RETRIEVE = 1000000;
    
    public static final int DEFAULT_MAX_COUNT = UNLIMITED_HITS;
    
    /** Annotated field name for default contents field */
    public static final String DEFAULT_CONTENTS_FIELD_NAME = "contents";
    
    private static final ConcordanceType DEFAULT_CONC_TYPE = ConcordanceType.CONTENT_STORE;
    
    private static final int DEFAULT_CONTEXT_SIZE = 5;

    /**
     * Stop retrieving hits after this number. (NO_LIMIT = -1 = don't stop
     * retrieving)
     */
    private int maxHitsToRetrieve;

    /**
     * Stop counting hits after this number. (NO_LIMIT = -1 = don't stop counting)
     */
    private int maxHitsToCount;

    /** What to use to make concordances: forward index or content store */
    private ConcordanceType concsType;

    /** Our desired context size */
    private int desiredContextSize;

    /**
     * Get a (non-frozen) copy of a HitsSettings instance.
     * 
     * @param copyFrom instance to copy
     */
    private HitsSettings(HitsSettings copyFrom) {
        maxHitsToRetrieve = copyFrom.maxHitsToRetrieve();
        maxHitsToCount = copyFrom.maxHitsToCount();
        concsType = copyFrom.concordanceType();
        desiredContextSize = copyFrom.contextSize();
    }

    /**
     * Get default hits settings for an index
     * @param index index
     */
    private HitsSettings() {
        maxHitsToRetrieve = DEFAULT_MAX_RETRIEVE;
        maxHitsToCount = DEFAULT_MAX_COUNT;
        concsType = DEFAULT_CONC_TYPE;
        desiredContextSize = DEFAULT_CONTEXT_SIZE;
    }
    
    private HitsSettings copy() {
        return new HitsSettings(this);
    }

    /** @return the maximum number of hits to retrieve. */
    public int maxHitsToRetrieve() {
        return maxHitsToRetrieve;
    }

    /** @return the maximum number of hits to count. */
    public int maxHitsToCount() {
        return maxHitsToCount;
    }

    /**
     * Are we making concordances using the forward index (true) or using the
     * content store (false)? Forward index is more efficient but returns
     * concordances that don't include XML tags.
     *
     * @return true iff we use the forward index for making concordances.
     */
    public ConcordanceType concordanceType() {
        return concsType;
    }

    public int contextSize() {
        return desiredContextSize;
    }
    
    
    // Methods that mutate data
    //------------------------------------------------------------------------

    /**
     * Get settings with different maximum number of hits to retrieve
     * 
     * @param n the number of hits, or HitsSettings.UNLIMITED for no limit
     * @return settings object
     */
    public HitsSettings withMaxHitsToRetrieve(int n) {
        HitsSettings x = copy();
        x.maxHitsToRetrieve = n;
        return x;
    }

    /**
     * Get settings with different maximum number of hits to count
     * 
     * @param n the number of hits, or HitsSettings.UNLIMITED for no limit
     * @return settings object
     */
    public HitsSettings withMaxHitsToCount(int n) {
        HitsSettings x = copy();
        x.maxHitsToCount = n;
        return x;
    }

    /**
     * Get settings with different context size.
     * 
     * @param n context size 
     * @return settings object
     */
    public HitsSettings withContextSize(int n) {
        HitsSettings x = copy();
        x.desiredContextSize = n;
        return x;
    }
    
    /**
     * Do we want to retrieve concordances from the forward index or from the
     * content store? Forward index is more efficient but doesn't exactly reproduces
     * the original XML.
     *
     * The default type can be set by calling Searcher.setDefaultConcordanceType().
     *
     * @param type the type of concordances to make
     * @return settings object
     */
    public HitsSettings withConcordanceType(ConcordanceType type) {
        HitsSettings x = copy();
        x.concsType = type;
        return x;
    }

}
