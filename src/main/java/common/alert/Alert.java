package common.alert;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FollowAlert.class, name = "FollowAlert"),
        @JsonSubTypes.Type(value = LikeAlert.class, name = "LikeAlert"),
        @JsonSubTypes.Type(value = RetweetAlert.class, name = "RetweetAlert"),
        @JsonSubTypes.Type(value = UserLogin.UserLoginResponse.class, name = "UserLoginResponse"),
})
public interface Alert {
}
