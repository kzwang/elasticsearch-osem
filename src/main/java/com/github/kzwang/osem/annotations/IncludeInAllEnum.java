package com.github.kzwang.osem.annotations;


/**
 * "include_in_all" field in mapping
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-core-types.html">Mapping Core Types</a>
 */
public enum IncludeInAllEnum {
    /**
     * Use default value in ElasticSearch
     */
    NA,

    /**
     * Set "include_in_all" to "true"
     */
    TRUE,

    /**
     * Set "include_in_all" to "false"
     */
    FALSE
}
