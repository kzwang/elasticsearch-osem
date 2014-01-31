package com.github.kzwang.osem.annotations;


/**
 * "similarity" field in mapping
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-core-types.html#similarity">Similarity</a>
 */
public enum SimilarityEnum {
    /**
     * Use default value in ElasticSearch
     */
    NA,

    /**
     * Set "similarity" to "default"
     */
    DEFAULT,

    /**
     * Set "similarity" to "BM25"
     */
    BM25
}
