package common.api.like.postlike;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.like.LikeResponse;
import common.comms.response.PostResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PostLikeResponse.class, name = "PostLikeResponse"),
        @JsonSubTypes.Type(value = PostLike.LikeGetResponseHandles.class, name = "LikeGetResponseHandles"),
        @JsonSubTypes.Type(value = PostLike.LikeGetResponseFailure.class, name = "LikeGetResponseFailure"),
        @JsonSubTypes.Type(value = PostLike.LikeGetResponseFailed.class, name = "LikeGetResponseFailed"),
})
public interface PostLikeResponse extends PostResponse, LikeResponse {
}
