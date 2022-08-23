package common.alert;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.error.ResponseError;

public class UserLogin {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = UserLogin.UserLoginRequest.class, name = "UserLoginRequest")
    })
    public static class UserLoginRequest {
        public String token;
        public String handle;

        public UserLoginRequest() {
        }

        public UserLoginRequest(String token, String handle) {
            this.token = token;
            this.handle = handle;
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = UserLogin.UserLoginSuccess.class, name = "UserLoginSuccess"),
            @JsonSubTypes.Type(value = UserLogin.UserLoginFailed.class, name = "UserLoginFailed"),
    })
    public interface UserLoginResponse extends Alert {
    }

    public static class UserLoginSuccess implements UserLoginResponse {
    }

    public static class UserLoginFailed implements UserLoginResponse {
    }
}
