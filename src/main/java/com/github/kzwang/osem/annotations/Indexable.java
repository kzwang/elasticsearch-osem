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
 * @See <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-root-object-type.html#mapping-root-object-type">Multi Field Type</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Indexable {

    String name() default "";

    /************** For object mapping   ****************/

    String indexAnalyzer() default "";

    String searchAnalyzer() default "";

    String[] dynamicDateFormats() default {};

    DateDetectionEnum dateDetection() default DateDetectionEnum.NA;

    NumericDetectionEnum numericDetection() default NumericDetectionEnum.NA;

    /**
     * name of parent id field
     */
    String parentIdField() default "";

    Class parentClass() default void.class;

    /************** For Serialize / DeSerialize object   ****************/

    /**
     * Json DeSerializer class
     */
    Class<? extends JsonDeserializer> deserializer() default JsonDeserializer.class;

    /**
     * Json Serializer class
     */
    Class<? extends JsonSerializer> serializer() default JsonSerializer.class;


}
