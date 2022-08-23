package common.api.twitteruser.putuser;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.twitteruser.UserResponse;
import common.comms.response.PutResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserSignup.UserSignupResponseSuccess.class, name = "UserSignupResponseSuccess"),
        @JsonSubTypes.Type(value = UserSignup.UserSignupResponseFailed.class, name = "UserSignupResponseFailed"),
        @JsonSubTypes.Type(value = UserSignup.UserSignupResponseInvalidHandle.class, name = "UserSignupResponseInvalidHandle"),
        @JsonSubTypes.Type(value = UserSignup.UserSignupResponseInvalidHandleExists.class, name = "UserSignupResponseInvalidHandleExists"),
        @JsonSubTypes.Type(value = UserSignup.UserSignupResponseInvalidPassword.class, name = "UserSignupResponseInvalidPassword"),
})
public interface PutUserResponse extends PutResponse, UserResponse {
}
