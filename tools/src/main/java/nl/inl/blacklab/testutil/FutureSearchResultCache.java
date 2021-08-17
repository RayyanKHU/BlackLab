package nl.inl.blacklab.testutil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import nl.inl.blacklab.search.BlackLabIndex;
import nl.inl.blacklab.search.results.SearchResult;
import nl.inl.blacklab.searches.Search;
import nl.inl.blacklab.searches.SearchCache;

/**
 * A cache containing [future] results for searches.
 *
 * Searches are executed in their own thread and may be interrupted half-way.
 * See {@link FutureSearchResult}.
 */
public class FutureSearchResultCache implements SearchCache {

    protected Map<Search<?>, Future<? extends SearchResult>> searches = new HashMap<>();

    protected boolean trace = false;

    public boolean isTrace() {
        return trace;
    }

    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends SearchResult> Future<R> getAsync(Search<R> search) {
        Future<R> future;
        synchronized (searches) {
            future = (Future<R>)searches.get(search);
            if (future == null) {
                future = new FutureSearchResult<>(search);
                searches.put(search, future);
                if (trace)
                    System.out.println("ADDED: " + search);
            } else {
                if (trace)
                    System.out.println("FOUND: " + search);
            }
        }
        return future;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends SearchResult> Future<R> remove(Search<R> search) {
        Future<R> future;
        synchronized (searches) {
            future = (Future<R>)searches.remove(search);
            if (trace)
                System.out.println("REMOVED: " + search);
        }
        return future;
    }

    @Override
    public void removeSearchesForIndex(BlackLabIndex index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear(boolean terminateRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cleanup() {
        // NOP
    }
}