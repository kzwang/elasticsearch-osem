package com.github.kzwang.osem.annotations;


/**
 * "norms.loading" field in mapping
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-core-types.html">Mapping Core Types</a>
 */
public enum NormsLoadingEnum {
    /**
     * Use default value in ElasticSearch
     */
    NA,

    /**
     * Set "norms.loading" to "eager"
     */
    EAGER,

    /**
     * Set "norms.loading" to "lazy"
     */
    LAZY
}
