package com.github.kzwang.osem.api;

import com.github.kzwang.osem.impl.ElasticSearchIndexerImpl;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.inject.ImplementedBy;
import org.elasticsearch.index.query.QueryBuilder;


@ImplementedBy(ElasticSearchIndexerImpl.class)
public interface ElasticSearchIndexer {

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
     * Generate mapping for class and create mapping in ElasticSearch
     *
     * @param clazz class for create mapping
     * @return response from ElasticSearch
     */
    public PutMappingResponse createMapping(Class clazz);

    /**
     * Put the mapping for class
     *
     * @param clazz   class for create mapping
     * @param mapping mapping string
     * @return response from ElasticSearch
     */
    public PutMappingResponse putMapping(Class clazz, String mapping);

    /**
     * Delete mapping for class
     *
     * @param clazz class for delete mapping
     * @return response from ElasticSearch
     */
    public DeleteMappingResponse deleteMapping(Class clazz);

    /**
     * Get the mapping string from ElasticSearch
     *
     * @param clazz class for get mapping
     * @return mapping string
     */
    public String getMapping(Class clazz);

    /**
     * Index an object
     *
     * @param object object to index
     * @return response from ElasticSearch
     */
    public IndexResponse index(Object object);

    /**
     * Index an array of objects
     *
     * @param objects objects to index
     * @return response from ElasticSearch
     */
    public BulkResponse bulkIndex(Object... objects);

    /**
     * Delete an object
     *
     * @param object object to delete
     * @return response from ElasticSearch
     */
    public DeleteResponse delete(Object object);

    /**
     * Delete an array of objects
     *
     * @param objects objects to delete
     * @return response from ElasticSearch
     */
    public BulkResponse bulkDelete(Object... objects);

    /**
     * Delete objects by query
     *
     * @param clazz        class of objects need to delete
     * @param queryBuilder delete query
     * @return response from ElasticSearch
     */
    public DeleteByQueryResponse deleteByQuery(Class clazz, QueryBuilder queryBuilder);

    /**
     * Check index exist or not
     *
     * @return index exist or not
     */
    public boolean indexExist();

    /**
     * Create the index if not exist
     *
     * @return response from ElasticSearch
     */
    public CreateIndexResponse createIndex();

    /**
     * Delete the index if exist
     *
     * @return response from ElasticSearch
     */
    public DeleteIndexResponse deleteIndex();

    /**
     * Refresh the index so it can be searched
     *
     * @return response from ElasticSearch
     */
    public RefreshResponse refreshIndex();

    /**
     * Add an alias to the index
     *
     * @param aliasName name of the alias to add
     * @return response from ElasticSearch
     */
    public IndicesAliasesResponse addAlias(String aliasName);

    /**
     * Remove an alias from the index
     *
     * @param aliasName name of the alias to remove
     * @return response from ElasticSearch
     */
    public IndicesAliasesResponse removeAlias(String aliasName);

    /**
     * Check alias exist or not
     *
     * @param aliasName name of the alias to check
     * @return alias exist or not
     */
    public boolean aliasExist(String aliasName);


}
