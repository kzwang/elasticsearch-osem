package com.github.kzwang.osem.impl;

import com.github.kzwang.osem.api.ElasticSearchSearcher;
import com.github.kzwang.osem.processor.MappingProcessor;
import com.github.kzwang.osem.processor.ObjectProcessor;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.Preconditions;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.List;


public class ElasticSearchSearcherImpl implements ElasticSearchSearcher {

    private static final ESLogger logger = Loggers.getLogger(ElasticSearchSearcherImpl.class);

    private Client client;

    private String indexName;

    private ObjectProcessor objectProcessor;

    public ElasticSearchSearcherImpl(Client client, String indexName) {
        this.client = client;
        this.indexName = indexName;
        objectProcessor = new ObjectProcessor();
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    @Override
    public Long count(Class clazz, @Nullable QueryBuilder queryBuilder) {
        CountRequestBuilder countRequestBuilder = client.prepareCount(getIndexName());
        if (queryBuilder != null) {
            countRequestBuilder.setQuery(queryBuilder);
        }


        String typeName = MappingProcessor.getIndexTypeName(clazz);
        countRequestBuilder.setTypes(typeName);

        if (logger.isDebugEnabled()) {
            String queryStr = "";
            if (queryBuilder != null) {
                queryStr = queryBuilder.buildAsBytes().toUtf8();
            }
            logger.debug("Count for class: {}, query: {}", clazz.getSimpleName(), queryStr);
        }

        return countRequestBuilder.get().getCount();
    }


    @Override
    public SearchResponse search(SearchRequestBuilder requestBuilder) {
        Preconditions.checkArgument(requestBuilder.request().types().length > 0, "Must have at least one type");
        return requestBuilder.get();
    }

    @Override
    public <T> List<T> search(Class<T> clazz, @Nullable QueryBuilder queryBuilder, @Nullable FilterBuilder filterBuilder) {
        SearchRequestBuilder builder = getSearchRequestBuilder(clazz);
        if (queryBuilder != null) {
            builder.setQuery(queryBuilder);
        }

        if (filterBuilder != null) {
            builder.setPostFilter(filterBuilder);
        }

        if (logger.isDebugEnabled()) {
            String queryStr = "";
            String filterStr = "";
            if (queryBuilder != null) {
                queryStr = queryBuilder.buildAsBytes().toUtf8();
            }
            if (filterBuilder != null) {
                filterStr = filterBuilder.buildAsBytes().toUtf8();
            }
            logger.debug("Search for class: {}, query: {}, filter: {}", clazz.getSimpleName(), queryStr, filterStr);
        }

        return search(clazz, builder);
    }

    @Override
    public <T> List<T> search(Class<T> clazz, SearchRequestBuilder requestBuilder) {
        Preconditions.checkArgument(requestBuilder.request().types().length > 0, "Must have at least one type");
        SearchResponse response = requestBuilder.get();
        List<T> results = new ArrayList<T>();
        if (response != null && response.getHits() != null) {
            SearchHit[] hits = response.getHits().getHits();
            if (hits != null && hits.length > 0) {
                for (SearchHit hit : hits) {
                    T t = objectProcessor.fromJsonString(hit.getSourceAsString(), clazz);
                    results.add(t);
                }
            }
        }
        return results;
    }

    @Override
    public SearchRequestBuilder getSearchRequestBuilder(Class... clazz) {
        Preconditions.checkArgument(clazz.length > 0, "Must have at least one class");
        SearchRequestBuilder builder = client.prepareSearch(getIndexName());
        List<String> typeNames = new ArrayList<String>();
        for (Class c : clazz) {
            String typeName = MappingProcessor.getIndexTypeName(c);
            if (typeName != null) {
                typeNames.add(typeName);
            }
        }

        builder.setTypes(typeNames.toArray(new String[typeNames.size()]));

        return builder;
    }

    @Override
    public <T> T getById(Class<T> clazz, String id) {
        String typeName = MappingProcessor.getIndexTypeName(clazz);
        if (logger.isDebugEnabled()) {
            logger.debug("Get object by id, class: {}, type: {}, id: {}", clazz.getSimpleName(), typeName, id);
        }
        GetResponse response = client.prepareGet(getIndexName(), typeName, id).get();
        if (response == null || response.getSourceAsString() == null) {
            return null;
        }
        return objectProcessor.fromJsonString(response.getSourceAsString(), clazz);
    }

    @Override
    public <T> List<T> getByIds(Class<T> clazz, List<String> ids) {
        String typeName = MappingProcessor.getIndexTypeName(clazz);
        if (logger.isDebugEnabled()) {
            logger.debug("Get objects by ids, class: {}, type: {}, ids: {}", clazz.getSimpleName(), typeName, ids);
        }
        MultiGetResponse responses = client.prepareMultiGet().add(getIndexName(), typeName, ids).get();
        List<T> results = new ArrayList<T>();
        if (responses != null) {
            for (MultiGetItemResponse response : responses) {
                if (response.getResponse() != null) {
                    results.add(objectProcessor.fromJsonString(response.getResponse().getSourceAsString(), clazz));
                }
            }
        }
        return results;
    }
}
