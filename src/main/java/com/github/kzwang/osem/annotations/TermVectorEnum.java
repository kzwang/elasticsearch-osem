package com.github.kzwang.osem.annotations;


/**
 * "term_vector" field in mapping
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-core-types.html">Mapping Core Types</a>
 */
public enum TermVectorEnum {
    /**
     * Use default value in ElasticSearch
     */
    NA,

    /**
     * Set "term_vector" to "no"
     */
    NO,

    /**
     * Set "term_vector" to "yes"
     */
    YES,

    /**
     * Set "term_vector" to "with_offsets"
     */
    WITH_OFFSETS,

    /**
     * Set "term_vector" to "with_positions"
     */
    WITH_POSITIONS,

    /**
     * Set "term_vector" to "with_positions_offsets"
     */
    WITH_POSITIONS_OFFSETS
}
