package com.github.kzwang.osem.processor;


import com.github.kzwang.osem.model.TweetComment;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import com.github.kzwang.osem.model.Tweet;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.elasticsearch.common.io.Streams.copyToStringFromClasspath;

import java.io.IOException;


public class MappingProcessorTest {

    private static final ESLogger logger = Loggers.getLogger(MappingProcessorTest.class);

    @Test
    public void test_get_mapping() throws IllegalAccessException, NoSuchFieldException, IOException {
        // mapping for Tweet
        String expectTweetMapping = copyToStringFromClasspath("/com/github/kzwang/osem/tweet_expect_mapping.json");
        String tweetMapping = MappingProcessor.getMappingAsJson(Tweet.class);
        logger.info("Expected Tweet Mapping: {}", expectTweetMapping);
        logger.info("Generated Tweet Mapping: {}", tweetMapping);
        assertThat(tweetMapping, equalToIgnoringWhiteSpace(expectTweetMapping));

        // mapping for TweetComment, parent should be Tweet
        String expectTweetCommentMapping = copyToStringFromClasspath("/com/github/kzwang/osem/tweet_comment_expect_mapping.json");
        String tweetCommentMapping = MappingProcessor.getMappingAsJson(TweetComment.class);
        logger.info("Expected Tweet Comment Mapping: {}", expectTweetCommentMapping);
        logger.info("Generated Tweet Comment Mapping: {}", tweetCommentMapping);
        assertThat(tweetCommentMapping, equalToIgnoringWhiteSpace(expectTweetCommentMapping));
    }
}
