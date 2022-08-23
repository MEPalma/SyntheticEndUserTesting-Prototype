package common.api.twitteruser.postuser;

import common.api.twitteruser.UserRequest;
import common.comms.request.PostRequest;

public interface PostUserRequest extends UserRequest, PostRequest {
    default String getContext() {
        return UserRequest.CONTEXT;
    }
}
