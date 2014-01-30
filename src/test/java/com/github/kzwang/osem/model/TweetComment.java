package com.github.kzwang.osem.model;

import com.github.kzwang.osem.annotations.Indexable;
import com.github.kzwang.osem.annotations.IndexableId;
import com.github.kzwang.osem.annotations.IndexableProperty;

@Indexable(parentClass = Tweet.class, parentIdField = "tweetId")
public class TweetComment {

    @IndexableId
    @IndexableProperty
    private Long id;

    @IndexableProperty
    private Long tweetId;

    @IndexableProperty
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
