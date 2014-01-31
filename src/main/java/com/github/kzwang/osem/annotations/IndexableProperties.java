package com.github.kzwang.osem.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Used for multi-field type
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-multi-field-type.html">Multi Field Type</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.METHOD})
public @interface IndexableProperties {
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
}
