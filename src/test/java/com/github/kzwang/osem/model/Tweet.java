package com.github.kzwang.osem.model;

import com.github.kzwang.osem.annotations.*;
import com.github.kzwang.osem.serializer.ImageSerializer;

import java.util.Date;
import java.util.List;

@Indexable(name = "tweetIndex", numericDetection = NumericDetectionEnum.TRUE, allFieldEnabled = false,
        sizeFieldEnabled = true, timestampFieldEnabled = true, timestampFieldPath = "tweetDatetime",
        timestampFieldFormat = "yyyy/MM/dd HH:mm:ss")
public class Tweet {

    @IndexableId(index = IndexEnum.NOT_ANALYZED)
    @IndexableProperty
    private Long id;

    @IndexableComponent
    private User user;

    @IndexableProperty(store = true, coerce = false, copyTo = {"image"}, fieldDataLoading = FieldDataLoading.EAGER,
                        fieldDataFormat = FieldDataFormat.FST, fieldDataFilterRegexPattern = "*", fieldDataFilterFrequencyMin = "0.001",
                        fieldDataFilterFrequencyMax = "0.1", fieldDataFilterFrequencyMinSegmentSize = "500")
    private String tweetString;

    @IndexableProperty(format = "basic_date||yyyy/MM/dd")
    private Date tweetDate;

    @IndexableProperty(serializer = ImageSerializer.class, jsonInclude = JsonInclude.ALWAYS, docValuesFormat = DocValuesFormatEnum.DISK)
    private String image;

    @IndexableProperty(analyzer = "standard")
    private List<String> urls;

    @IndexableComponent(name = "mentionedUsers")
    private List<User> mentionedUserList;

    @IndexableProperty
    private Boolean flagged;

    @IndexableProperty(format = "basic_date_time_no_millis")
    private List<Date> specialDates;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTweetString() {
        return tweetString;
    }

    public void setTweetString(String tweetString) {
        this.tweetString = tweetString;
    }

    public Date getTweetDate() {
        return tweetDate;
    }

    @IndexableProperty(name = "tweetDatetime", format = "yyyy/MM/dd HH:mm:ss")
    public Date getTweetDatetime() {
        return tweetDate;
    }

    public void setTweetDate(Date tweetDate) {
        this.tweetDate = tweetDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<User> getMentionedUserList() {
        return mentionedUserList;
    }

    public void setMentionedUserList(List<User> mentionedUserList) {
        this.mentionedUserList = mentionedUserList;
    }

    public Boolean getFlagged() {
        return flagged;
    }

    public void setFlagged(Boolean flagged) {
        this.flagged = flagged;
    }

    public List<Date> getSpecialDates() {
        return specialDates;
    }

    public void setSpecialDates(List<Date> specialDates) {
        this.specialDates = specialDates;
    }
}
