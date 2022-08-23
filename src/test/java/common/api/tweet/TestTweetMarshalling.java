package common.api.tweet;

import common.api.serialization.JSONObjectMapper;
import common.api.tweet.puttweet.TweetPut;
import org.junit.jupiter.api.Test;

public class TestTweetMarshalling {

    @Test
    public void testTweetPublishRequest() throws Exception {
        TweetPut.TweetPublishRequest request =
                new TweetPut.TweetPublishRequest("somehandle", "sometext", "somebase64img");
        String jsonReq = JSONObjectMapper.JSONObjectMapper.writeValueAsString(request);
        System.out.println(jsonReq);
    }

}
