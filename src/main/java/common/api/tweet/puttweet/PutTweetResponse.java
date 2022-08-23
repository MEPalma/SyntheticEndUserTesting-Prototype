package common.api.tweet.puttweet;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.tweet.TweetResponse;
import common.comms.response.PutResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TweetPut.TweetPublishResponseSuccess.class, name = "TweetPublishResponseSuccess"),
        @JsonSubTypes.Type(value = TweetPut.TweetPublishResponseFailed.class, name = "TweetPublishResponseFailed"),
        @JsonSubTypes.Type(value = TweetPut.TweetPublishResponseEmpty.class, name = "TweetPublishResponseEmpty"),
        @JsonSubTypes.Type(value = TweetPut.TweetPublishResponseInvalidRetweetReference.class, name = "TweetPublishResponseInvalidRetweetReference"),
        @JsonSubTypes.Type(value = TweetPut.TweetPublishResponseNoSuchAuthor.class, name = "TweetPublishResponseNoSuchAuthor"),
})
public interface PutTweetResponse extends PutResponse, TweetResponse {
}
