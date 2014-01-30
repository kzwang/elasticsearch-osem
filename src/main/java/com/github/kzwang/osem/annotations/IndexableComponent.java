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
 * @See <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-object-type.html">Object Type</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.METHOD})
public @interface IndexableComponent {

    /**
     * name of the field
     */
    String name() default "";

    /**
     * *********** For object mapping   ***************
     */


    boolean nested() default false;

    DynamicEnum dynamic() default DynamicEnum.NA;

    boolean enabled() default true;

    String path() default "";

    IncludeInAllEnum includeInAll() default IncludeInAllEnum.NA;


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
