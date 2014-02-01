package com.github.kzwang.osem.api;


import com.carrotsearch.randomizedtesting.annotations.*;
import com.github.kzwang.osem.model.TweetComment;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import com.github.kzwang.osem.impl.ElasticSearchIndexerImpl;
import com.github.kzwang.osem.impl.ElasticSearchSearcherImpl;
import com.github.kzwang.osem.model.Tweet;
import com.github.kzwang.osem.test.AbstractOsemTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.common.io.Streams.copyToStringFromClasspath;
import static org.hamcrest.Matchers.*;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

@ThreadLeakFilters(defaultFilters = true, filters = {})
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
@TimeoutSuite(millis = 1200000)
@Listeners({})
public class ElasticSearchOsemIntegrationTest extends AbstractOsemTest {

    private static final ESLogger logger = Loggers.getLogger(ElasticSearchOsemIntegrationTest.class);


    private ElasticSearchIndexer indexer;

    private ElasticSearchSearcher searcher;

    private Node node;

    @Before
    public void setUp() {
        node = nodeBuilder().local(true).node();
        Client client = node.client();
        indexer = new ElasticSearchIndexerImpl(client, "test");
        searcher = new ElasticSearchSearcherImpl(client, "test");
        indexer.deleteIndex();  // delete old index if exist
        indexer.createIndex();
    }

    @After
    public void tearDown() {
        indexer.deleteIndex();
        node.close();
    }

    @Test
    public void test_mapping() throws IOException {
        // create mapping
        indexer.createMapping(Tweet.class);

        // get mapping and check correct
        String expectMapping = copyToStringFromClasspath("/com/github/kzwang/osem/tweet_expect_mapping_from_server.json");
        String mappingFromServer = indexer.getMapping(Tweet.class);
        logger.info("Expected Mapping from server: {}", expectMapping);
        logger.info("Mapping from server: {}", mappingFromServer);
        assertThat(mappingFromServer, equalTo(expectMapping));

        // delete mapping
        indexer.deleteMapping(Tweet.class);
        String mappingShouldNotExist = indexer.getMapping(Tweet.class);
        assertThat(mappingShouldNotExist, nullValue());

    }

    @Test
    public void test_index_get_delete_object() {
        assertThat(indexer.indexExist(), equalTo(true));

        // test index object
        Tweet tweet = getRandomTweet();
        IndexResponse indexResponse = indexer.index(tweet);
        assertThat(Long.parseLong(indexResponse.getId()), equalTo(tweet.getId()));
        assertThat(indexResponse.getIndex(), equalTo("test"));
        assertThat(indexResponse.getType(), equalTo("tweetIndex"));

        // test get object
        Tweet tweetFromIndex = searcher.getById(Tweet.class, tweet.getId().toString());
        checkTweetEquals(tweetFromIndex, tweet);

        // test delete object
        indexer.delete(tweetFromIndex);
        Tweet tweetAfterDelete = searcher.getById(Tweet.class, tweet.getId().toString());
        assertThat(tweetAfterDelete, nullValue());

    }

    @Test
    public void test_bulk_operations() {
        assertThat(indexer.indexExist(), equalTo(true));
        assertThat(searcher.count(Tweet.class, null), equalTo(0l));

        // generate objects
        Integer count = randomIntBetween(10, 50);
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<String> tweetIds = new ArrayList<String>();
        for (int i = 0; i < count; i ++) {
            Tweet tweet = getRandomTweet();
            tweets.add(tweet);
            tweetIds.add(tweet.getId().toString());
        }


        // test bulk index objects
        BulkResponse bulkIndexResponse = indexer.bulkIndex(tweets.toArray());
        assertThat(bulkIndexResponse.hasFailures(), equalTo(false));
        assertThat(bulkIndexResponse.getItems().length, equalTo(count));
        indexer.refreshIndex();
        assertThat(searcher.count(Tweet.class, null), equalTo((long) count));

        // test multi get
        List<Tweet> multiGetTweets = searcher.getByIds(Tweet.class, tweetIds);
        assertThat(multiGetTweets, hasSize(count));

        // test bulk delete
        BulkResponse bulkDeleteResponse = indexer.bulkDelete(tweets.toArray());
        assertThat(bulkDeleteResponse.hasFailures(), equalTo(false));
        assertThat(bulkDeleteResponse.getItems().length, equalTo(count));
        indexer.refreshIndex();
        assertThat(searcher.count(Tweet.class, null), equalTo(0l));
    }

    @Test
    public void test_search() {
        // index object
        Tweet tweet = getRandomTweet();
        indexer.index(tweet);

        // test search
        indexer.refreshIndex();
        List<Tweet> searchResult = searcher.search(Tweet.class, QueryBuilders.matchAllQuery(), null);
        assertThat(searchResult, hasSize(1));
        checkTweetEquals(searchResult.get(0), tweet);

        // delete object
        indexer.delete(tweet);
    }

    @Test
    public void test_parent() {
        // create mapping
        indexer.createMapping(Tweet.class);
        indexer.createMapping(TweetComment.class);

        // index Tweet first
        Tweet tweet = getRandomTweet();
        indexer.index(tweet);

        // index TweetComment
        Integer commentCount = randomIntBetween(1, 50);
        List<TweetComment> commentList = new ArrayList<TweetComment>();
        for (int i = 0; i < commentCount; i ++) {
            commentList.add(getRandomTweetComment(tweet.getId()));
        }
        indexer.bulkIndex(commentList.toArray());

        // test get
        TweetComment comment = searcher.getById(TweetComment.class, commentList.get(0).getId().toString());
        assertThat(comment, nullValue());  // should not found result if no routing set
        comment = searcher.getById(TweetComment.class, commentList.get(0).getId().toString(), commentList.get(0).getTweetId().toString());
        assertThat(comment.getId(), equalTo(comment.getId()));


        // test search
        indexer.refreshIndex();

        // check count for TweetComment
        Long serverCommentCount = searcher.count(TweetComment.class, QueryBuilders.hasParentQuery("tweetIndex", QueryBuilders.termQuery("id", tweet.getId())));
        assertThat(serverCommentCount, equalTo((long) commentCount));

        // check count for Tweet
        Long serverTweetCount = searcher.count(Tweet.class, QueryBuilders.hasChildQuery("tweet_comment", QueryBuilders.termQuery("id", commentList.get(0).getId())));
        assertThat(serverTweetCount, equalTo(1l));


    }




}
