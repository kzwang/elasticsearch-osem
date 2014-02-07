package com.github.kzwang.osem.processor;

import com.github.kzwang.osem.annotations.*;
import com.github.kzwang.osem.cache.CacheType;
import com.github.kzwang.osem.cache.OsemCache;
import com.github.kzwang.osem.exception.ElasticSearchOsemException;
import com.github.kzwang.osem.utils.OsemReflectionUtils;
import com.google.common.base.CaseFormat;
import org.elasticsearch.common.Preconditions;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.collect.Maps;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static org.reflections.ReflectionUtils.*;

/**
 * Utils for mapping related operations
 */
public class MappingProcessor {

    private static final ESLogger logger = Loggers.getLogger(MappingProcessor.class);

    private static final OsemCache osemCache = OsemCache.getInstance();

    /**
     * Get the mapping for class
     *
     * @param clazz class to get mapping
     * @return map of the mapping
     */
    public static Map<String, Object> getMapping(Class clazz) {
        String indexableName = getIndexTypeName(clazz);

        Map<String, Object> indexableMap = getIndexableMap(clazz);
        indexableMap.put("properties", getPropertiesMap(clazz));


        Map<String, Object> mapping = Maps.newHashMap();
        mapping.put(indexableName, indexableMap);
        return mapping;

    }

    /**
     * Get the mapping for class
     *
     * @param clazz class to get mapping
     * @return mapping string
     */
    public static String getMappingAsJson(Class clazz) {
        Map<String, Object> mappingMap = getMapping(clazz);
        if (mappingMap != null) {
            try {
                XContentBuilder builder = XContentFactory.contentBuilder(XContentType.JSON);
                builder.map(mappingMap);
                return builder.string();
            } catch (IOException e) {
                logger.error("Failed to convert mapping to JSON string", e);
            }
        }
        return null;
    }

    /**
     * Get the index type name for class
     *
     * @param clazz class to get type name
     * @return index type name
     */
    public static String getIndexTypeName(Class clazz) {
        if (osemCache.isExist(CacheType.INDEX_TYPE_NAME, clazz)) {
            return (String) osemCache.getCache(CacheType.INDEX_TYPE_NAME, clazz);
        }
        String typeName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName());
        Indexable indexable = (Indexable) clazz.getAnnotation(Indexable.class);
        if (indexable != null && indexable.name() != null && !indexable.name().isEmpty()) {
            typeName = indexable.name();
        }
        osemCache.putCache(CacheType.INDEX_TYPE_NAME, clazz, typeName);
        return typeName;
    }

    private static Map<String, Object> getPropertiesMap(Class clazz) {
        Map<String, Object> propertiesMap = Maps.newHashMap();

        // process IndexableProperty
        Set<Field> indexablePropertyFields = getAllFields(clazz, withAnnotation(IndexableProperty.class));
        Set<Method> indexablePropertyMethods = getAllMethods(clazz, withAnnotation(IndexableProperty.class));
        if (!indexablePropertyFields.isEmpty()) {
            for (Field field : indexablePropertyFields) {
                processIndexableProperty(field, propertiesMap);
            }
        }
        if (!indexablePropertyMethods.isEmpty()) {
            for (Method method : indexablePropertyMethods) {
                processIndexableProperty(method, propertiesMap);
            }
        }


        // process IndexableComponent
        Set<Field> indexableComponentFields = getAllFields(clazz, withAnnotation(IndexableComponent.class));
        Set<Method> indexableComponentMethods = getAllMethods(clazz, withAnnotation(IndexableComponent.class));
        if (!indexableComponentFields.isEmpty()) {
            for (Field field : indexableComponentFields) {
                processIndexableComponent(field, propertiesMap);
            }
        }
        if (!indexableComponentMethods.isEmpty()) {
            for (Method method : indexableComponentMethods) {
                processIndexableComponent(method, propertiesMap);
            }
        }

        // process IndexableProperties
        Set<Field> indexablePropertiesFields = getAllFields(clazz, withAnnotation(IndexableProperties.class));
        Set<Method> indexablePropertiesMethods = getAllMethods(clazz, withAnnotation(IndexableProperties.class));
        if (!indexablePropertiesFields.isEmpty()) {
            for (Field field : indexablePropertiesFields) {
                processIndexableProperties(field, propertiesMap);
            }
        }
        if (!indexablePropertiesMethods.isEmpty()) {
            for (Method method : indexablePropertiesMethods) {
                processIndexableProperties(method, propertiesMap);
            }
        }
        return propertiesMap;
    }

    private static Map<String, Object> getIndexableMap(Class clazz) {
        Map<String, Object> objectMap = Maps.newHashMap();

        Indexable indexable = (Indexable) clazz.getAnnotation(Indexable.class);
        Preconditions.checkNotNull(indexable, "Class {} is not Indexable", clazz.getName());

        if (!indexable.indexAnalyzer().isEmpty()) {
            objectMap.put("index_analyzer", indexable.indexAnalyzer());
        }

        if (!indexable.searchAnalyzer().isEmpty()) {
            objectMap.put("search_analyzer", indexable.searchAnalyzer());
        }

        if (indexable.dynamicDateFormats().length > 0) {
            objectMap.put("dynamic_date_formats", Lists.newArrayList(indexable.dynamicDateFormats()));
        }

        if (!indexable.dateDetection().equals(DateDetectionEnum.NA)) {
            objectMap.put("date_detection", Boolean.valueOf(indexable.dateDetection().toString()));
        }

        if (!indexable.numericDetection().equals(NumericDetectionEnum.NA)) {
            objectMap.put("numeric_detection", Boolean.valueOf(indexable.numericDetection().toString()));
        }

        // handle _parent
        if (indexable.parentClass() != void.class) {
            Map<String, Object> parentMap = Maps.newHashMap();
            parentMap.put("type", getIndexTypeName(indexable.parentClass()));
            objectMap.put("_parent", parentMap);
        }

        // handle _id
        Field indexableIdField = OsemReflectionUtils.getIdField(clazz);
        Map<String, Object> idMap = getIndexableIdMap(indexableIdField);
        if (!idMap.isEmpty()) {
            objectMap.put("_id", idMap);
        }

        // handle _type
        Map<String, Object> typeMap = Maps.newHashMap();
        if (indexable.typeFieldStore()) {
            typeMap.put("store", "yes");
        }

        if (!indexable.typeFieldIndex().equals(IndexEnum.NA)) {
            typeMap.put("index", indexable.typeFieldIndex().toString().toLowerCase());
        }

        if (!typeMap.isEmpty()) {
            objectMap.put("_type", typeMap);
        }

        // handle _source
        Map<String, Object> sourceMap = Maps.newHashMap();
        if (!indexable.sourceFieldEnabled()) {
            sourceMap.put("enabled", Boolean.FALSE);
        }

        if (indexable.sourceFieldCompress()) {
            sourceMap.put("compress", Boolean.TRUE);
        }

        if (!indexable.sourceFieldCompressThreshold().isEmpty()) {
            sourceMap.put("compress_threshold", indexable.sourceFieldCompressThreshold());
        }

        if (indexable.sourceFieldIncludes().length > 0) {
            sourceMap.put("includes", Lists.newArrayList(indexable.sourceFieldIncludes()));
        }

        if (indexable.sourceFieldExcludes().length > 0) {
            sourceMap.put("excludes", Lists.newArrayList(indexable.sourceFieldExcludes()));
        }

        if (!sourceMap.isEmpty()) {
            objectMap.put("_source", sourceMap);
        }

        // handle _all
        Map<String, Object> allMap = Maps.newHashMap();
        if (!indexable.allFieldEnabled()) {
            allMap.put("enabled", Boolean.FALSE);
        }

        if (indexable.allFieldStore()) {
            allMap.put("store", "yes");
        }

        if (!indexable.allFieldTermVector().equals(TermVectorEnum.NA)) {
            allMap.put("term_vector", indexable.allFieldTermVector().toString().toLowerCase());
        }

        if (!indexable.allFieldAnalyzer().isEmpty()) {
            allMap.put("analyzer", indexable.allFieldAnalyzer());
        }

        if (!indexable.allFieldIndexAnalyzer().isEmpty()) {
            allMap.put("index_analyzer", indexable.allFieldIndexAnalyzer());
        }

        if (!indexable.allFieldSearchAnalyzer().isEmpty()) {
            allMap.put("search_analyzer", indexable.allFieldSearchAnalyzer());
        }

        if (!allMap.isEmpty()) {
            objectMap.put("_all", allMap);
        }

        // handle _analyzer
        Map<String, Object> analyzerMap = Maps.newHashMap();
        if (!indexable.analyzerFieldPath().isEmpty()) {
            analyzerMap.put("path", indexable.analyzerFieldPath());
        }

        if (!analyzerMap.isEmpty()) {
            objectMap.put("_analyzer", analyzerMap);
        }

        // handle _boost
        Map<String, Object> boostMap = Maps.newHashMap();
        if (!indexable.boostFieldName().isEmpty()) {
            boostMap.put("name", indexable.boostFieldName());
        }

        if (indexable.boostFieldNullValue() != Double.MIN_VALUE) {
            boostMap.put("null_value", indexable.boostFieldNullValue());
        }

        if (!boostMap.isEmpty()) {
            objectMap.put("_boost", boostMap);
        }

        // handle _routing
        Map<String, Object> routingMap = Maps.newHashMap();
        if (!indexable.routingFieldStore()) {
            routingMap.put("store", "no");
        }

        if (!indexable.routingFieldIndex().equals(IndexEnum.NA)) {
            routingMap.put("index", indexable.routingFieldIndex().toString().toLowerCase());
        }

        if (indexable.routingFieldRequired()) {
            routingMap.put("required", Boolean.TRUE);
        }

        if (!indexable.routingFieldPath().isEmpty()) {
            routingMap.put("path", indexable.routingFieldPath());
        }

        if (!routingMap.isEmpty()) {
            objectMap.put("_routing", routingMap);
        }

        // handle _index
        Map<String, Object> indexMap = Maps.newHashMap();
        if (indexable.indexFieldEnabled()) {
            indexMap.put("enabled", Boolean.TRUE);
        }

        if (!indexMap.isEmpty()) {
            objectMap.put("_index", indexMap);
        }

        // handle _size
        Map<String, Object> sizeMap = Maps.newHashMap();
        if (indexable.sizeFieldEnabled()) {
            sizeMap.put("enabled", Boolean.TRUE);
        }

        if (indexable.sizeFieldStore()) {
            sizeMap.put("store", "yes");
        }

        if (!sizeMap.isEmpty()) {
            objectMap.put("_size", sizeMap);
        }

        // handle _timestamp
        Map<String, Object> timestampMap = Maps.newHashMap();
        if (indexable.timestampFieldEnabled()) {
            timestampMap.put("enabled", Boolean.TRUE);
        }

        if (indexable.timestampFieldStore()) {
            timestampMap.put("store", "yes");
        }

        if (!indexable.timestampFieldIndex().equals(IndexEnum.NA)) {
            timestampMap.put("index", indexable.timestampFieldIndex().toString().toLowerCase());
        }

        if (!indexable.timestampFieldPath().isEmpty()) {
            timestampMap.put("path", indexable.timestampFieldPath());
        }

        if (!indexable.timestampFieldFormat().isEmpty()) {
            timestampMap.put("format", indexable.timestampFieldFormat());
        }

        if (!timestampMap.isEmpty()) {
            objectMap.put("_timestamp", timestampMap);
        }

        // handle _ttl
        Map<String, Object> ttlMap = Maps.newHashMap();
        if (indexable.ttlFieldEnabled()) {
            ttlMap.put("enabled", Boolean.TRUE);
        }

        if (!indexable.ttlFieldStore()) {
            ttlMap.put("store", "no");
        }

        if (!indexable.ttlFieldIndex().equals(IndexEnum.NA)) {
            ttlMap.put("index", indexable.ttlFieldIndex().toString().toLowerCase());
        }

        if (!indexable.ttlFieldDefault().isEmpty()) {
            ttlMap.put("default", indexable.ttlFieldDefault());
        }

        if (!ttlMap.isEmpty()) {
            objectMap.put("_ttl", ttlMap);
        }

        return objectMap;
    }

    private static Map<String, Object> getIndexableIdMap(Field field) {
        Map<String, Object> idMap = Maps.newHashMap();

        IndexableId indexableId = field.getAnnotation(IndexableId.class);
        if (indexableId.index() != IndexEnum.NA) {
            idMap.put("index", indexableId.index().toString().toLowerCase());
        }

        if (indexableId.store()) {
            idMap.put("store", "yes");
        }

        IndexableProperty indexableProperty = field.getAnnotation(IndexableProperty.class);
        if (indexableProperty != null) {
            String fieldName = field.getName();
            if (indexableProperty.name() != null && !indexableProperty.name().isEmpty()) {
                fieldName = indexableProperty.name();
            }
            idMap.put("path", fieldName);  // only need to put this if the IndexableId field is also IndexableProperty
        }

        return idMap;
    }

    private static void processIndexableProperty(AccessibleObject accessibleObject, Map<String, Object> propertiesMap) {
        IndexableProperty indexableProperty = accessibleObject.getAnnotation(IndexableProperty.class);
        Preconditions.checkNotNull(indexableProperty, "Unable to find annotation IndexableProperty");
        String fieldName = null;
        if (accessibleObject instanceof Field) {
            fieldName = ((Field) accessibleObject).getName();
        }
        if (indexableProperty.name() != null && !indexableProperty.name().isEmpty()) {
            fieldName = indexableProperty.name();
        }

        Preconditions.checkNotNull(fieldName, "Unable to find field name");

        Map<String, Object> fieldMap = getIndexablePropertyMapping(accessibleObject, indexableProperty);
        if (fieldMap != null) {
            propertiesMap.put(fieldName, fieldMap);
        }
    }

    private static Map<String, Object> getIndexablePropertyMapping(AccessibleObject accessibleObject, IndexableProperty indexableProperty) {
        if (!indexableProperty.rawMapping().isEmpty()) {    // has raw mapping, use it directly
            return XContentHelper.convertToMap(indexableProperty.rawMapping().getBytes(), false).v2();
        }

        Map<String, Object> fieldMap = Maps.newHashMap();

        String fieldType = getFieldType(indexableProperty.type(), accessibleObject);

        if (fieldType.equals(TypeEnum.JSON.toString().toLowerCase())) {
            logger.warn("Can't find mapping for json, please specify rawMapping if needed");
            return null;
        }

        fieldMap.put("type", fieldType);

        if (indexableProperty.index() != IndexEnum.NA) {
            fieldMap.put("index", indexableProperty.index().toString().toLowerCase());
        }

        if (indexableProperty.docValues()) {
            fieldMap.put("doc_values", Boolean.TRUE);
        }

        if (indexableProperty.docValuesFormat() != DocValuesFormatEnum.NA) {
            fieldMap.put("doc_values_format", indexableProperty.docValuesFormat().toString().toLowerCase());
        }

        if (!indexableProperty.indexName().isEmpty()) {
            fieldMap.put("index_name", indexableProperty.indexName());
        }

        if (indexableProperty.termVector() != TermVectorEnum.NA) {
            fieldMap.put("term_vector", indexableProperty.termVector().toString().toLowerCase());
        }

        if (indexableProperty.store()) {
            fieldMap.put("store", "yes");
        }

        if (indexableProperty.boost() != Double.MIN_VALUE) {
            fieldMap.put("boost", indexableProperty.boost());
        }

        if (!indexableProperty.nullValue().isEmpty()) {
            fieldMap.put("null_value", indexableProperty.nullValue());
        }

        if (indexableProperty.normsEnabled() != NormsEnabledEnum.NA) {
            fieldMap.put("norms.enabled", indexableProperty.normsEnabled().toString().toLowerCase());
        }

        if (indexableProperty.normsLoading() != NormsLoadingEnum.NA) {
            fieldMap.put("norms.loading", indexableProperty.normsLoading().toString().toLowerCase());
        }

        if (indexableProperty.indexOptions() != IndexOptionsEnum.NA) {
            fieldMap.put("index_options", indexableProperty.indexOptions().toString().toLowerCase());
        }

        if (!indexableProperty.analyzer().isEmpty()) {
            fieldMap.put("analyzer", indexableProperty.analyzer());
        }

        if (!indexableProperty.indexAnalyzer().isEmpty()) {
            fieldMap.put("index_analyzer", indexableProperty.indexAnalyzer());
        }

        if (!indexableProperty.searchAnalyzer().isEmpty()) {
            fieldMap.put("search_analyzer", indexableProperty.searchAnalyzer());
        }

        if (indexableProperty.includeInAll() != IncludeInAllEnum.NA) {
            fieldMap.put("include_in_all", indexableProperty.includeInAll().toString().toLowerCase());
        }

        if (indexableProperty.ignoreAbove() != Integer.MIN_VALUE) {
            fieldMap.put("ignore_above", indexableProperty.ignoreAbove());
        }

        if (indexableProperty.positionOffsetGap() != Integer.MIN_VALUE) {
            fieldMap.put("position_offset_gap", indexableProperty.positionOffsetGap());
        }

        if (indexableProperty.precisionStep() != Integer.MIN_VALUE) {
            fieldMap.put("precision_step", indexableProperty.precisionStep());
        }

        if (indexableProperty.ignoreMalformed()) {
            fieldMap.put("ignore_malformed", Boolean.TRUE);
        }

        if (!indexableProperty.coerce()) {
            fieldMap.put("coerce", Boolean.FALSE);
        }

        if (indexableProperty.postingsFormat() != PostingsFormatEnum.NA) {
            fieldMap.put("postings_format", indexableProperty.postingsFormat().toString().toLowerCase());
        }

        if (indexableProperty.similarity() != SimilarityEnum.NA) {
            switch (indexableProperty.similarity()) {
                case DEFAULT:
                    fieldMap.put("similarity", indexableProperty.postingsFormat().toString().toLowerCase());
                    break;
                case BM25: // BM25 should be uppercase
                    fieldMap.put("similarity", indexableProperty.postingsFormat().toString().toUpperCase());
                    break;
            }
        }


        if (!indexableProperty.format().isEmpty()) {
            fieldMap.put("format", indexableProperty.format());
        }

        if (indexableProperty.copyTo().length > 0) {
            fieldMap.put("copy_to", Lists.newArrayList(indexableProperty.copyTo()));
        }

        if (indexableProperty.geoPointLatLon()) {
            fieldMap.put("lat_lon", Boolean.TRUE);
        }

        if (indexableProperty.geoPointGeohash()) {
            fieldMap.put("geohash", Boolean.TRUE);
        }

        if (indexableProperty.geoPointGeohashPrecision() != Integer.MIN_VALUE) {
            fieldMap.put("geohash_precision", indexableProperty.geoPointGeohashPrecision());
        }

        if (indexableProperty.geoPointGeohashPrefix()) {
            fieldMap.put("geohash_prefix", Boolean.TRUE);
        }

        if (!indexableProperty.geoPointValidate()) {
            fieldMap.put("validate", Boolean.FALSE);
        }

        if (!indexableProperty.geoPointValidateLat()) {
            fieldMap.put("validate_lat", Boolean.FALSE);
        }

        if (!indexableProperty.geoPointValidateLon()) {
            fieldMap.put("validate_lon", Boolean.FALSE);
        }

        if (!indexableProperty.geoPointNormalize()) {
            fieldMap.put("normalize", Boolean.FALSE);
        }

        if (!indexableProperty.geoPointNormalizeLat()) {
            fieldMap.put("normalize_lat", Boolean.FALSE);
        }

        if (!indexableProperty.geoPointNormalizeLon()) {
            fieldMap.put("normalize_lon", Boolean.FALSE);
        }

        if (indexableProperty.geoShapeTree() != GeoShapeTreeEnum.NA) {
            fieldMap.put("tree", indexableProperty.geoShapeTree().toString().toLowerCase());
        }

        if (!indexableProperty.geoShapePrecision().isEmpty()) {
            fieldMap.put("precision", indexableProperty.geoShapePrecision());
        }

        if (indexableProperty.geoShapeTreeLevels() != Integer.MIN_VALUE) {
            fieldMap.put("tree_levels", indexableProperty.geoShapeTreeLevels());
        }

        if (indexableProperty.geoShapeDistanceErrorPct() != Float.MIN_VALUE) {
            fieldMap.put("distance_error_pct", indexableProperty.geoShapeDistanceErrorPct());
        }

        Map<String, Object> fieldDataMap = getFieldDataMap(indexableProperty);
        if (fieldDataMap != null && !fieldDataMap.isEmpty()) {
            fieldMap.put("fielddata", fieldDataMap);
        }

        return fieldMap;
    }

    private static Map<String, Object> getFieldDataMap(IndexableProperty indexableProperty) {
        Map<String, Object> fieldDataMap = Maps.newHashMap();
        if (!indexableProperty.fieldDataFormat().equals(FieldDataFormat.NA)) {
            fieldDataMap.put("format", indexableProperty.fieldDataFormat().toString().toLowerCase());
        }

        if (!indexableProperty.fieldDataLoading().equals(FieldDataLoading.NA)) {
            fieldDataMap.put("loading", indexableProperty.fieldDataLoading().toString().toLowerCase());
        }

        if (!indexableProperty.fieldDataFilterFrequencyMin().isEmpty()) {
            fieldDataMap.put("filter.frequency.min", indexableProperty.fieldDataFilterFrequencyMin());
        }

        if (!indexableProperty.fieldDataFilterFrequencyMax().isEmpty()) {
            fieldDataMap.put("filter.frequency.max", indexableProperty.fieldDataFilterFrequencyMax());
        }

        if (!indexableProperty.fieldDataFilterFrequencyMinSegmentSize().isEmpty()) {
            fieldDataMap.put("filter.frequency.min_segment_size", indexableProperty.fieldDataFilterFrequencyMinSegmentSize());
        }

        if (!indexableProperty.fieldDataFilterRegexPattern().isEmpty()) {
            fieldDataMap.put("filter.regex.pattern", indexableProperty.fieldDataFilterRegexPattern());
        }
        return fieldDataMap;
    }

    private static void processIndexableComponent(AccessibleObject accessibleObject, Map<String, Object> propertiesMap) {
        IndexableComponent indexableComponent = accessibleObject.getAnnotation(IndexableComponent.class);
        Preconditions.checkNotNull(indexableComponent, "Unable to find annotation IndexableComponent");
        String fieldName = null;
        if (accessibleObject instanceof Field) {
            fieldName = ((Field) accessibleObject).getName();
        }
        if (indexableComponent.name() != null && !indexableComponent.name().isEmpty()) {
            fieldName = indexableComponent.name();
        }

        Preconditions.checkNotNull(fieldName, "Unable to find field name for IndexableComponent");

        Map<String, Object> fieldMap = getIndexableComponentMapping(accessibleObject, indexableComponent);
        if (fieldMap != null) {
            propertiesMap.put(fieldName, fieldMap);
        }
    }

    private static Map<String, Object> getIndexableComponentMapping(AccessibleObject accessibleObject, IndexableComponent indexableComponent) {
        Class fieldClazz = null;
        if (accessibleObject instanceof Field) {
            fieldClazz = OsemReflectionUtils.getGenericType((Field) accessibleObject);
        } else if (accessibleObject instanceof Method) {
            fieldClazz = OsemReflectionUtils.getGenericType((Method) accessibleObject);
        }
        Preconditions.checkNotNull(fieldClazz, "Unknown AccessibleObject type");

        Map<String, Object> fieldMap = Maps.newHashMap();
        fieldMap.put("properties", getPropertiesMap(fieldClazz));
        if (indexableComponent.nested()) {
            fieldMap.put("type", "nested");
        } else {
            fieldMap.put("type", "object");
        }

        if (indexableComponent.dynamic() != DynamicEnum.NA) {
            fieldMap.put("dynamic", indexableComponent.dynamic().toString().toLowerCase());
        }

        if (!indexableComponent.enabled()) {
            fieldMap.put("enabled", Boolean.FALSE);
        }

        if (indexableComponent.path() != ObjectFieldPathEnum.NA) {
            fieldMap.put("path", indexableComponent.path().toString().toLowerCase());
        }

        if (indexableComponent.includeInAll() != IncludeInAllEnum.NA) {
            fieldMap.put("include_in_all", indexableComponent.includeInAll().toString().toLowerCase());
        }

        return fieldMap;
    }


    private static void processIndexableProperties(AccessibleObject accessibleObject, Map<String, Object> propertiesMap) {
        IndexableProperties indexableProperties = accessibleObject.getAnnotation(IndexableProperties.class);
        Preconditions.checkNotNull(indexableProperties, "Unable to find annotation IndexableProperties");
        Preconditions.checkArgument(indexableProperties.properties().length > 0, "IndexableProperties must have at lease one IndexableProperty");

        String fieldName = null;
        if (accessibleObject instanceof Field) {
            fieldName = ((Field) accessibleObject).getName();
        }
        if (!indexableProperties.name().isEmpty()) {
            fieldName = indexableProperties.name();
        }

        Preconditions.checkNotNull(fieldName, "Unable to find field name for IndexableProperties");

        Map<String, Object> multiFieldMap = Maps.newHashMap();
        multiFieldMap.put("type", getFieldType(indexableProperties.type(), accessibleObject));

        if (indexableProperties.path() != MultiFieldPathEnum.NA) {
            multiFieldMap.put("path", indexableProperties.path().toString().toLowerCase());
        }

        boolean emptyNameProcessed = false;
        Map<String, Object> fieldsMap = Maps.newHashMap();
        for (IndexableProperty property : indexableProperties.properties()) {
            String propertyName = property.name();
            if (propertyName.isEmpty()) {
                if (!emptyNameProcessed) {
                    emptyNameProcessed = true;
                    propertyName = fieldName;
                } else {
                    throw new ElasticSearchOsemException("Field name cannot be empty in multi-field");
                }
            }
            Map<String, Object> fieldMap = getIndexablePropertyMapping(accessibleObject, property);
            if (propertyName.equals(fieldName)) {
                multiFieldMap.putAll(fieldMap);
            } else {
                fieldsMap.put(propertyName, fieldMap);
            }
        }
        multiFieldMap.put("fields", fieldsMap);
        propertiesMap.put(fieldName, multiFieldMap);
    }

    private static String getFieldType(TypeEnum fieldTypeEnum, AccessibleObject accessibleObject) {
        String fieldType;

        if (fieldTypeEnum.equals(TypeEnum.AUTO)) {
            Class fieldClass = null;
            if (accessibleObject instanceof Field) {
                fieldClass = OsemReflectionUtils.getGenericType((Field) accessibleObject);
            } else if (accessibleObject instanceof Method) {
                fieldClass = OsemReflectionUtils.getGenericType((Method) accessibleObject);
            }
            Preconditions.checkNotNull(fieldClass, "Unknown AccessibleObject type");
            fieldType = fieldClass.getSimpleName().toLowerCase();
        } else {
            fieldType = fieldTypeEnum.toString().toLowerCase();
        }
        return fieldType;
    }


}
