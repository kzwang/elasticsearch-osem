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
     * field name of parent id field
     */
    String parentIdField() default "";

    /**
     * Class of the parent type
     */
    Class parentClass() default void.class;


    /**
     * Json DeSerializer class, must extend {@link JsonDeserializer}
     */
    Class<? extends JsonDeserializer> deserializer() default JsonDeserializer.class;

    /**
     * Json Serializer class, must extend {@link JsonSerializer}
     */
    Class<? extends JsonSerializer> serializer() default JsonSerializer.class;


}
