package com.github.kzwang.osem.annotations;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Used for core objects
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-core-types.html">Core Type</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.METHOD})
public @interface IndexableProperty {

    /**
     * name of the field, default to field name
     */
    String name() default "";

    /**
     * field type, "type" field in mapping, default to auto detect
     */
    TypeEnum type() default TypeEnum.AUTO;

    /**
     * "index_name" field in mapping
     */
    String indexName() default "";

    /**
     * "store" field in mapping
     */
    boolean store() default false;

    /**
     * "index" field in mapping
     */
    IndexEnum index() default IndexEnum.NA;

    /**
     * "doc_values" field in mapping
     */
    boolean docValues() default false;

    /**
     * "doc_values_format" field in mapping
     */
    DocValuesFormatEnum docValuesFormat() default DocValuesFormatEnum.NA;

    /**
     * "term_vector" field in mapping
     */
    TermVectorEnum termVector() default TermVectorEnum.NA;

    /**
     * "boost" field in mapping
     */
    double boost() default Double.MIN_VALUE;

    /**
     * "null_value" field in mapping
     */
    String nullValue() default "";

    /**
     * "norms.enabled" field in mapping
     */
    NormsEnabledEnum normsEnabled() default NormsEnabledEnum.NA;

    /**
     * "norms.loading" field in mapping
     */
    NormsLoadingEnum normsLoading() default NormsLoadingEnum.NA;

    /**
     * "index_options" field in mapping
     */
    IndexOptionsEnum indexOptions() default IndexOptionsEnum.NA;

    /**
     * "analyzer" field in mapping
     */
    String analyzer() default "";

    /**
     * "index_analyzer" field in mapping
     */
    String indexAnalyzer() default "";

    /**
     * "search_analyzer" field in mapping
     */
    String searchAnalyzer() default "";

    /**
     * "include_in_all" field in mapping
     */
    IncludeInAllEnum includeInAll() default IncludeInAllEnum.NA;

    /**
     * "ignore_above" field in mapping
     */
    int ignoreAbove() default Integer.MIN_VALUE;

    /**
     * "position_offset_gap" field in mapping
     */
    int positionOffsetGap() default Integer.MIN_VALUE;

    /**
     * "precision_step" field in mapping
     */
    int precisionStep() default Integer.MIN_VALUE;

    /**
     * "ignore_malformed" field in mapping
     */
    boolean ignoreMalformed() default false;

    /**
     * "coerce" field in mapping
     */
    boolean coerce() default true;

    /**
     * format of Date
     * "format"  field in mapping
     * if multiple format specified, will use the first
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-date-format.html">Date Format</a>
     */
    String format() default "";

    /**
     * "copy_to" field in mapping
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/1.x/mapping-core-types.html#_copy_to_field">Copy to field</a>
     */
    String[] copyTo() default {};

    /**
     * "postings_format" field in mapping
     */
    PostingsFormatEnum postingsFormat() default PostingsFormatEnum.NA;

    /**
     * "similarity" field in mapping
     */
    SimilarityEnum similarity() default SimilarityEnum.NA;

    /**
     * "lat_lon" field in mapping for Geo Point type
     */
    boolean geoPointLatLon() default false;

    /**
     * "geohash" field in mapping for Geo Point type
     */
    boolean geoPointGeohash() default false;

    /**
     * "geohash_precision" field in mapping for Geo Point type
     */
    int geoPointGeohashPrecision() default Integer.MIN_VALUE;

    /**
     * "geohash_prefix" field in mapping for Geo Point type
     */
    boolean geoPointGeohashPrefix() default false;

    /**
     * "validate" field in mapping for Geo Point type
     */
    boolean geoPointValidate() default true;

    /**
     * "validate_lat" field in mapping for Geo Point type
     */
    boolean geoPointValidateLat() default true;

    /**
     * "validate_lon" field in mapping for Geo Point type
     */
    boolean geoPointValidateLon() default true;

    /**
     * "normalize" field in mapping for Geo Point type
     */
    boolean geoPointNormalize() default true;

    /**
     * "normalize_lat" field in mapping for Geo Point type
     */
    boolean geoPointNormalizeLat() default true;

    /**
     * "normalize_lon" field in mapping for Geo Point type
     */
    boolean geoPointNormalizeLon() default true;

    /**
     * "tree" field in mapping for Geo Shape type
     */
    GeoShapeTreeEnum geoShapeTree() default GeoShapeTreeEnum.NA;

    /**
     * "precision" field in mapping for Geo Shape type
     */
    String geoShapePrecision() default "";

    /**
     * "tree_levels" field in mapping for Geo Shape type
     */
    int geoShapeTreeLevels() default Integer.MIN_VALUE;

    /**
     * "distance_error_pct" field in mapping for Geo Shape type
     */
    float geoShapeDistanceErrorPct() default Float.MIN_VALUE;


    // for fielddata
    /**
     * set "format" in fielddata
     */
    FieldDataFormat fieldDataFormat() default FieldDataFormat.NA;

    /**
     * set "loading" in fielddata
     */
    FieldDataLoading fieldDataLoading() default FieldDataLoading.NA;

    /**
     * "min" in fielddata.filter.frequency
     */
    String fieldDataFilterFrequencyMin() default "";

    /**
     * "max" in fielddata.filter.frequency
     */
    String fieldDataFilterFrequencyMax() default "";

    /**
     * "min_segment_size" in fielddata.filter.frequency
     */
    String fieldDataFilterFrequencyMinSegmentSize() default "";

    /**
     * "pattern" in fielddata.filter.regex
     */
    String fieldDataFilterRegexPattern() default "";


    /**
     * Mapping json string, will convert to JSON object and send to ElasticSearch directly
     */
    String rawMapping() default "";


    /**
     * Indicate when value should be include in JSON object
     */
    JsonInclude jsonInclude() default JsonInclude.DEFAULT;

    /**
     * Json DeSerializer class, must extend {@link JsonDeserializer}
     */
    Class<? extends JsonDeserializer> deserializer() default JsonDeserializer.class;

    /**
     * Json Serializer class, must extend {@link JsonSerializer}
     */
    Class<? extends JsonSerializer> serializer() default JsonSerializer.class;


}
