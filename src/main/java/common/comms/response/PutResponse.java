package common.comms.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.alert.putalert.PutAlert;
import common.api.alert.putalert.PutAlertResponse;
import common.api.follow.putfollow.PutFollowResponse;
import common.api.like.putlike.PutLikeResponse;
import common.api.tweet.puttweet.PutTweetResponse;
import common.api.twitteruser.putuser.PutUserResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PutFollowResponse.class, name = "PutFollowResponse"),
        @JsonSubTypes.Type(value = PutLikeResponse.class, name = "PutLikeResponse"),
        @JsonSubTypes.Type(value = PutTweetResponse.class, name = "PutTweetResponse"),
        @JsonSubTypes.Type(value = PutUserResponse.class, name = "PutUserResponse"),
        @JsonSubTypes.Type(value = PutAlertResponse.class, name = "PutAlertResponse"),
})
public interface PutResponse {
}
