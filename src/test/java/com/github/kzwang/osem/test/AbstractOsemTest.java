package com.github.kzwang.osem.test;


import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.github.kzwang.osem.model.Tweet;
import com.github.kzwang.osem.model.TweetComment;
import com.github.kzwang.osem.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

public abstract class AbstractOsemTest extends RandomizedTest {

    protected User getRandomUser() {
        User user = new User();
        user.setDescription(randomAsciiOfLengthBetween(100, 10000));
        user.setUserName(randomAsciiOfLengthBetween(10, 100));
        return user;
    }

    protected Tweet getRandomTweet() {
        Tweet tweet = new Tweet();
        tweet.setId(randomLong());
        tweet.setImage(randomAsciiOfLengthBetween(100, 10000));
        tweet.setTweetDate(new Date());
        tweet.setTweetString(randomAsciiOfLengthBetween(100, 10000));
        tweet.setFlagged(randomBoolean());

        List<String> urls = new ArrayList<String>();
        for (int i = 0; i < randomIntBetween(10, 20); i ++) {
            urls.add(randomAsciiOfLengthBetween(10, 100));
        }
        tweet.setUrls(urls);

        tweet.setUser(getRandomUser());

        List<User> userList = new ArrayList<User>();
        for (int i = 0; i < randomIntBetween(10, 20); i ++) {
            userList.add(getRandomUser());
        }
        tweet.setMentionedUserList(userList);

        List<Date> specialDates = new ArrayList<Date>();
        for (int i = 0; i < randomIntBetween(10, 20); i ++) {
            specialDates.add(new Date());
        }
        tweet.setSpecialDates(specialDates);

        return tweet;
    }

    protected TweetComment getRandomTweetComment(Long tweetId) {
        TweetComment tweetComment = new TweetComment();
        tweetComment.setTweetId(tweetId);
        tweetComment.setId(randomLong());
        tweetComment.setComment(randomAsciiOfLengthBetween(100, 10000));
        return tweetComment;
    }

    protected void checkTweetEquals(Tweet tweet, Tweet expected) {
        assertThat(tweet.getId(), equalTo(expected.getId()));
        assertThat(tweet.getImage().toUpperCase().replaceAll("A", ""), equalTo(expected.getImage().toUpperCase().replaceAll("A", "")));  // serializer will convert to upper case and remove all 'A'
        assertThat(tweet.getFlagged(), equalTo(expected.getFlagged()));
        assertThat(tweet.getUrls(), hasSize(expected.getUrls().size()));
        assertThat(tweet.getUrls(), hasItems(expected.getUrls().toArray(new String[expected.getUrls().size()])));
        assertThat(tweet.getUser(), equalTo(expected.getUser()));
        assertThat(tweet.getMentionedUserList(), hasSize(expected.getMentionedUserList().size()));
        assertThat(tweet.getMentionedUserList(), hasItems(expected.getMentionedUserList().toArray(new User[expected.getMentionedUserList().size()])));
    }


}
