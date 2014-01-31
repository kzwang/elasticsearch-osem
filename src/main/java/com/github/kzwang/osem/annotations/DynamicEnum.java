package com.github.kzwang.osem.annotations;

/**
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-object-type.html#_dynamic">Dynamic</a>
 */
public enum DynamicEnum {
    /**
     * Use default value in ElasticSearch
     */
    NA,

    /**
     * Enable dynamic feature
     */
    TRUE,

    /**
     * Disable dynamic feature
     */
    FALSE,

    /**
     * Not only new fields will not be introduced into the mapping, parsing (indexing) docs with such new fields will fail
     */
    STRICT
}
