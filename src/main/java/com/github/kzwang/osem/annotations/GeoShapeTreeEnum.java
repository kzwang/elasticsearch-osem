package com.github.kzwang.osem.annotations;

/**
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-geo-shape-type.html#_prefix_trees">Prefix Tree</a>
 */
public enum GeoShapeTreeEnum {
    /**
     * Use default value in ElasticSearch
     */
    NA,

    /**
     * Use GeohashPrefixTree (geohash)
     */
    GEOHASH,

    /**
     * Use QuadPrefixTree (quadtree)
     */
    QUADTREE
}
