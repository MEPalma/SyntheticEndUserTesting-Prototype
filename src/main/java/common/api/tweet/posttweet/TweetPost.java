package common.api.tweet.posttweet;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.error.ResponseError;

import java.util.Collection;
import java.util.List;

public class TweetPost {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = TweetPost.TweetGetRequest.class, name = "TweetGetRequest")
    })
    public static class TweetGetRequest implements PostTweetRequest {
        public String handle;
        public Collection<String> tweetIds;

        public TweetGetRequest() {
        }

        public TweetGetRequest(String handle, Collection<String> tweetIds) {
            this.handle = handle;
            this.tweetIds = tweetIds;
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = TweetPost.TweetSampleRequest.class, name = "TweetSampleRequest")
    })
    public static class TweetSampleRequest implements PostTweetRequest {
        public String handle;
        public long minEpochCreated;

        public TweetSampleRequest() {
        }

        public TweetSampleRequest(String handle, long minEpochCreated) {
            this.handle = handle;
            this.minEpochCreated = minEpochCreated;
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = TweetPost.TweetLatestOfUserRequest.class, name = "TweetLatestOfUserRequest")
    })
    public static class TweetLatestOfUserRequest implements PostTweetRequest {
        public String handle;
        public long minEpochCreated;

        public TweetLatestOfUserRequest() {
        }

        public TweetLatestOfUserRequest(String handle, long minEpochCreated) {
            this.handle = handle;
            this.minEpochCreated = minEpochCreated;
        }
    }

    public static class TweetGetResponseItem {
        public String authorHandle;
        public String tweetId;
        public long epochCreated;
        public String retweetOf;
        public String text;
        public String base64Img;
        public int likes;
        public boolean liked;

        public TweetGetResponseItem() {
        }

        public TweetGetResponseItem(String authorHandle, String tweetId, long epochCreated, String retweetOf, String text, String base64Img, int likes, boolean liked) {
            this.authorHandle = authorHandle;
            this.tweetId = tweetId;
            this.epochCreated = epochCreated;
            this.retweetOf = retweetOf;
            this.text = text;
            this.base64Img = base64Img;
            this.likes = likes;
            this.liked = liked;
        }
    }

    public static class TweetGetResponseSuccess implements PostTweetResponse {
        public List<TweetGetResponseItem> tweets;

        public TweetGetResponseSuccess() {
        }

        public TweetGetResponseSuccess(List<TweetGetResponseItem> tweets) {
            this.tweets = tweets;
        }
    }

    public abstract static class TweetGetResponseFailure extends ResponseError {
        public TweetGetResponseFailure() {
        }

        public TweetGetResponseFailure(String failure) {
            super(failure);
        }
    }

    public static class TweetGetResponseFailed extends TweetGetResponseFailure {
        public TweetGetResponseFailed() {
        }

        public TweetGetResponseFailed(String failure) {
            super(failure);
        }
    }

}
