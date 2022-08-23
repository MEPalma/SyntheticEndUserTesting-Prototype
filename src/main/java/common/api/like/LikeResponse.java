package common.api.like;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.like.postlike.PostLikeResponse;
import common.api.like.putlike.PutLikeResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LikeResponse.class, name = "LikeResponse"),
        @JsonSubTypes.Type(value = PutLikeResponse.class, name = "PutLikeResponse"),
        @JsonSubTypes.Type(value = PostLikeResponse.class, name = "PostLikeResponse"),
})
public interface LikeResponse {
}
