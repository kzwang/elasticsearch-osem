package com.github.kzwang.osem.annotations;

/**
 * Allows to disable automatic date type detection
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-root-object-type.html#_date_detection">Date Detection</a>
 */
public enum DateDetectionEnum {
    /**
     * Use default value in ElasticSearch
     */
    NA,

    /**
     * Enable Date Detection
     */
    TRUE,

    /**
     * Disable Date Detection
     */
    FALSE
}
