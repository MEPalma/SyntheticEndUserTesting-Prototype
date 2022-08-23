package common.api.twitteruser.postuser;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.error.ResponseError;

public class UserSignin {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = UserSignin.UserSigninRequest.class, name = "UserSigninRequest")
    })
    public static class UserSigninRequest implements PostUserRequest {
        public String handle;
        public String passwd;

        public UserSigninRequest() {
        }

        public UserSigninRequest(String handle, String passwd) {
            this.handle = handle;
            this.passwd = passwd;
        }
    }

    public static class UserSigninSuccess implements PostUserResponse {
        public String token;

        public UserSigninSuccess() {
        }

        public UserSigninSuccess(String token) {
            this.token = token;
        }
    }

    public static class UserSigninFailed extends ResponseError {
        public UserSigninFailed() {
            super("Invalid credentials.");
        }
    }
}
