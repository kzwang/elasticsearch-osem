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
 * @See <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-core-types.html">Core Type</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.METHOD})
public @interface IndexableProperty {

    /**
     * name of the field
     */
    String name() default "";


    /**
     * *********** For object mapping   ***************
     */

    TypeEnum type() default TypeEnum.AUTO;

    String indexName() default "";

    boolean store() default false;

    IndexEnum index() default IndexEnum.NA;

    TermVectorEnum termVector() default TermVectorEnum.NA;

    double boost() default 1.0;

    String nullValue() default "";

    NormsEnabledEnum normsEnabled() default NormsEnabledEnum.NA;

    NormsLoadingEnum normsLoading() default NormsLoadingEnum.NA;

    IndexOptionsEnum indexOptions() default IndexOptionsEnum.NA;

    String analyzer() default "";

    String indexAnalyzer() default "";

    String searchAnalyzer() default "";

    IncludeInAllEnum includeInAll() default IncludeInAllEnum.NA;

    int ignoreAbove() default Integer.MIN_VALUE;

    int positionOffsetGap() default Integer.MIN_VALUE;

    int precisionStep() default Integer.MIN_VALUE;

    boolean ignoreMalformed() default false;

    PostingsFormatEnum postingsFormat() default PostingsFormatEnum.NA;

    SimilarityEnum similarity() default SimilarityEnum.NA;

    /**
     * For Geo Point
     */

    boolean geoPointLatLon() default false;

    boolean geoPointGeoHash() default false;

    int geoPointGeoHashPrecision() default Integer.MIN_VALUE;

    boolean geoPointValidate() default false;

    boolean geoPointValidateLat() default false;

    boolean geoPointValidateLon() default false;

    boolean geoPointNormalize() default true;

    boolean geoPointNormalizeLat() default true;

    boolean geoPointNormalizeLon() default true;

    /**
     * For Geo Shape
     */

    GeoShapeTreeEnum geoShapeTree() default GeoShapeTreeEnum.NA;

    String geoShapePrecision() default "";

    float geoShapeDistanceErrorPct() default Float.MIN_VALUE;


    /**
     * date format string
     */
    String format() default "";

    /**
     * Mapping json string, will added to mapping directly
     */
    String rawMapping() default "";


    /**
     * *********** For Serialize / DeSerialize object   ***************
     */

    JsonInclude jsonInclude() default JsonInclude.DEFAULT;

    /**
     * Json DeSerializer class
     */
    Class<? extends JsonDeserializer> deserializer() default JsonDeserializer.class;

    /**
     * Json Serializer class
     */
    Class<? extends JsonSerializer> serializer() default JsonSerializer.class;


}
