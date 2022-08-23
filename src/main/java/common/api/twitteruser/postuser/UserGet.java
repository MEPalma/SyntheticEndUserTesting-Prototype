package common.api.twitteruser.postuser;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.error.ResponseError;

import java.util.List;

public class UserGet {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = UserGet.UserGetRequest.class, name = "UserGetRequest"),
            @JsonSubTypes.Type(value = UserGet.UserGetAllRequest.class, name = "UserGetAllRequest")
    })
    public static class UserGetRequest implements PostUserRequest {
        public String handle;
        public String reqHandle;

        public UserGetRequest() {
        }

        public UserGetRequest(String handle, String reqHandle) {
            this.handle = handle;
            this.reqHandle = reqHandle;
        }
    }

    public static class UserGetAllRequest implements PostUserRequest {
        public String handle;

        public UserGetAllRequest() {

        }

        public UserGetAllRequest(String handle) {
            this.handle = handle;
        }
    }

    public static class UserGetResponseSuccess implements PostUserResponse {
        public String handle;
        public String base64Img;
        public int epochCreated;
        public long followers;
        public long following;
        public boolean follows;

        public UserGetResponseSuccess() {
        }

        public UserGetResponseSuccess(String handle, String base64Img, int epochCreated, long followers, long following, boolean follows) {
            this.handle = handle;
            this.base64Img = base64Img;
            this.epochCreated = epochCreated;
            this.followers = followers;
            this.following = following;
            this.follows = follows;
        }
    }

    public static class UserGetAllResponseSuccess implements PostUserResponse {
        public List<UserGetResponseSuccess> users;

        public UserGetAllResponseSuccess() {

        }

        public UserGetAllResponseSuccess(List<UserGetResponseSuccess> users) {
            this.users = users;
        }
    }

    public static abstract class UserGetResponseFailure extends ResponseError {
        public UserGetResponseFailure() {
        }

        public UserGetResponseFailure(String failure) {
            super(failure);
        }
    }

    public static class UserGetResponseFailed extends UserGetResponseFailure {
        public UserGetResponseFailed() {
        }

        public UserGetResponseFailed(String failure) {
            super(failure);
        }
    }

    public static class UserGetResponseNoSuchUser extends UserGetResponseFailure {
        public String handle;

        public UserGetResponseNoSuchUser() {
        }

        public UserGetResponseNoSuchUser(String handle) {
            super("No such user with handle '" + handle + "'.");
            this.handle = handle;
        }
    }

}
