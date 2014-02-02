package com.github.kzwang.osem.processor;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kzwang.osem.model.TweetComment;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import com.github.kzwang.osem.model.Tweet;
import com.github.kzwang.osem.test.AbstractOsemTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.Matchers.*;

public class ObjectProcessorTest extends AbstractOsemTest {

    private static final ESLogger logger = Loggers.getLogger(ObjectProcessorTest.class);

    private ObjectProcessor objectProcessor;

    @Before
    public void setUp(){
        objectProcessor = new ObjectProcessor();
    }

    @Test
    public void test_process_object() throws IllegalAccessException, NoSuchFieldException, IOException {
        Tweet tweet = getRandomTweet();

        String tweetJson = objectProcessor.toJsonString(tweet);

        logger.info("Generated JSON: {}", tweetJson);

        Tweet tweetFromJson = objectProcessor.fromJsonString(tweetJson, Tweet.class);

        Map<String, Object> tweetMap = jsonToMap(tweetJson);

        checkTweetEquals(tweetFromJson, tweet);
        // check date
        assertThat((String) tweetMap.get("tweetDate"), equalTo(new SimpleDateFormat("yyyy/MM/dd").format(tweet.getTweetDate())));
        assertThat((String) tweetMap.get("tweetDatetime"), equalTo(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(tweet.getTweetDate())));

    }

    @Test
    public void test_custom_serializer() {
        // test serialize null value
        Tweet tweet = getRandomTweet();
        tweet.setImage(null);
        String tweetJson = objectProcessor.toJsonString(tweet);
        Map<String, Object> tweetMap = jsonToMap(tweetJson);
        assertThat((String) tweetMap.get("image"), equalTo("NULLSTR"));

        // serializer should convert to upper case and remove all 'A'
        tweet.setImage("AaBbCcDd");
        tweetJson = objectProcessor.toJsonString(tweet);
        tweetMap = jsonToMap(tweetJson);
        assertThat((String) tweetMap.get("image"), equalTo("BBCCDD"));

    }

    @Test
    public void test_get_id(){
        Tweet tweet = getRandomTweet();
        Long id = (Long) objectProcessor.getIdValue(tweet);
        assertThat(id, equalTo(tweet.getId()));
    }

    @Test
    public void test_get_parent_id(){
        Long tweetId = randomLong();
        TweetComment tweetComment = getRandomTweetComment(tweetId);
        String parentId = objectProcessor.getParentId(tweetComment);
        assertThat(parentId, equalTo(tweetComment.getTweetId().toString()));
    }


    private Map<String, Object> jsonToMap(String json) {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
        try {
            HashMap<String,Object> o = mapper.readValue(json, typeRef);
            return o;
        } catch (IOException e) {
            logger.error("Failed convert json to map", e);
        }
        return null;
    }


}
