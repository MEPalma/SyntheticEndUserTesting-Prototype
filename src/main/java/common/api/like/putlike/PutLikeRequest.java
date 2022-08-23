package common.api.like.putlike;

import common.api.like.LikeRequest;
import common.comms.request.PutRequest;

public interface PutLikeRequest extends LikeRequest, PutRequest {
    default String getContext() {
        return LikeRequest.CONTEXT;
    }
}
