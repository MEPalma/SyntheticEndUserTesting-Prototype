package common.api.like.postlike;

import common.api.like.LikeRequest;
import common.comms.request.PostRequest;

public interface PostLikeRequest extends LikeRequest, PostRequest {
    default String getContext() {
        return LikeRequest.CONTEXT;
    }
}
