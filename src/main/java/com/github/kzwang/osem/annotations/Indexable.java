package com.github.kzwang.osem.annotations;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Root object type
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-root-object-type.html#mapping-root-object-type">Root Object Type</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Indexable {
    /**
     * Index type name, default to class name in lower_underscore format
     */
    String name() default "";

    /**
     * "index_analyzer" field in mapping
     */
    String indexAnalyzer() default "";

    /**
     * "search_analyzer" field in mapping
     */
    String searchAnalyzer() default "";

    /**
     * "dynamic_date_formats" field in mapping
     */
    String[] dynamicDateFormats() default {};

    /**
     * "date_detection" field in mapping
     */
    DateDetectionEnum dateDetection() default DateDetectionEnum.NA;

    /**
     * "numeric_detection" field in mapping
     */
    NumericDetectionEnum numericDetection() default NumericDetectionEnum.NA;

    /**
     * path of parent id field
     */
    String parentPath() default "";

    /**
     * Class of the parent type
     */
    Class parentClass() default void.class;

    /**
     * "store" in "_type"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-type-field.html">Mapping type field</a>
     */
    boolean typeFieldStore() default false;

    /**
     * "index" in "_type"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-type-field.html">Mapping type field</a>
     */
    IndexEnum typeFieldIndex() default IndexEnum.NA;

    /**
     * "enabled" in "_source"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-source-field.html">Mapping source field</a>
     */
    boolean sourceFieldEnabled() default true;

    /**
     * "compress" in "_source"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-source-field.html#compression">Mapping source field compress</a>
     */
    boolean sourceFieldCompress() default false;

    /**
     * "compress_threshold" in "_source"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-source-field.html#compression">Mapping source field compress</a>
     */
    String sourceFieldCompressThreshold() default "";

    /**
     * "includes" in "_source"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-source-field.html#include-exclude">Mapping source field include</a>
     */
    String[] sourceFieldIncludes() default {};

    /**
     * "excludes" in "_source"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-source-field.html#include-exclude">Mapping source field exclude</a>
     */
    String[] sourceFieldExcludes() default {};

    /**
     * "enabled" in "_all"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-all-field.html">Mapping all field</a>
     */
    boolean allFieldEnabled() default true;

    /**
     * "store" in "_all"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-all-field.html">Mapping all field</a>
     */
    boolean allFieldStore() default false;

    /**
     * "term_vector" in "_all"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-all-field.html">Mapping all field</a>
     */
    TermVectorEnum allFieldTermVector() default TermVectorEnum.NA;

    /**
     * "analyzer" in "_all"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-all-field.html">Mapping all field</a>
     */
    String allFieldAnalyzer() default "";

    /**
     * "index_analyzer" in "_all"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-all-field.html">Mapping all field</a>
     */
    String allFieldIndexAnalyzer() default "";

    /**
     * "search_analyzer" in "_all"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-all-field.html">Mapping all field</a>
     */
    String allFieldSearchAnalyzer() default "";

    /**
     * "path" in "_analyzer"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-analyzer-field.html">Mapping analyzer field</a>
     */
    String analyzerFieldPath() default "";

    /**
     * "name" in "_boost"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/1.x/mapping-boost-field.html">Mapping boost field</a>
     * @deprecated deprecated in ElasticSearch 1.0.0.rc1
     */
    @Deprecated
    String boostFieldName() default "";

    /**
     * "name" in "_boost"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/1.x/mapping-boost-field.html">Mapping boost field</a>
     * @deprecated deprecated in ElasticSearch 1.0.0.rc1
     */
    @Deprecated
    double boostFieldNullValue() default Double.MIN_VALUE;

    /**
     * "store" in "_routing"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-routing-field.html#_store_index">Mapping routing field</a>
     */
    boolean routingFieldStore() default true;

    /**
     * "index" in "_routing"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-routing-field.html#_store_index">Mapping routing field</a>
     */
    IndexEnum routingFieldIndex() default IndexEnum.NA;

    /**
     * "required" in "_routing"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-routing-field.html#_required">Mapping routing field</a>
     */
    boolean routingFieldRequired() default false;

    /**
     * "path" in "_routing"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-routing-field.html#_path">Mapping routing field</a>
     */
    String routingFieldPath() default "";

    /**
     * "enabled" in "_index"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-index-field.html">Mapping index field</a>
     */
    boolean indexFieldEnabled() default false;

    /**
     * "enabled" in "_size"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-size-field.html">Mapping size field</a>
     */
    boolean sizeFieldEnabled() default false;

    /**
     * "store" in "_size"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-size-field.html">Mapping size field</a>
     */
    boolean sizeFieldStore() default false;

    /**
     * "enabled" in "_timestamp"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-timestamp-field.html#_enabled">Mapping timestamp field</a>
     */
    boolean timestampFieldEnabled() default false;

    /**
     * "store" in "_timestamp"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-timestamp-field.html#_store_index_2">Mapping timestamp field</a>
     */
    boolean timestampFieldStore() default false;

    /**
     * "index" in "_timestamp"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-timestamp-field.html#_store_index_2">Mapping timestamp field</a>
     */
    IndexEnum timestampFieldIndex() default IndexEnum.NA;

    /**
     * "path" in "_timestamp"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-timestamp-field.html#_path_2">Mapping timestamp field</a>
     */
    String timestampFieldPath() default "";

    /**
     * "format" in "_timestamp"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-timestamp-field.html#_format">Mapping timestamp field</a>
     */
    String timestampFieldFormat() default "";

    /**
     * "enabled" in "_ttl"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-ttl-field.html#_enabled_2">Mapping ttl field</a>
     */
    boolean ttlFieldEnabled() default false;

    /**
     * "store" in "_ttl"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-ttl-field.html#_store_index_3">Mapping ttl field</a>
     */
    boolean ttlFieldStore() default true;

    /**
     * "index" in "_ttl"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-ttl-field.html#_store_index_3">Mapping ttl field</a>
     */
    IndexEnum ttlFieldIndex() default IndexEnum.NA;

    /**
     * "default" in "_timestamp"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-ttl-field.html#_default">Mapping ttl field</a>
     */
    String ttlFieldDefault() default "";


    /**
     * Json DeSerializer class, must extend {@link JsonDeserializer}
     */
    Class<? extends JsonDeserializer> deserializer() default JsonDeserializer.class;

    /**
     * Json Serializer class, must extend {@link JsonSerializer}
     */
    Class<? extends JsonSerializer> serializer() default JsonSerializer.class;


}
