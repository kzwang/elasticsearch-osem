package com.github.kzwang.osem.annotations;


/**
 * "index" field in mapping
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-core-types.html">Mapping Core Types</a>
 */
public enum IndexEnum {
    /**
     * Use default value in ElasticSearch
     */
    NA,

    /**
     * Set "index" to "analyzed"
     */
    ANALYZED,

    /**
     * Set "index" to "not_analyzed"
     */
    NOT_ANALYZED,

    /**
     * Set "index" to "no"
     */
    NO
}
