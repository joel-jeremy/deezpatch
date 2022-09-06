package io.github.joeljeremy7.deezpatch.commands;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WeakConcurrentHashMap<K, V> implements ConcurrentMap<K, V> {

    private final ConcurrentMap<WeakKey<K>, V> map = new ConcurrentHashMap<>();
    private final ReferenceQueue<K> referenceQueue = new ReferenceQueue<>();

    @Override
    public int size() {
        purgeKeys();
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        purgeKeys();
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        purgeKeys();
        return map.containsKey(new WeakKey<>(key));
    }

    @Override
    public boolean containsValue(Object value) {
        purgeKeys();
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        purgeKeys();
        return map.get(new WeakKey<>(key));
    }

    @Override
    public V put(K key, V value) {
        purgeKeys();
        return map.put(new WeakKey<>(key, referenceQueue), value);
    }

    @Override
    public V remove(Object key) {
        purgeKeys();
        return map.remove(new WeakKey<>(key));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            map.put(new WeakKey<>(entry.getKey(), referenceQueue), entry.getValue());
        }
    }

    @Override
    public void clear() {
        purgeKeys();
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return new AbstractSet<K>() {
            @Override
            public WeakSafeIterator<K, WeakKey<K>> iterator() {
                purgeKeys();
                return new WeakSafeIterator<K, WeakKey<K>>(map.keySet().iterator()) {
                    @Override
                    protected K extract(WeakKey<K> u) {
                        return u.get();
                    }
                };
            }

            @Override
            public boolean contains(Object o) {
                return WeakConcurrentHashMap.this.containsKey(o);
            }

            @Override
            public int size() {
                return map.size();
            }
        };
    }

    @Override
    public Collection<V> values() {
        purgeKeys();
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                purgeKeys();
                return new WeakSafeIterator<Entry<K, V>, Entry<WeakKey<K>, V>>(map.entrySet().iterator()) {
                    @Override
                    protected Entry<K, V> extract(Entry<WeakKey<K>, V> u) {
                        K key = u.getKey().get();
                        if (key == null) {
                            return null;
                        } else {
                            return new SimpleEntry<K, V>(key, u.getValue());
                        }
                    }
                };
            }

            @Override
            public int size() {
                return map.size();
            }
        };
    }

    @Override
    public V putIfAbsent(K key, V value) {
        purgeKeys();
        return map.putIfAbsent(new WeakKey<>(key, referenceQueue), value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        purgeKeys();
        return map.remove(new WeakKey<>(key), value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        purgeKeys();
        return map.replace(new WeakKey<>(key, referenceQueue), oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        purgeKeys();
        return map.replace(new WeakKey<>(key, referenceQueue), value);
    }

    private void purgeKeys() {
        Reference<? extends K> reference;
        while ((reference = referenceQueue.poll()) != null) {
            map.remove(reference);
        }
    }

    private static class WeakKey<K> extends WeakReference<K> {
        private final int hashCode;

        public WeakKey(K referent, ReferenceQueue<? super K> referenceQueue) {
            super(referent, referenceQueue);
            hashCode = referent.hashCode();
        }

        public WeakKey(K referent) {
            this(referent, null);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof WeakKey) {
                @SuppressWarnings("unchecked")
                WeakKey<K> other = (WeakKey<K>)obj;
                if (Objects.equals(get(), other.get())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    private static abstract class WeakSafeIterator<T, U> implements Iterator<T> {

        private final Iterator<U> weakIterator;
        protected T strongNext;

        public WeakSafeIterator(Iterator<U> weakIterator) {
            this.weakIterator = weakIterator;
            advance();
        }

        private void advance() {
            while (weakIterator.hasNext()) {
                U nextU = weakIterator.next();
                if ((strongNext = extract(nextU)) != null) {
                    return;
                }
            }
            strongNext = null;
        }

        @Override
        public boolean hasNext() {
            return strongNext != null;
        }

        @Override
        public final T next() {
            T next = strongNext;
            advance();
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        protected abstract T extract(U u);
    }
}
