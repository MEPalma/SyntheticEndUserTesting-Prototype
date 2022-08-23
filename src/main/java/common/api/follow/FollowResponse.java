package common.api.follow;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.follow.postfollow.PostFollowResponse;
import common.api.follow.putfollow.PutFollowResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FollowResponse.class, name = "FollowResponse"),
        @JsonSubTypes.Type(value = PostFollowResponse.class, name = "PostFollowResponse"),
        @JsonSubTypes.Type(value = PutFollowResponse.class, name = "PutFollowResponse"),
})
public interface FollowResponse {
}
