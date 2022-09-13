package io.github.joeljeremy7.deezpatch.core.internal;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * A simple weak concurrent map implementation which only holds weak references
 * to the keys.
 */
public class WeakConcurrentMap<K,V> {
    private final ConcurrentMap<WeakConcurrentMap.WeakKey<K>, V> map = new ConcurrentHashMap<>();
    private final ReferenceQueue<K> referenceQueue = new ReferenceQueue<>();

    /**
     * Get the value mapped to the key.
     * 
     * @param key The key.
     * @return The value mapped to the key. Otherwise, {@code null} if no mapping exists.
     */
    public @Nullable V get(K key) {
        purgeKeys();
        return map.get(WeakKey.forLookup(key));
    }

    /**
     * If the specified key is not already associated with a value (or is mapped to null), 
     * attempts to compute its value using the given mapping function and enters it into 
     * this map unless null.
     * 
     * @param key The key which the computed value is to be associated.
     * @param mappingFunction The mapping function to compute a value.
     * @return The current (existing or computed) value associated with the specified key, 
     * or {@code null} if the computed value is {@code null}.
     */
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return map.computeIfAbsent(
            WeakKey.forWrite(key, referenceQueue), 
            k -> mappingFunction.apply(k.get())
        );
    }

    /**
     * If the specified key is not already associated with a value, associates it with 
     * the given value.
     * 
     * @param key The key which the specified value is to be associated.
     * @param value The value to be associated with the specifeid key.
     * @return The previous value associated with the specified key, or null if there was 
     * no mapping for the key.
     */
    public V putIfAbsent(K key, V value) {
        purgeKeys();
        return map.putIfAbsent(WeakKey.forWrite(key, referenceQueue), value);
    }

    private void purgeKeys() {
        Reference<? extends K> reference;
        while ((reference = referenceQueue.poll()) != null) {
            map.remove(reference);
        }
    }

    /**
     * Package-private weak map key.
     * 
     * @param <K> The key type.
     */
    static class WeakKey<K> extends WeakReference<K> {
        private final int hashCode;

        /**
         * Private constructor.
         * 
         * @param referent The referent.
         * @param referenceQueue The reference queue.
         */
        private WeakKey(K referent, @Nullable ReferenceQueue<? super K> referenceQueue) {
            super(referent, referenceQueue);
            hashCode = Objects.hashCode(referent);
        }

        /**
         * Create a {@link WeakKey} to be used when doing lookups in the weak map.
         * 
         * @param <K> The key type.
         * @param referent The referent.
         * @return A {@link WeakKey} to be used in map lookups.
         */
        static <K> WeakKey<K> forLookup(K referent) {
            return new WeakKey<>(referent, null);
        }

        /**
         * Create a {@link WeakKey} to be used when writing to the weak map.
         * 
         * @param <K> The key type.
         * @param referent The referent.
         * @param referenceQueue The reference queue.
         * @return A {@link WeakKey} to be used in map writes.
         */
        static <K> WeakKey<K> forWrite(
                K referent, 
                @Nullable ReferenceQueue<? super K> referenceQueue
        ) {
            return new WeakKey<>(referent, referenceQueue);
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof WeakKey) {
                @SuppressWarnings("unchecked")
                WeakKey<K> other = (WeakKey<K>)obj;
                if (Objects.equals(super.get(), other.get())) {
                    return true;
                }
            }
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}