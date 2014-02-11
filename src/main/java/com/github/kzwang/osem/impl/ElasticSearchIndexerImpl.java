package com.github.kzwang.osem.impl;

import com.github.kzwang.osem.api.ElasticSearchIndexer;
import com.github.kzwang.osem.cache.CacheType;
import com.github.kzwang.osem.cache.OsemCache;
import com.github.kzwang.osem.exception.ElasticSearchOsemException;
import com.github.kzwang.osem.processor.MappingProcessor;
import com.github.kzwang.osem.processor.ObjectProcessor;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.Preconditions;
import org.elasticsearch.common.hppc.cursors.ObjectCursor;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.index.query.QueryBuilder;

import java.io.IOException;


public class ElasticSearchIndexerImpl implements ElasticSearchIndexer {

    private static final ESLogger logger = Loggers.getLogger(ElasticSearchIndexerImpl.class);

    private Client client;

    private OsemCache cache;

    private ObjectProcessor objectProcessor;

    private String indexName = null;


    public ElasticSearchIndexerImpl(Client client, String indexName) {
        this.client = client;
        this.indexName = indexName;
        cache = OsemCache.getInstance();
        objectProcessor = new ObjectProcessor();
    }

    @Override
    public String getIndexName() {
        return indexName;
    }

    @Override
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    @Override
    public PutMappingResponse createMapping(Class clazz) {
        String mapping = MappingProcessor.getMappingAsJson(clazz);
        return putMapping(clazz, mapping);
    }

    @Override
    public PutMappingResponse putMapping(Class clazz, String mapping) {
        String typeName = MappingProcessor.getIndexTypeName(clazz);

        if (logger.isDebugEnabled()) {
            logger.debug("Put mapping for class: {}, type: {}, mapping: {}", clazz.getSimpleName(), typeName, mapping);
        }

        PutMappingResponse response = client.admin().indices().preparePutMapping(getIndexName()).setType(typeName).setSource(mapping).get();

        if (!cache.isExist(CacheType.MAPPING, clazz)) {
            cache.putCache(CacheType.MAPPING, clazz, mapping);
        }

        return response;

    }

    @Override
    public DeleteMappingResponse deleteMapping(Class clazz) {
        String typeName = MappingProcessor.getIndexTypeName(clazz);

        if (logger.isDebugEnabled()) {
            logger.debug("Delete mapping for class: {}, type: {}", clazz.getSimpleName(), typeName);
        }

        if (client.admin().indices().prepareTypesExists(getIndexName()).setTypes(typeName).get().isExists()) {
            DeleteMappingResponse response = client.admin().indices().prepareDeleteMapping(getIndexName()).setType(typeName).get();
            cache.removeCache(CacheType.MAPPING, clazz);
            return response;
        }

        return null;
    }

    @Override
    public String getMapping(Class clazz) {
        String typeName = MappingProcessor.getIndexTypeName(clazz);
        if (logger.isDebugEnabled()) {
            logger.debug("Get mapping for class: {}, type: {}", clazz.getSimpleName(), typeName);
        }
        ClusterStateResponse response = client.admin().cluster().prepareState().setIndices(getIndexName()).get();
        MetaData metaData = response.getState().metaData();
        if (metaData.iterator().hasNext()) {
            IndexMetaData indexMetaData = metaData.iterator().next();
            for (ObjectCursor<MappingMetaData> cursor : indexMetaData.mappings().values()) {
                MappingMetaData mappingMd = cursor.value;
                if (mappingMd.type().equals(typeName)) {
                    try {
                        return mappingMd.source().string();
                    } catch (IOException e) {
                        logger.error("Failed convert mapping to string", e);
                    }
                }
            }
        }

        return null;
    }

    private IndexRequestBuilder getIndexRequest(Object object) {
        Class objectClass = object.getClass();
        String typeName = MappingProcessor.getIndexTypeName(objectClass);
        Object objectId = objectProcessor.getIdValue(object);
        if (objectId == null) {
            throw new ElasticSearchOsemException("Unable to find object id");
        }

        String objectJson = objectProcessor.toJsonString(object);

        logger.debug("Get index object request, type:{}, id: {}, content: {}", typeName, objectId, objectJson);

        if (!cache.isExist(CacheType.MAPPING, objectClass)) {  // check mapping exist in cache or not
            if (getMapping(objectClass) == null) {  // mapping not exist on server
                createMapping(objectClass);  // create mapping first
            }
        }
        IndexRequestBuilder indexRequestBuilder = client.prepareIndex(getIndexName(), typeName, objectId.toString());
        indexRequestBuilder.setSource(objectJson);
        String routing = objectProcessor.getRoutingId(object);
        if (routing != null) {
            indexRequestBuilder.setRouting(routing);
        }
        String parent = objectProcessor.getParentId(object);
        if (parent != null) {
            indexRequestBuilder.setParent(parent);
        }
        return indexRequestBuilder;
    }

    @Override
    public IndexResponse index(Object object) {
        return getIndexRequest(object).get();
    }

    @Override
    public BulkResponse bulkIndex(Object... objects) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        logger.debug("Bulk index {} objects", objects.length);
        for (Object object : objects) {
            if (object != null) {
                if (object instanceof IndexRequestBuilder) {
                    bulkRequest.add((IndexRequestBuilder) object);
                } else {
                    bulkRequest.add(getIndexRequest(object));
                }
            }
        }
        return bulkRequest.get();

    }

    private DeleteRequestBuilder getDeleteRequest(Object object) {
        String typeName = MappingProcessor.getIndexTypeName(object.getClass());
        Object objectId = objectProcessor.getIdValue(object);
        if (objectId == null) {
            throw new ElasticSearchOsemException("Unable to find object id");
        }
        logger.debug("Get delete object request, type:{}, id: {}", typeName, objectId);
        DeleteRequestBuilder deleteRequestBuilder = client.prepareDelete(getIndexName(), typeName, objectId.toString());
        String routing = objectProcessor.getRoutingId(object);
        if (routing != null) {
            deleteRequestBuilder.setRouting(routing);
        }
        String parent = objectProcessor.getParentId(object);
        if (parent != null) {
            deleteRequestBuilder.setParent(parent);
        }
        return deleteRequestBuilder;
    }

    @Override
    public DeleteResponse delete(Object object) {
        return getDeleteRequest(object).get();
    }


    @Override
    public BulkResponse bulkDelete(Object... objects) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        logger.debug("Bulk delete {} objects", objects.length);
        for (Object object : objects) {
            if (object != null) {
                if (object instanceof DeleteRequestBuilder) {
                    bulkRequest.add((DeleteRequestBuilder) object);
                } else {
                    bulkRequest.add(getDeleteRequest(object));
                }
            }
        }
        return bulkRequest.get();
    }


    @Override
    public DeleteByQueryResponse deleteByQuery(Class clazz, QueryBuilder queryBuilder) {
        String typeName = MappingProcessor.getIndexTypeName(clazz);
        return client.prepareDeleteByQuery(getIndexName()).setQuery(queryBuilder).setTypes(typeName).get();
    }


    @Override
    public boolean indexExist() {
        return client.admin().indices().prepareExists(getIndexName()).get().isExists();
    }

    @Override
    public CreateIndexResponse createIndex() {
        logger.debug("Create index: {}", getIndexName());
        if (!indexExist()) {
            return client.admin().indices().prepareCreate(getIndexName()).get();
        }
        logger.warn("Index {} already exist, cannot create", getIndexName());
        return null;

    }

    @Override
    public DeleteIndexResponse deleteIndex() {
        logger.debug("Delete index: {}", getIndexName());
        if (indexExist()) {
            return client.admin().indices().prepareDelete(getIndexName()).get();
        }
        logger.warn("Index {} not exist, cannot delete", getIndexName());
        return null;
    }

    @Override
    public RefreshResponse refreshIndex() {
        logger.debug("Refresh index: {}", getIndexName());
        return client.admin().indices().prepareRefresh(getIndexName()).get();
    }

    @Override
    public IndicesAliasesResponse addAlias(String aliasName) {
        logger.debug("Add alias {} to index: {}", aliasName, getIndexName());
        return client.admin().indices().prepareAliases().addAlias(getIndexName(), aliasName).get();
    }

    @Override
    public IndicesAliasesResponse removeAlias(String aliasName) {
        logger.debug("Remove alias {} from index: {}", aliasName, getIndexName());
        return client.admin().indices().prepareAliases().removeAlias(getIndexName(), aliasName).get();
    }

    @Override
    public boolean aliasExist(String aliasName) {
        return client.admin().indices().prepareAliasesExist(aliasName).get().isExists();
    }
}
