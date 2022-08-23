package common.api.follow.putfollow;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.error.ResponseError;

public class FollowPut {

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = FollowPut.FollowAddRequest.class, name = "FollowAddRequest")
    })
    public static class FollowAddRequest implements PutFollowRequest {
        public String followingHandler;
        public String followedHandler;

        public FollowAddRequest() {
        }

        public FollowAddRequest(String followingHandler, String followedHandler) {
            this.followingHandler = followingHandler;
            this.followedHandler = followedHandler;
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = FollowPut.FollowRemoveRequest.class, name = "FollowRemoveRequest")
    })
    public static class FollowRemoveRequest implements PutFollowRequest {
        public String followingHandler;
        public String followedHandler;

        public FollowRemoveRequest() {
        }

        public FollowRemoveRequest(String followingHandler, String followedHandler) {
            this.followingHandler = followingHandler;
            this.followedHandler = followedHandler;
        }
    }

    public static class FollowResponseSuccess implements PutFollowResponse {
    }

    public abstract static class FollowResponseFailure extends ResponseError {
        public FollowResponseFailure() {
        }

        public FollowResponseFailure(String failure) {
            super(failure);
        }
    }

    public static class FollowResponseFailed extends FollowResponseFailure {
        public FollowResponseFailed() {
        }

        public FollowResponseFailed(String failure) {
            super(failure);
        }
    }

    public static class FollowResponseNoSuchFollowing extends FollowResponseFailure {
        public FollowResponseNoSuchFollowing() {
        }

        public FollowResponseNoSuchFollowing(String followerId) {
            super("No such following user with id " + followerId);
        }
    }

    public static class FollowResponseNoSuchFollowed extends FollowResponseFailure {
        public FollowResponseNoSuchFollowed() {
        }

        public FollowResponseNoSuchFollowed(String followerId) {
            super("No such user to follow with id " + followerId);
        }
    }

}
