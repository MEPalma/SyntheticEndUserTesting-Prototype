package common.api.follow.postfollow;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.follow.FollowResponse;
import common.comms.response.PostResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FollowGet.FollowGetResponseHandles.class, name = "FollowGetResponseHandles"),
        @JsonSubTypes.Type(value = FollowGet.FollowGetResponseCount.class, name = "FollowGetResponseCount"),
        @JsonSubTypes.Type(value = FollowGet.FollowGetResponseFailure.class, name = "FollowGetResponseFailure"),
        @JsonSubTypes.Type(value = FollowGet.FollowGetResponseFailed.class, name = "FollowGetResponseFailed"),
})
public interface PostFollowResponse extends PostResponse, FollowResponse {
}
