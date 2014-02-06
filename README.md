# ElasticSearch OSEM

Object Search Engine Mapping for ElasticSearch

[![Build Status](https://travis-ci.org/kzwang/elasticsearch-osem.png?branch=master)](https://travis-ci.org/kzwang/elasticsearch-osem)


|           OSEM            |   elasticsearch   | Release date |
|---------------------------|-------------------|--------------|
| 2.0.0-SNAPSHOT (master)   |   1.0.0.RC2       |              |
| 2.0.0.RC1                 |   1.0.0.RC2       | 2014-02-06   |
| 1.1.0-SNAPSHOT (1.x)      |   0.90.10         |              |
| 1.0.0                     |   0.90.10         | 2014-02-03   |


## Example

Please see [ElasticSearchOsemIntegrationTest.java](https://github.com/kzwang/elasticsearch-osem/blob/master/src/test/java/com/github/kzwang/osem/api/ElasticSearchOsemIntegrationTest.java) for more examples

Create model:

```Java
    @Indexable(name = "tweetIndex", numericDetection = NumericDetectionEnum.TRUE, allFieldEnabled = false,
            sizeFieldEnabled = true, timestampFieldEnabled = true, timestampFieldPath = "tweetDatetime",
            timestampFieldFormat = "yyyy/MM/dd HH:mm:ss")
    public class Tweet {

        @IndexableId(index = IndexEnum.NOT_ANALYZED)
        @IndexableProperty
        private Long id;

        @IndexableComponent
        private User user;

        @IndexableProperty(store = true, coerce = false, copyTo = {"image"})
        @IndexablePropertyFieldData(loading = FieldDataLoading.EAGER, format = FieldDataFormat.FST, filterRegexPattern = "*",
                                    filterFrequencyMin = "0.001", filterFrequencyMax = "0.1", filterFrequencyMinSegmentSize = "500")
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

        @IndexableProperty(name = "tweetDatetime", format = "yyyy/MM/dd HH:mm:ss")
        public Date getTweetDatetime() {
            return tweetDate;
        }
    }
```
Create Mapping:

```Java
    Client client = nodeBuilder().node().client();
    ElasticSearchIndexer indexer = new ElasticSearchIndexer(client, indexName);
    indexer.createMapping(Tweet.class);
```
Index Object:

```Java
    Tweet tweet = new Tweet();
    ...
    indexer.index(tweet);
```

Delete Object:

```Java    
    Tweet tweet = ...;
    indexer.delete(tweet);
```

Get Object from index:

```Java
    ElasticSearchSearcher searcher = new ElasticSearchSearcherImpl(client, indexName);
    Tweet tweet = searcher.getById(Tweet.class, tweet.getId().toString());
```
    
Search Object:

```Java
    List<Tweet> searchResult = searcher.search(Tweet.class, QueryBuilders.matchAllQuery(), null);
```

## Maven
```xml
    <dependency>
        <groupId>com.github.kzwang</groupId>
        <artifactId>elasticsearch-osem</artifactId>
        <version>2.0.0.RC1</version>
    </dependency>
```