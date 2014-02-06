package com.github.kzwang.osem.annotations;

/**
 * Field Data Loading
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/1.x/fielddata-formats.html#_fielddata_loading">Field Data Loading</a>
 */
public enum FieldDataLoading {
    /**
     * Use default value in ElasticSearch
     */
    NA,

    /**
     * set loading to "lazy"
     */
    LAZY,

    /**
     * set loading to "eager"
     */
    EAGER,
}
