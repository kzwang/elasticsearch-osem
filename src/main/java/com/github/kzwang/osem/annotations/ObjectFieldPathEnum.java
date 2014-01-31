package com.github.kzwang.osem.annotations;

/**
 * "path" in object type
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-object-type.html#_path_3">Object Type path</a>
 */
public enum ObjectFieldPathEnum {
    /**
     * Use default value in ElasticSearch
     */
    NA,

    /**
     * Set "path" to "just_name"
     */
    JUST_NAME,

    /**
     * Set "path" to "full"
     */
    FULL
}
