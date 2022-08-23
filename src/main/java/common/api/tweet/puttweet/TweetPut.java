package common.api.tweet.puttweet;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.error.ResponseError;

public class TweetPut {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = TweetPut.TweetPublishRequest.class, name = "TweetPublishRequest"),
            @JsonSubTypes.Type(value = TweetPut.RetweetPublishRequest.class, name = "RetweetPublishRequest")
    })
    public static class TweetPublishRequest implements PutTweetRequest {
        public String handle;
        public String txt;
        public String base64Img;

        public TweetPublishRequest() {
        }

        public TweetPublishRequest(String handle, String txt, String base64Img) {
            this.handle = handle;
            this.txt = txt;
            this.base64Img = base64Img;
        }
    }

    public static class RetweetPublishRequest extends TweetPublishRequest {
        public String retweetOf;

        public RetweetPublishRequest() {
        }

        public RetweetPublishRequest(String handle, String txt, String base64Img, String retweetOf) {
            super(handle, txt, base64Img);
            this.retweetOf = retweetOf;
        }
    }

    public static class TweetPublishResponseSuccess implements PutTweetResponse {
        public String tweetId;

        public TweetPublishResponseSuccess() {
        }

        public TweetPublishResponseSuccess(String tweetId) {
            this.tweetId = tweetId;
        }
    }

    public static abstract class TweetPublishResponseFailure extends ResponseError {
        public TweetPublishResponseFailure() {
        }

        public TweetPublishResponseFailure(String failure) {
            super(failure);
        }
    }

    public static class TweetPublishResponseFailed extends TweetPublishResponseFailure {
        public TweetPublishResponseFailed() {
        }

        public TweetPublishResponseFailed(String failure) {
            super(failure);
        }
    }

    public static class TweetPublishResponseNoSuchAuthor extends TweetPublishResponseFailure {
        public TweetPublishResponseNoSuchAuthor() {
        }

        public TweetPublishResponseNoSuchAuthor(String handle) {
            super("No such user with handle " + handle);
        }
    }

    public static class TweetPublishResponseEmpty extends TweetPublishResponseFailure {
        public TweetPublishResponseEmpty() {
            super("Tweets cannot be empty");
        }
    }

    public static class TweetPublishResponseInvalidRetweetReference extends TweetPublishResponseFailure {
        public TweetPublishResponseInvalidRetweetReference() {
        }

        public TweetPublishResponseInvalidRetweetReference(String reference) {
            super("Invalid retweet reference " + reference);
        }
    }

}
