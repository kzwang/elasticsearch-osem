package com.github.kzwang.osem;


import org.elasticsearch.common.collect.Maps;

import java.lang.reflect.Field;
import java.util.Map;


public class OsemCache {

    private static OsemCache instance = null;

    private Map<Class, String> mappingCache = Maps.newHashMap();

    private Map<Class, Field> idFieldCache = Maps.newHashMap();

    public static OsemCache getInstance() {
        if (instance == null) {
            instance = new OsemCache();
        }
        return instance;
    }

    public void addMappingCache(Class clazz, String mapping) {
        mappingCache.put(clazz, mapping);
    }

    public boolean isMappingExist(Class clazz) {
        return mappingCache.containsKey(clazz);
    }

    public void deleteMappingCache(Class clazz) {
        mappingCache.remove(clazz);
    }

    public void addIdFieldCache(Class clazz, Field field) {
        idFieldCache.put(clazz, field);
    }

    public Field getIdFieldCache(Class clazz) {
        return idFieldCache.get(clazz);
    }

}
