package common.api.tweet.puttweet;

import common.api.tweet.TweetRequest;
import common.comms.request.PutRequest;

public interface PutTweetRequest extends TweetRequest, PutRequest {
    default String getContext() {
        return TweetRequest.CONTEXT;
    }
}
