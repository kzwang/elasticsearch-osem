package com.github.kzwang.osem.annotations;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Used for multi-field type
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/1.x/mapping-core-types.html#_multi_fields_3">Multi Field Type</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.METHOD})
public @interface IndexableProperties {
    /**
     * field type, "type" field in mapping, default to auto detect
     */
    TypeEnum type() default TypeEnum.AUTO;

    /**
     * Array of {@link IndexableProperty}, must have at least one
     */
    IndexableProperty[] properties() default {};

    /**
     * Name of the field, default to field name
     */
    String name() default "";

    /**
     * "path" field in mapping
     */
    MultiFieldPathEnum path() default MultiFieldPathEnum.NA;


    /**
     * Indicate when value should be include in JSON object
     */
    JsonInclude jsonInclude() default JsonInclude.DEFAULT;

    /**
     * Json DeSerializer class, must extend {@link com.fasterxml.jackson.databind.JsonDeserializer}
     */
    Class<? extends JsonDeserializer> deserializer() default JsonDeserializer.class;

    /**
     * Json Serializer class, must extend {@link com.fasterxml.jackson.databind.JsonSerializer}
     */
    Class<? extends JsonSerializer> serializer() default JsonSerializer.class;
}
