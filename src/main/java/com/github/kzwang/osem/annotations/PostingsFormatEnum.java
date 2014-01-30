package com.github.kzwang.osem.annotations;

/**
 * @See <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-core-types.html#postings">
 * http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-core-types.html#postings</a>
 */
public enum PostingsFormatEnum {
    NA, DIRECT, MEMORY, PULSING, BLOOM_DEFAULT, BLOOM_PULSING, DEFAULT
}
