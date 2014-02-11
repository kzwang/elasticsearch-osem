package com.github.kzwang.osem.processor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.kzwang.osem.annotations.Indexable;
import com.github.kzwang.osem.cache.CacheType;
import com.github.kzwang.osem.cache.OsemCache;
import com.github.kzwang.osem.exception.ElasticSearchOsemException;
import com.github.kzwang.osem.jackson.JacksonElasticSearchOsemModule;
import com.github.kzwang.osem.utils.OsemReflectionUtils;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

import java.lang.reflect.Field;


/**
 * Serialize/Deserialize object using Jackson
 */
public class ObjectProcessor {

    private static final ESLogger logger = Loggers.getLogger(ObjectProcessor.class);

    private ObjectMapper serializeMapper;

    private ObjectMapper deSerializeMapper;

    private OsemCache osemCache;


    public ObjectProcessor() {
        osemCache = OsemCache.getInstance();
        initSerializeMapper();
        initDeSerializeMapper();
    }


    private void initSerializeMapper() {
        serializeMapper = new ObjectMapper();
        serializeMapper.setVisibilityChecker(serializeMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.ANY)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE));
        serializeMapper.registerModule(new JacksonElasticSearchOsemModule());
        serializeMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        serializeMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    private void initDeSerializeMapper() {
        deSerializeMapper = new ObjectMapper();
        deSerializeMapper.setVisibilityChecker(deSerializeMapper.getDeserializationConfig().getDefaultVisibilityChecker()
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE));
        deSerializeMapper.registerModule(new JacksonElasticSearchOsemModule());
        deSerializeMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Serialize object to json string
     *
     * @param object object to serialize
     * @return json string of the object
     */
    public String toJsonString(Object object) {
        try {
            return serializeMapper.writeValueAsString(object);
        } catch (Exception ex) {
            throw new ElasticSearchOsemException("Failed to convert object to json string", ex);
        }

    }


    /**
     * Deserialize object to json string
     *
     * @param string json string to deserialize
     * @param clazz  Class to deserialize to
     * @return object
     */
    public <T> T fromJsonString(String string, Class<T> clazz) {
        try {
            return deSerializeMapper.readValue(string, clazz);
        } catch (Exception ex) {
            throw new ElasticSearchOsemException("Failed to convert object from json string", ex);
        }
    }


    /**
     * Get the id of the object
     *
     * @param object object to get id
     * @return id value
     */
    public Object getIdValue(Object object) {
        Field idField = (Field) osemCache.getCache(CacheType.ID_FIELD, object.getClass());  // try cache first
        if (idField == null) {
            idField = OsemReflectionUtils.getIdField(object.getClass());
            if (idField == null) {
                throw new ElasticSearchOsemException("Can't find id field for class: " + object.getClass().getSimpleName());
            }
            osemCache.putCache(CacheType.ID_FIELD, object.getClass(), idField);
        }
        return OsemReflectionUtils.getFieldValue(object, idField);
    }

    /**
     * Get the routing id of the object
     *
     * @param object object to get routing id
     * @return routing id
     */
    public String getRoutingId(Object object) {
        Class clazz = object.getClass();
        if (osemCache.isExist(CacheType.ROUTING_PATH, clazz)) {
            String path = (String) osemCache.getCache(CacheType.ROUTING_PATH, clazz);
            if (path == null || path.isEmpty()) return null;
            return getValueByPath(object, path);
        }
        Indexable indexable = (Indexable) clazz.getAnnotation(Indexable.class);
        if (indexable == null) {
            throw new ElasticSearchOsemException("Class " + object.getClass().getSimpleName() + " is no Indexable");
        }
        String path = indexable.routingFieldPath();
        osemCache.putCache(CacheType.ROUTING_PATH, clazz, path);
        if (!path.isEmpty()) {
            return getValueByPath(object, path);
        }
        return null;
    }

    /**
     * Get the parent id of the object
     *
     * @param object object to get parent id
     * @return parent id
     */
    public String getParentId(Object object) {
        Class clazz = object.getClass();
        if (osemCache.isExist(CacheType.PARENT_PATH, clazz)) {
            String path = (String) osemCache.getCache(CacheType.PARENT_PATH, clazz);
            if (path == null || path.isEmpty()) return null;
            return getValueByPath(object, path);
        }
        Indexable indexable = (Indexable) clazz.getAnnotation(Indexable.class);
        if (indexable == null) {
            throw new ElasticSearchOsemException("Class " + object.getClass().getSimpleName() + " is no Indexable");
        }
        String path = indexable.parentPath();
        osemCache.putCache(CacheType.PARENT_PATH, clazz, path);
        if (!path.isEmpty()) {
            return getValueByPath(object, path);
        }
        return null;
    }

    private String getValueByPath(Object object, String path) {
        String[] fieldNames = path.split("\\.");
        Object parentObject = object;
        for (String fieldName : fieldNames) {
            parentObject = OsemReflectionUtils.getFieldValue(parentObject, fieldName);
            if (parentObject == null) return null;
        }
        return parentObject.toString();
    }

}
