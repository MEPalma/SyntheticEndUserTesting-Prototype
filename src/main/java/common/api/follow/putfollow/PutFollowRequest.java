package common.api.follow.putfollow;

import common.api.follow.FollowRequest;
import common.comms.request.PutRequest;

public interface PutFollowRequest extends FollowRequest, PutRequest {
    default String getContext() {
        return FollowRequest.CONTEXT;
    }
}
