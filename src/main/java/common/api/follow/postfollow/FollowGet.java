package common.api.follow.postfollow;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.error.ResponseError;

import java.util.List;

public class FollowGet {

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = FollowGetFollowersRequest.class, name = "FollowGetFollowersRequest")
    })
    public static class FollowGetFollowersRequest implements PostFollowRequest {
        public String handle;

        public FollowGetFollowersRequest() {
        }

        public FollowGetFollowersRequest(String handle) {
            this.handle = handle;
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = FollowGetFollowingRequest.class, name = "FollowGetFollowingRequest")
    })
    public static class FollowGetFollowingRequest implements PostFollowRequest {
        public String handle;

        public FollowGetFollowingRequest() {
        }

        public FollowGetFollowingRequest(String handle) {
            this.handle = handle;
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = FollowGetCountRequest.class, name = "FollowGetCountRequest")
    })
    public static class FollowGetCountRequest implements PostFollowRequest {
        public String handle;

        public FollowGetCountRequest() {
        }

        public FollowGetCountRequest(String handle) {
            this.handle = handle;
        }
    }

    public static class FollowGetResponseHandles implements PostFollowResponse {
        public List<String> followerHandles;

        public FollowGetResponseHandles() {
        }

        public FollowGetResponseHandles(List<String> followerHandles) {
            this.followerHandles = followerHandles;
        }
    }

    public static class FollowGetResponseCount implements PostFollowResponse {
        public long following;
        public long followers;

        public FollowGetResponseCount() {
        }

        public FollowGetResponseCount(long following, long followers) {
            this.following = following;
            this.followers = followers;
        }
    }

    public abstract static class FollowGetResponseFailure extends ResponseError {
        public FollowGetResponseFailure() {
        }

        public FollowGetResponseFailure(String failure) {
            super(failure);
        }
    }

    public static class FollowGetResponseFailed extends FollowGetResponseFailure {
        public FollowGetResponseFailed() {
        }

        public FollowGetResponseFailed(String failure) {
            super(failure);
        }
    }

}
