package common.api.like.postlike;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.error.ResponseError;
import common.api.like.putlike.PutLike;

import java.util.List;

public class PostLike {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = PostLike.LikeGetHandlesRequest.class, name = "LikeGetHandlesRequest")
    })
    public static class LikeGetHandlesRequest implements PostLikeRequest {
        public String tweetId;

        public LikeGetHandlesRequest() {
        }

        public LikeGetHandlesRequest(String tweetId) {
            this.tweetId = tweetId;
        }
    }

    public static class LikeGetResponseHandles implements PostLikeResponse {
        public List<String> handles;

        public LikeGetResponseHandles() {
        }

        public LikeGetResponseHandles(List<String> handles) {
            this.handles = handles;
        }
    }

    public static abstract class LikeGetResponseFailure extends ResponseError {
        public LikeGetResponseFailure() {
        }

        public LikeGetResponseFailure(String failure) {
            super(failure);
        }
    }

    public static class LikeGetResponseFailed extends PutLike.LikeResponseFailure {
        public LikeGetResponseFailed() {
        }

        public LikeGetResponseFailed(String failure) {
            super(failure);
        }
    }

}
