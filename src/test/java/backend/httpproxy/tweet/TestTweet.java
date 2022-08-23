package backend.httpproxy.tweet;

import backend.Backend;
import backend.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.LinkedList;


public class TestTweet {
    private final Backend backend;

    public TestTweet() throws IOException {
        this.backend = new Backend();
    }

    @Test
    public void testTweeRetweet() throws Exception {
        var h1 = "handle1";
        var h1Pass = h1+h1;
        var h1Tok = TestUtils.registerAndLogin(h1, h1Pass);

        var tweetIds = new LinkedList<String>();
        assert TestUtils.getTweets(h1Tok, h1, tweetIds).tweets.size() == 0;

        tweetIds.add("somethinginvalid");
        assert TestUtils.getTweets(h1Tok, h1, tweetIds).tweets.size() == 1;

        var h1t1id = TestUtils.tweet(h1, h1Tok, "Some tweet text");
        tweetIds.add(h1t1id);
        var gottenTweets = TestUtils.getTweets(h1Tok, h1, tweetIds).tweets;
        assert gottenTweets.size() == 2;
        var gottenTweet2 = gottenTweets.get(1);
        assert gottenTweet2.text.equals("Some tweet text");
        assert gottenTweet2.base64Img == null;
        assert gottenTweet2.tweetId.equals(h1t1id);

        var h1t2id = TestUtils.retweet(h1, h1Tok, "Some retweet text", h1t1id);
        tweetIds.add(h1t2id);
        gottenTweets = TestUtils.getTweets(h1Tok, h1, tweetIds).tweets;
        assert gottenTweets.size() == 3;
        gottenTweet2 = gottenTweets.get(1);
        assert gottenTweet2.text.equals("Some tweet text");
        assert gottenTweet2.base64Img == null;
        assert gottenTweet2.tweetId.equals(h1t1id);
        var gottenTweet3 = gottenTweets.get(2);
        assert gottenTweet3.text.equals("Some retweet text");
        assert gottenTweet3.base64Img == null;
        assert gottenTweet3.tweetId.equals(h1t2id);
        assert gottenTweet3.retweetOf.equals(h1t1id);

        var h2 = "handle2";
        var h2Pass = h2+h2;
        var h2Tok = TestUtils.registerAndLogin(h2, h2Pass);

        var h2t1id = TestUtils.tweet(h2, h2Tok, "Some h2 tweet text");

        var sample = TestUtils.sampleTweets(h1Tok, h1, 0);
        assert sample.tweets.size() == 2;
        assert sample.tweets.get(0).tweetId.equals(h1t1id);
        assert sample.tweets.get(1).tweetId.equals(h1t2id);

        TestUtils.followAdd(h1Tok, h1, h2);

        sample = TestUtils.sampleTweets(h1Tok, h1, 0);
        assert sample.tweets.size() == 3;
        assert sample.tweets.get(0).tweetId.equals(h1t1id);
        assert sample.tweets.get(1).tweetId.equals(h1t2id);
        assert sample.tweets.get(2).tweetId.equals(h2t1id);

        sample = TestUtils.sampleTweets(h1Tok, h1, sample.tweets.get(1).epochCreated);
        assert sample.tweets.size() == 1;
        assert sample.tweets.get(0).tweetId.equals(h2t1id);

        sample = TestUtils.latestTweets(h1Tok, h1, 0);
        assert sample.tweets.size() == 2;
        assert sample.tweets.get(0).tweetId.equals(h1t1id);
        assert sample.tweets.get(1).tweetId.equals(h1t2id);

        sample = TestUtils.latestTweets(h1Tok, h1, sample.tweets.get(0).epochCreated);
        assert sample.tweets.size() == 1;
        assert sample.tweets.get(0).tweetId.equals(h1t2id);
    }
}
