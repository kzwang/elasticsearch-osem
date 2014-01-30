package com.github.kzwang.osem.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Used for multi-field type
 *
 * @See <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-multi-field-type.html">Multi Field Type</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.METHOD})
public @interface IndexableProperties {
    IndexableProperty[] properties() default {};

    String name() default "";

    MultiFieldPathEnum path() default MultiFieldPathEnum.NA;
}
