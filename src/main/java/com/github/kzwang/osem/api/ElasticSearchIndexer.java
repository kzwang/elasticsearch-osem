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

    public String getIndexName();

    public void setIndexName(String indexName);

    public PutMappingResponse createMapping(Class clazz);

    public PutMappingResponse putMapping(Class clazz, String mapping);

    public DeleteMappingResponse deleteMapping(Class clazz);

    public String getMapping(Class clazz);

    public IndexResponse index(Object object);

    public BulkResponse bulkIndex(Object... objects);

    public DeleteResponse delete(Object object);

    public DeleteByQueryResponse deleteByQuery(Class clazz, QueryBuilder queryBuilder);

    public BulkResponse bulkDelete(Object... objects);

    public boolean indexExist();

    public CreateIndexResponse createIndex();

    public DeleteIndexResponse deleteIndex();

    public RefreshResponse refreshIndex();

    public IndicesAliasesResponse addAlias(String aliasName);

    public IndicesAliasesResponse removeAlias(String aliasName);

    public boolean aliasExist(String aliasName);


}
