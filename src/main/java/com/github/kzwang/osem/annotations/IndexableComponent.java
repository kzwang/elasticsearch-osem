package com.github.kzwang.osem.annotations;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for object type
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-object-type.html">Object Type</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.METHOD})
public @interface IndexableComponent {
    /**
     * name of the field, default to field name, mandatory if this annotation is used on method
     */
    String name() default "";

    /**
     * set object type to "nested"
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-nested-type.html">Nested Type</a>
     */
    boolean nested() default false;

    /**
     * "dynamic" field in mapping
     */
    DynamicEnum dynamic() default DynamicEnum.NA;

    /**
     * "enabled" field in mapping
     */
    boolean enabled() default true;

    /**
     * "path" field in mapping
     */
    ObjectFieldPathEnum path() default ObjectFieldPathEnum.NA;

    /**
     * "include_in_all" field in mapping
     */
    IncludeInAllEnum includeInAll() default IncludeInAllEnum.NA;


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
