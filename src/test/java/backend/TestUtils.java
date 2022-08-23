package backend;

import common.alert.Alert;
import common.api.follow.postfollow.FollowGet;
import common.api.follow.putfollow.FollowPut;
import common.api.tweet.posttweet.TweetPost;
import common.api.tweet.puttweet.TweetPut;
import common.alert.UserLogin;
import common.api.twitteruser.postuser.UserSignin;
import common.api.twitteruser.putuser.UserSignup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import static common.api.serialization.JSONObjectMapper.JSONObjectMapper;
import static common.comms.HttpApiComms.BACKEND_CONN_ENDPOINT_PORT;

public class TestUtils {

    public static void signupUser(String handle, String passwd) throws Exception {
        var oSignupRes = new UserSignup.UserSignupRequest(handle, passwd).send();
        assert oSignupRes instanceof UserSignup.UserSignupResponseSuccess;
    }

    public static String signinUser(String handle, String passwd) throws Exception {
        var res = new UserSignin.UserSigninRequest(handle, passwd).send();
        assert res instanceof UserSignin.UserSigninSuccess;
        return ((UserSignin.UserSigninSuccess) res).token;
    }

    public static void loginUser(String token, String handle) throws Exception {
        Socket socket = new Socket("localhost", BACKEND_CONN_ENDPOINT_PORT);
        PrintWriter socketOut = new PrintWriter(socket.getOutputStream(), false);
        BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        socketOut.println(JSONObjectMapper.writeValueAsString(new UserLogin.UserLoginRequest(token, handle)));
        socketOut.flush();

        Alert loginResponse =
                JSONObjectMapper.readValue(socketIn.readLine(), Alert.class);
        assert loginResponse instanceof UserLogin.UserLoginSuccess;
    }

    public static String registerAndLogin(String handle, String passwd) throws Exception {
        signupUser(handle, passwd);
        var tok = signinUser(handle, passwd);
        loginUser(tok, handle);
        return tok;
    }

    public static String tweet(String handle, String tok, String txt) throws Exception {
        var res = new TweetPut.TweetPublishRequest(handle, txt, null).send(tok);
        assert res instanceof TweetPut.TweetPublishResponseSuccess;
        return ((TweetPut.TweetPublishResponseSuccess) res).tweetId;
    }

    public static String retweet(String handle, String tok, String txt, String tweetId) throws Exception {
        var res = new TweetPut.RetweetPublishRequest(handle, txt, null, tweetId).send(tok);
        assert res instanceof TweetPut.TweetPublishResponseSuccess;
        return ((TweetPut.TweetPublishResponseSuccess) res).tweetId;
    }

    public static TweetPost.TweetGetResponseSuccess getTweets(String tok, String handle, List<String> tweetIds) throws Exception {
        var res = new TweetPost.TweetGetRequest(handle, tweetIds).send(tok);
        assert res instanceof TweetPost.TweetGetResponseSuccess;
        return (TweetPost.TweetGetResponseSuccess) res;
    }

    public static TweetPost.TweetGetResponseSuccess sampleTweets(String tok, String handle, long minEpochCreated) throws Exception {
        var res = new TweetPost.TweetSampleRequest(handle, minEpochCreated).send(tok);
        assert res instanceof TweetPost.TweetGetResponseSuccess;
        return (TweetPost.TweetGetResponseSuccess) res;
    }

    public static TweetPost.TweetGetResponseSuccess latestTweets(String tok, String handle, long minEpochCreated) throws Exception {
        var res = new TweetPost.TweetLatestOfUserRequest(handle, minEpochCreated).send(tok);
        assert res instanceof TweetPost.TweetGetResponseSuccess;
        return (TweetPost.TweetGetResponseSuccess) res;
    }

    public static void followAdd(String tok, String followingHandle, String followedHandle) throws Exception {
        var res = new FollowPut.FollowAddRequest(followingHandle, followedHandle).send(tok);
        assert res instanceof FollowPut.FollowResponseSuccess;
    }

    public static void followRemove(String tok, String followingHandle, String followedHandle) throws Exception {
        var res = new FollowPut.FollowRemoveRequest(followingHandle, followedHandle).send(tok);
        assert res instanceof FollowPut.FollowResponseSuccess;
    }

    public static List<String> getFollowers(String tok, String handle) throws Exception {
        var res = new FollowGet.FollowGetFollowersRequest(handle).send(tok);
        assert res instanceof FollowGet.FollowGetResponseHandles;
        return ((FollowGet.FollowGetResponseHandles) res).followerHandles;
    }

    public static List<String> getFollowing(String tok, String handle) throws Exception {
        var res = new FollowGet.FollowGetFollowingRequest(handle).send(tok);
        assert res instanceof FollowGet.FollowGetResponseHandles;
        return ((FollowGet.FollowGetResponseHandles) res).followerHandles;
    }

    public static FollowGet.FollowGetResponseCount getFollowCount(String tok, String handle) throws Exception {
        var res = new FollowGet.FollowGetFollowersRequest(handle).send(tok);
        assert res instanceof FollowGet.FollowGetResponseCount;
        return (FollowGet.FollowGetResponseCount) res;
    }

}
