package common.api.follow.putfollow;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.follow.FollowResponse;
import common.comms.response.PutResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FollowPut.FollowResponseSuccess.class, name = "FollowResponseSuccess"),
        @JsonSubTypes.Type(value = FollowPut.FollowResponseFailed.class, name = "FollowResponseFailed"),
        @JsonSubTypes.Type(value = FollowPut.FollowResponseNoSuchFollowing.class, name = "FollowResponseNoSuchFollowing"),
        @JsonSubTypes.Type(value = FollowPut.FollowResponseNoSuchFollowed.class, name = "FollowResponseNoSuchFollowed"),
})
public interface PutFollowResponse extends PutResponse, FollowResponse {
}
