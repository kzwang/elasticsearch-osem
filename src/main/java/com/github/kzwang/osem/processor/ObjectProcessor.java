package com.github.kzwang.osem.processor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.kzwang.osem.OsemCache;
import com.github.kzwang.osem.annotations.Indexable;
import com.github.kzwang.osem.jackson.JacksonElasticSearchOsemModule;
import com.github.kzwang.osem.utils.OsemReflectionUtils;
import org.elasticsearch.common.Preconditions;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

import java.lang.reflect.Field;


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

    public String toJsonString(Object object) {
        try {
            return serializeMapper.writeValueAsString(object);
        } catch (Exception ex) {
            logger.error("Failed to convert object to json string", ex);
        }
        return null;

    }


    public <T> T fromJsonString(String string, Class<T> clazz) {
        try {
            return deSerializeMapper.readValue(string, clazz);
        } catch (Exception ex) {
            logger.error("Failed to convert object from json string", ex);
        }
        return null;

    }


    public Object getIdValue(Object object) throws NoSuchFieldException, IllegalAccessException {
        Field idField = osemCache.getIdFieldCache(object.getClass());
        if (idField == null) {
            idField = OsemReflectionUtils.getIdField(object.getClass());
            Preconditions.checkNotNull(idField, "Can't find id field for class: {}", object.getClass().getSimpleName());
            osemCache.addIdFieldCache(object.getClass(), idField);
        }
        return OsemReflectionUtils.getFieldValue(object, idField);
    }

    public String getParentId(Object object) throws NoSuchFieldException, IllegalAccessException {
        if (object == null) return null;
        Indexable indexable = object.getClass().getAnnotation(Indexable.class);
        if (indexable == null || indexable.parentIdField().isEmpty() || indexable.parentClass() == void.class)
            return null;
        String[] fieldNames = indexable.parentIdField().split("\\.");
        Object parentObject = object;
        for (String fieldName : fieldNames) {
            parentObject = OsemReflectionUtils.getFieldValue(parentObject, fieldName);
        }
        if (parentObject == null) return null;
        return parentObject.toString();
    }

}
