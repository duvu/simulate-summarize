package com.deemerge.enrichment.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom cache manager that implements a per-tenant LRU cache with limited size
 */
@Component
public class TenantAwareCacheManager implements CacheManager {

    private final Map<String, Cache> caches = new ConcurrentHashMap<>();

    @Override
    public Cache getCache(String name) {
        return caches.computeIfAbsent(name, n -> {
            if ("summaryCache".equals(n)) {
                return new TenantAwareLruCache(n, 5);
            } else {
                return new ConcurrentMapCache(n);
            }
        });
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(caches.keySet());
    }

    /**
     * Custom cache implementation that maintains separate LRU caches for each tenant
     */
    static class TenantAwareLruCache implements Cache {
        private final String name;
        private final int maxSize;
        private final Map<String, Map<Object, Object>> tenantCaches = new ConcurrentHashMap<>();

        public TenantAwareLruCache(String name, int maxSize) {
            this.name = name;
            this.maxSize = maxSize;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Object getNativeCache() {
            return tenantCaches;
        }

        @Override
        public ValueWrapper get(Object key) {
            if (!(key instanceof String)) {
                return null;
            }

            String keyStr = (String) key;
            String tenantId = extractTenantId(keyStr);
            Object cacheKey = extractCacheKey(keyStr);

            Map<Object, Object> tenantCache = tenantCaches.get(tenantId);
            if (tenantCache == null) {
                return null;
            }

            Object value = tenantCache.get(cacheKey);
            return value != null ? new SimpleValueWrapper(value) : null;
        }

        @Override
        public <T> T get(Object key, Class<T> type) {
            ValueWrapper wrapper = get(key);
            return wrapper != null ? type.cast(wrapper.get()) : null;
        }

        @Override
        public <T> T get(Object key, Callable<T> valueLoader) {
            ValueWrapper wrapper = get(key);
            if (wrapper != null) {
                Object value = wrapper.get();
                if (value != null) {
                    @SuppressWarnings("unchecked")
                    T typedValue = (T) value;
                    return typedValue;
                }
                return null;
            }

            try {
                T value = valueLoader.call();
                put(key, value);
                return value;
            } catch (Exception e) {
                throw new Cache.ValueRetrievalException(key, valueLoader, e);
            }
        }

        @Override
        public void put(Object key, Object value) {
            if (!(key instanceof String)) {
                return;
            }

            String keyStr = (String) key;
            String tenantId = extractTenantId(keyStr);
            Object cacheKey = extractCacheKey(keyStr);

            // Get or create tenant-specific cache with LRU eviction
            Map<Object, Object> tenantCache = tenantCaches.computeIfAbsent(tenantId, k -> {
                return Collections.synchronizedMap(new LinkedHashMap<Object, Object>(maxSize + 1, 0.75f, true) {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
                        return size() > maxSize;
                    }
                });
            });

            // Add to tenant-specific cache
            tenantCache.put(cacheKey, value);
        }

        @Override
        public void evict(Object key) {
            if (!(key instanceof String)) {
                return;
            }

            String keyStr = (String) key;
            String tenantId = extractTenantId(keyStr);
            Object cacheKey = extractCacheKey(keyStr);

            Map<Object, Object> tenantCache = tenantCaches.get(tenantId);
            if (tenantCache != null) {
                tenantCache.remove(cacheKey);
            }
        }

        @Override
        public void clear() {
            tenantCaches.clear();
        }

        // Extract tenant ID from a composite key (format: "tenantId-hashCode")
        private String extractTenantId(String key) {
            int separatorIndex = key.indexOf('-');
            return separatorIndex > 0 ? key.substring(0, separatorIndex) : key;
        }

        // Extract the actual cache key without tenant ID
        private Object extractCacheKey(String key) {
            int separatorIndex = key.indexOf('-');
            return separatorIndex > 0 ? key.substring(separatorIndex + 1) : key;
        }
    }
}
