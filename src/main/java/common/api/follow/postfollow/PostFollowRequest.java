package common.api.follow.postfollow;

import common.api.follow.FollowRequest;
import common.comms.request.PostRequest;

public interface PostFollowRequest extends FollowRequest, PostRequest {
    default String getContext() {
        return FollowRequest.CONTEXT;
    }
}
