package com.github.kzwang.osem.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Id field of the object, it will not be include in the json unless has annotation {@link com.github.kzwang.osem.annotations.IndexableProperty}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.METHOD})
public @interface IndexableId {

    boolean store() default false;

    IndexEnum index() default IndexEnum.NA;


}
