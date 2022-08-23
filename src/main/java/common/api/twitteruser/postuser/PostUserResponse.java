package common.api.twitteruser.postuser;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.twitteruser.UserResponse;
import common.comms.response.PostResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserGet.UserGetResponseSuccess.class, name = "UserGetResponseSuccess"),
        @JsonSubTypes.Type(value = UserGet.UserGetAllResponseSuccess.class, name = "UserGetAllResponseSuccess"),
        @JsonSubTypes.Type(value = UserGet.UserGetResponseFailure.class, name = "UserGetResponseFailure"),
        @JsonSubTypes.Type(value = UserGet.UserGetResponseFailed.class, name = "UserGetResponseFailed"),
        @JsonSubTypes.Type(value = UserGet.UserGetResponseNoSuchUser.class, name = "UserGetResponseNoSuchUser"),
        //
        @JsonSubTypes.Type(value = UserSignin.UserSigninSuccess.class, name = "UserSigninSuccess"),
        @JsonSubTypes.Type(value = UserSignin.UserSigninFailed.class, name = "UserSigninFailed"),
        //
        @JsonSubTypes.Type(value = UserSignOut.UserSignOutSuccess.class, name = "UserSignOutSuccess"),
        @JsonSubTypes.Type(value = UserSignOut.UserSignOutFailed.class, name = "UserSignOutFailed"),
})
public interface PostUserResponse extends PostResponse, UserResponse {
}
