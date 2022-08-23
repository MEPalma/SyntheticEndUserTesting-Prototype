package common.api.twitteruser.putuser;

import common.api.twitteruser.UserRequest;
import common.comms.request.PutRequest;

public interface PutUserRequest extends UserRequest, PutRequest {
    default String getContext() {
        return UserRequest.CONTEXT;
    }
}
