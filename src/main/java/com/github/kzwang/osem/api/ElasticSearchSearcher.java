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

    public String getIndexName();

    public void setIndexName(String indexName);

    public Long count(Class clazz, @Nullable QueryBuilder queryBuilder);

    /**
     * @param requestBuilder, must get from getSearchRequestBuilder()
     * @return SearchResponse from ElasticSearch
     */
    public SearchResponse search(SearchRequestBuilder requestBuilder);

    public <T> List<T> search(Class<T> clazz, @Nullable QueryBuilder queryBuilder, @Nullable FilterBuilder filterBuilder);

    /**
     * @param clazz
     * @param requestBuilder, must get from getSearchRequestBuilder()
     * @return
     */
    public <T> List<T> search(Class<T> clazz, SearchRequestBuilder requestBuilder);

    public SearchRequestBuilder getSearchRequestBuilder(Class... clazz);

    public <T> T getById(Class<T> clazz, String id);

    public <T> List<T> getByIds(Class<T> clazz, List<String> ids);

}
