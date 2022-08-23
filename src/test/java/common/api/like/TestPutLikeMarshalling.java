package common.api.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import common.api.error.ResponseError;
import common.api.like.putlike.PutLike;
import org.junit.jupiter.api.Test;

import static common.api.serialization.JSONObjectMapper.JSONObjectMapper;

public class TestPutLikeMarshalling {

    @Test
    public void testLikeAddRequestMarshalling() throws JsonProcessingException {
        PutLike.LikeAddRequest likeAddRequest = new PutLike.LikeAddRequest("handle", "tweetId");
        String jsonLine = JSONObjectMapper.writeValueAsString(likeAddRequest);
        assert jsonLine.equals("{\"LikeAddRequest\":{\"handle\":\"handle\",\"tweetId\":\"tweetId\"}}");
        PutLike.LikeAddRequest dLikeAddRequest = JSONObjectMapper.readValue(jsonLine, PutLike.LikeAddRequest.class);
        assert likeAddRequest.handle.equals(dLikeAddRequest.handle);
        assert likeAddRequest.tweetId.equals(dLikeAddRequest.tweetId);
    }

    @Test
    public void testLikeResponseNoSuchUserMarshalling() throws JsonProcessingException {
        PutLike.LikeResponseNoSuchUser lr = new PutLike.LikeResponseNoSuchUser("someuserid");
        String jsonLine = JSONObjectMapper.writeValueAsString(lr);
        ResponseError responseFailure = JSONObjectMapper.readValue(jsonLine, ResponseError.class);
        assert responseFailure.getType().equals("LikeResponseNoSuchUser");
        assert responseFailure.getMessage().equals("No such user with id someuserid");
    }

    @Test
    public void testLikeResponseSuccessDeserialization() throws JsonProcessingException {
        PutLike.LikeResponseSuccess success = new PutLike.LikeResponseSuccess();
        String jsonLine = JSONObjectMapper.writeValueAsString(success);
        LikeResponse likeResponse = JSONObjectMapper.readValue(jsonLine, LikeResponse.class);
        assert likeResponse instanceof PutLike.LikeResponseSuccess;
    }

}
