package common.api.tweet;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.tweet.posttweet.PostTweetResponse;
import common.api.tweet.puttweet.PutTweetResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TweetResponse.class, name = "TweetResponse"),
        @JsonSubTypes.Type(value = PostTweetResponse.class, name = "PostTweetResponse"),
        @JsonSubTypes.Type(value = PutTweetResponse.class, name = "PutTweetResponse"),
})
public interface TweetResponse {
}
