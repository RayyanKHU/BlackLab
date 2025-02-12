package nl.inl.blacklab.search.results;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import nl.inl.blacklab.resultproperty.HitProperty;

/**
 * A HitsInternal implementation that locks and can handle huge result sets.
 */
class HitsInternalLock extends HitsInternalNoLock {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    HitsInternalLock(long initialCapacity) {
        super(initialCapacity);
    }

    public void add(int doc, int start, int end) {
        this.lock.writeLock().lock();
        try {
            // Don't call super method, this is faster (hot code)
            docs.add(doc);
            starts.add(start);
            ends.add(end);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Add the hit to the end of this list, copying the values. The hit object itself is not retained.
     */
    public void add(EphemeralHit hit) {
        this.lock.writeLock().lock();
        try {
            // Don't call super method, this is faster (hot code)
            docs.add(hit.doc);
            starts.add(hit.start);
            ends.add(hit.end);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Add the hit to the end of this list, copying the values. The hit object itself is not retained.
     */
    public void add(Hit hit) {
        this.lock.writeLock().lock();
        try {
            // Don't call super method, this is faster (hot code)
            docs.add(hit.doc());
            starts.add(hit.start());
            ends.add(hit.end());
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void addAll(HitsInternalLock hits) {
        this.lock.writeLock().lock();
        try {
            hits.lock.readLock().lock();
            try {
                super.addAll(hits);
            } finally {
                hits.lock.readLock().unlock();
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void addAll(HitsInternal hits) {
        this.lock.writeLock().lock();
        try {
            super.addAll(hits);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Clear the arrays.
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            super.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void withReadLock(Consumer<HitsInternal> cons) {
        lock.readLock().lock();
        try {
            super.withReadLock(cons);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Hit get(long index) {
        lock.readLock().lock();
        try {
            // Don't call super method, this is faster (hot code)
            return new HitImpl(docs.getInt((int) index), starts.getInt((int) index), ends.getInt((int) index));
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Copy values into the ephemeral hit, for use in a hot loop or somesuch.
     * The intent of this function is to allow retrieving many hits without needing to allocate so many short lived objects.
     * Example:
     *
     * <pre>
     * EphemeralHitImpl h = new EphemeralHitImpl();
     * int size = hits.size();
     * for (int i = 0; i < size; ++i) {
     *     hits.getEphemeral(i, h);
     *     // use h now
     * }
     * </pre>
     */
    public void getEphemeral(long index, EphemeralHit h) {
        lock.readLock().lock();
        try {
            h.doc = docs.getInt(index);
            h.start = starts.getInt(index);
            h.end = ends.getInt(index);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int doc(long index) {
        lock.readLock().lock();
        try {
            // Don't call super method, this is faster (hot code)
            return this.docs.getInt(index);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int start(long index) {
        lock.readLock().lock();
        try {
            // Don't call super method, this is faster (hot code)
            return this.starts.getInt(index);
        } finally {
            lock.readLock().unlock();
        }
    }

    public int end(long index) {
        lock.readLock().lock();
        try {
            // Don't call super method, this is faster (hot code)
            return this.ends.getInt(index);
        } finally {
            lock.readLock().unlock();
        }
    }

    public long size() {
        lock.readLock().lock();
        try {
            return super.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public HitsInternal sort(HitProperty p) {
        this.lock.readLock().lock();
        try {
            return super.sort(p);
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
