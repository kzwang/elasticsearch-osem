package com.github.kzwang.osem.api;

import com.github.kzwang.osem.impl.ElasticSearchSearcherImpl;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.inject.ImplementedBy;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;


@ImplementedBy(ElasticSearchSearcherImpl.class)
public interface ElasticSearchSearcher {

    /**
     * Get current index name
     *
     * @return indexName current index name
     */
    public String getIndexName();

    /**
     * Set the index to use
     *
     * @param indexName the index name to use
     */
    public void setIndexName(String indexName);

    /**
     * Count the number of objects in ElasticSearch for given class
     *
     * @param clazz        class to count
     * @param queryBuilder optional count query
     * @return number of objects
     */
    public Long count(Class clazz, @Nullable QueryBuilder queryBuilder);

    /**
     * Perform search in ElasticSearch
     *
     * @param requestBuilder SearchRequestBuilder, must get from {@link #getSearchRequestBuilder(Class[])}
     * @return SearchResponse from ElasticSearch
     */
    public SearchResponse search(SearchRequestBuilder requestBuilder);

    /**
     * Perform search in ElasticSearch and convert search result to original object
     *
     * @param clazz         class to search
     * @param queryBuilder  optional query builder
     * @param filterBuilder optional filter builder
     * @return list of object from search result
     */
    public <T> List<T> search(Class<T> clazz, @Nullable QueryBuilder queryBuilder, @Nullable FilterBuilder filterBuilder);

    /**
     * Perform search in ElasticSearch and convert search result to original object
     *
     * @param clazz          class to search
     * @param requestBuilder SearchRequestBuilder, must get from {@link #getSearchRequestBuilder(Class[])}
     * @return list of object from search result
     */
    public <T> List<T> search(Class<T> clazz, SearchRequestBuilder requestBuilder);

    /**
     * Get search request build and set the search types
     *
     * @param clazz array of classes to search
     * @return SearchRequestBuilder
     */
    public SearchRequestBuilder getSearchRequestBuilder(Class... clazz);

    /**
     * Get an object by id
     *
     * @param clazz class of the object
     * @param id    id of the object
     * @return object from ElasticSearch
     */
    public <T> T getById(Class<T> clazz, String id);

    /**
     * Get an object by id
     *
     * @param clazz class of the object
     * @param id    id of the object
     * @param routing    routing of the object
     * @return object from ElasticSearch
     */
    public <T> T getById(Class<T> clazz, String id, @Nullable String routing);

    /**
     * Get objects by id list
     *
     * @param clazz class of the objects
     * @param ids   id list of the ojects
     * @return list of objects from ElasticSearch
     */
    public <T> List<T> getByIds(Class<T> clazz, List<String> ids);

}
