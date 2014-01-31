package com.github.kzwang.osem.annotations;

/**
 * type of the field
 */
public enum TypeEnum {
    /**
     * Auto detect field type
     */
    AUTO,

    /**
     * String type
     */
    STRING,

    /**
     * Integer type
     */
    INTEGER,

    /**
     * Long type
     */
    LONG,

    /**
     * Float type
     */
    FLOAT,

    /**
     * Double type
     */
    DOUBLE,

    /**
     * Boolean type
     */
    BOOLEAN,

    /**
     * Short type
     */
    SHORT,

    /**
     * Byte type
     */
    BYTE,

    /**
     * Date type
     */
    DATE,

    /**
     * Attachment type
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-attachment-type.html">Attachment Type</a>
     */
    ATTACHMENT,

    /**
     * IP type
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-ip-type.html">IP Type</a>
     */
    IP,

    /**
     * Geo Point type
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-geo-point-type.html">Geo Point type</a>
     */
    GEO_POINT,

    /**
     * Geo Shape type
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-geo-shape-type.html">Geo Shape Type</a>
     */
    GEO_SHAPE,

    /**
     * Token Count type
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-core-types.html#token_count">Token Count</a>
     */
    TOKEN_COUNT,

    /**
     * Binary type
     *
     * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-core-types.html#binary">Binary</a>
     */
    BINARY,

    /**
     * Raw JSON object, will convert field value into json object and send to ElasticSearch
     */
    JSON
}
