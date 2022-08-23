package common.api.twitteruser;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.twitteruser.postuser.PostUserResponse;
import common.api.twitteruser.putuser.PutUserResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserResponse.class, name = "UserResponse"),
        @JsonSubTypes.Type(value = PostUserResponse.class, name = "PostUserResponse"),
        @JsonSubTypes.Type(value = PutUserResponse.class, name = "PutUserResponse"),
})
public interface UserResponse {
}
