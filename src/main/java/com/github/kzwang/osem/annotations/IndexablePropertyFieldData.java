package com.github.kzwang.osem.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for field data formats
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/1.x/fielddata-formats.html">Field data formats</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.METHOD})
public @interface IndexablePropertyFieldData {

    /**
     * set "format" in fielddata
     */
    FieldDataFormat format() default FieldDataFormat.NA;

    /**
     * set "loading" in fielddata
     */
    FieldDataLoading loading() default FieldDataLoading.NA;

    /**
     * "min" in fielddata.filter.frequency
     */
    String filterFrequencyMin() default "";

    /**
     * "max" in fielddata.filter.frequency
     */
    String filterFrequencyMax() default "";

    /**
     * "min_segment_size" in fielddata.filter.frequency
     */
    String filterFrequencyMinSegmentSize() default "";

    /**
     * "pattern" in fielddata.filter.regex
     */
    String filterRegexPattern() default "";

}
