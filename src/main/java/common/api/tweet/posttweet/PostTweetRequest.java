package common.api.tweet.posttweet;

import common.api.tweet.TweetRequest;
import common.comms.request.PostRequest;

public interface PostTweetRequest extends TweetRequest, PostRequest {
    default String getContext() {
        return TweetRequest.CONTEXT;
    }
}
