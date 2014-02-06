package com.github.kzwang.osem.annotations;

/**
 * Field Data Format
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/1.x/fielddata-formats.html#fielddata-formats">Field Data Format</a>
 */
public enum FieldDataFormat {
    /**
     * Use default value in ElasticSearch
     */
    NA,

    /**
     * set format to "paged_bytes", only available for string
     */
    PAGED_BYTES,

    /**
     * set format to "fst", only available for string
     */
    FST,

    /**
     * set format to "doc_values"
     */
    DOC_VALUES,

    /**
     * set format to "array"
     */
    ARRAY,

    /**
     * set format to "disabled"
     */
    DISABLED,

    /**
     * set format to "compressed", only available for geo_point
     */
    COMPRESSED,
}
