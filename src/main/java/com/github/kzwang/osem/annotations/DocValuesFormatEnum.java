package com.github.kzwang.osem.annotations;

/**
 * Format for doc values
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/1.x/mapping-core-types.html#_doc_values_format">Doc Values Format</a>
 */
public enum DocValuesFormatEnum {
    /**
     * Use default value in ElasticSearch
     */
    NA,

    /**
     * Set "doc_values_format" to "memory"
     */
    MEMORY,

    /**
     * Set "doc_values_format" to "disk"
     */
    DISK,

    /**
     * Set "doc_values_format" to "default"
     */
    DEFAULT
}
