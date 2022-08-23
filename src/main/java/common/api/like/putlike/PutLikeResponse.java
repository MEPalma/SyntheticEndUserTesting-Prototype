package common.api.like.putlike;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.like.LikeResponse;
import common.comms.response.PutResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PutLike.LikeResponseSuccess.class, name = "LikeResponseSuccess"),
        @JsonSubTypes.Type(value = PutLike.LikeResponseFailed.class, name = "LikeResponseFailed"),
        @JsonSubTypes.Type(value = PutLike.LikeResponseNoSuchTweet.class, name = "LikeResponseNoSuchTweet"),
        @JsonSubTypes.Type(value = PutLike.LikeResponseNoSuchUser.class, name = "LikeResponseNoSuchUser"),
})
public interface PutLikeResponse extends PutResponse, LikeResponse {
}
