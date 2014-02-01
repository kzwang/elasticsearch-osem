package com.github.kzwang.osem.cache;


import org.elasticsearch.common.collect.Maps;

import java.util.HashMap;

/**
 * Cache mapping, fields etc.
 */
public class OsemCache {

    private static OsemCache instance = null;


    private HashMap<CacheType, HashMap<Object, Object>> cache = Maps.newHashMap();


    /**
     * Get Singleton {@link OsemCache} cache instance
     *
     * @return instance
     */
    public static OsemCache getInstance() {
        if (instance == null) {
            instance = new OsemCache();
        }
        return instance;
    }

    public void putCache(CacheType cacheType, Object key, Object value) {
        if (!cache.containsKey(cacheType)) {
            cache.put(cacheType, Maps.newHashMap());
        }
        cache.get(cacheType).put(key, value);
    }

    public boolean isExist(CacheType cacheType, Object key) {
        return cache.containsKey(cacheType) && cache.get(cacheType).containsKey(key);
    }

    public Object getCache(CacheType cacheType, Object key) {
        if (!cache.containsKey(cacheType)) {
            return null;
        }
        return cache.get(cacheType).get(key);
    }

    public void removeCache(CacheType cacheType, Object key) {
        if (cache.containsKey(cacheType)) {
            cache.get(cacheType).remove(key);
        }
    }


}
