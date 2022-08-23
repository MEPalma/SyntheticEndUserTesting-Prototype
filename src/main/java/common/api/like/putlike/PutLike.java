package common.api.like.putlike;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.error.ResponseError;

public class PutLike {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = LikeAddRequest.class, name = "LikeAddRequest")
    })
    public static class LikeAddRequest implements PutLikeRequest {
        public String handle;
        public String tweetId;

        public LikeAddRequest() {
        }

        public LikeAddRequest(String handle, String tweetId) {
            this.handle = handle;
            this.tweetId = tweetId;
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = LikeRemoveRequest.class, name = "LikeRemoveRequest")
    })
    public static class LikeRemoveRequest implements PutLikeRequest {
        public String handle;
        public String tweetId;

        public LikeRemoveRequest() {
        }

        public LikeRemoveRequest(String handle, String tweetId) {
            this.handle = handle;
            this.tweetId = tweetId;
        }
    }

    public static class LikeResponseSuccess implements PutLikeResponse {
    }

    public abstract static class LikeResponseFailure extends ResponseError {
        public LikeResponseFailure() {
        }

        public LikeResponseFailure(String failure) {
            super(failure);
        }
    }

    public static class LikeResponseFailed extends LikeResponseFailure {
        public LikeResponseFailed() {
        }

        public LikeResponseFailed(String failure) {
            super(failure);
        }
    }

    public static class LikeResponseNoSuchTweet extends LikeResponseFailure {
        public LikeResponseNoSuchTweet() {
        }

        public LikeResponseNoSuchTweet(String tweetId) {
            super("No such tweet with id " + tweetId);
        }
    }

    public static class LikeResponseNoSuchUser extends LikeResponseFailure {
        public LikeResponseNoSuchUser() {
        }

        public LikeResponseNoSuchUser(String userId) {
            super("No such user with id " + userId);
        }
    }
}
