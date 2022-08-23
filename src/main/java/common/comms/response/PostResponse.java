package common.comms.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.follow.postfollow.PostFollowResponse;
import common.api.like.postlike.PostLikeResponse;
import common.api.tweet.posttweet.PostTweetResponse;
import common.api.twitteruser.postuser.PostUserResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PostFollowResponse.class, name = "PostFollowResponse"),
        @JsonSubTypes.Type(value = PostLikeResponse.class, name = "PostLikeResponse"),
        @JsonSubTypes.Type(value = PostTweetResponse.class, name = "PostTweetResponse"),
        @JsonSubTypes.Type(value = PostUserResponse.class, name = "PostUserResponse"),
})
public interface PostResponse {

}
