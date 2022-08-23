package common.api.tweet.posttweet;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.tweet.TweetResponse;
import common.comms.response.PostResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TweetPost.TweetGetResponseSuccess.class, name = "TweetGetResponseSuccess"),
        @JsonSubTypes.Type(value = TweetPost.TweetGetResponseFailure.class, name = "TweetGetResponseFailure"),
        @JsonSubTypes.Type(value = TweetPost.TweetGetResponseFailed.class, name = "TweetGetResponseFailed"),
})
public interface PostTweetResponse extends PostResponse, TweetResponse {
}
