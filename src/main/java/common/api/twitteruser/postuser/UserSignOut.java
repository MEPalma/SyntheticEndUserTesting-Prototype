package common.api.twitteruser.postuser;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.error.ResponseError;

public class UserSignOut {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = UserSignOut.UserSignOutRequest.class, name = "UserSignOutRequest")
    })
    public static class UserSignOutRequest implements PostUserRequest {
        public String handle;
        public String token;

        public UserSignOutRequest() {
        }

        public UserSignOutRequest(String handle, String token) {
            this.handle = handle;
            this.token = token;
        }
    }

    public static class UserSignOutSuccess implements PostUserResponse {
    }

    public static class UserSignOutFailed extends ResponseError {
    }
}
