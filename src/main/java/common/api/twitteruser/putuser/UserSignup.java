package common.api.twitteruser.putuser;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sun.istack.NotNull;
import common.api.error.ResponseError;

import java.text.MessageFormat;

public class UserSignup {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = UserSignup.UserSignupRequest.class, name = "UserSignupRequest")
    })
    public static class UserSignupRequest implements PutUserRequest {
        public String handle;
        public String passwd;
        public String base64Img;

        public UserSignupRequest() {
        }

        public UserSignupRequest(@NotNull String handle, @NotNull String passwd) {
            this.handle = handle;
            this.passwd = passwd;
            this.base64Img = null;
        }

        public UserSignupRequest(@NotNull String handle, @NotNull String passwd, String base64Img) {
            this.handle = handle;
            this.passwd = passwd;
            this.base64Img = base64Img;
        }
    }

    public static class UserSignupResponseSuccess implements PutUserResponse {
        public String handle;

        public UserSignupResponseSuccess() {
        }

        public UserSignupResponseSuccess(String handle) {
            this.handle = handle;
        }
    }

    public abstract static class UserSignupResponseFailure extends ResponseError {
        public UserSignupResponseFailure() {
        }

        public UserSignupResponseFailure(String failure) {
            super(failure);
        }
    }

    public static class UserSignupResponseFailed extends UserSignupResponseFailure {
        public UserSignupResponseFailed() {
        }

        public UserSignupResponseFailed(String failure) {
            super(failure);
        }
    }

    public static class UserSignupResponseInvalidHandle extends UserSignupResponseFailure {
        public UserSignupResponseInvalidHandle() {
        }

        public UserSignupResponseInvalidHandle(String failure) {
            super(failure);
        }
    }

    public static class UserSignupResponseInvalidHandleExists extends UserSignupResponseInvalidHandle {
        public UserSignupResponseInvalidHandleExists() {
        }

        public UserSignupResponseInvalidHandleExists(String handle) {
            super(MessageFormat.format("Handle {0} already exists. Choose another.", handle));
        }
    }

    public static class UserSignupResponseInvalidPassword extends UserSignupResponseFailure {
        public UserSignupResponseInvalidPassword() {
            super("Invalid password.");
        }
    }
}
