package com.github.kzwang.osem.annotations;


/**
 * "index_options" field in mapping
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-core-types.html">Mapping Core Types</a>
 */
public enum IndexOptionsEnum {
    /**
     * Use default value in ElasticSearch
     */
    NA,

    /**
     * Set "index_options" to "docs"
     */
    DOCS,

    /**
     * Set "index_options" to "freqs"
     */
    FREQS,

    /**
     * Set "index_options" to "positions"
     */
    POSITIONS,

    /**
     * Set "index_options" to "offsets"
     */
    OFFSETS
}
