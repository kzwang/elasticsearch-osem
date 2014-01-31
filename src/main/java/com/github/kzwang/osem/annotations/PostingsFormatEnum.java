package com.github.kzwang.osem.annotations;

/**
 * "postings_format" field in mapping
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-core-types.html#postings">Postings format</a>
 */
public enum PostingsFormatEnum {
    /**
     * Use default value in ElasticSearch
     */
    NA,

    /**
     * Set "postings_format" to "direct"
     */
    DIRECT,

    /**
     * Set "postings_format" to "memory"
     */
    MEMORY,

    /**
     * Set "postings_format" to "pulsing"
     */
    PULSING,

    /**
     * Set "postings_format" to "bloom_default"
     */
    BLOOM_DEFAULT,

    /**
     * Set "postings_format" to "bloom_pulsing"
     */
    BLOOM_PULSING,

    /**
     * Set "postings_format" to "default"
     */
    DEFAULT
}
