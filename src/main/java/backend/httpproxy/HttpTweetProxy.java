package backend.httpproxy;

import backend.controller.Controller;
import com.sun.net.httpserver.HttpExchange;
import common.api.tweet.TweetResponse;
import common.api.tweet.posttweet.TweetPost;
import common.api.tweet.puttweet.TweetPut;

import java.io.IOException;

import static common.api.serialization.JSONObjectMapper.JSONObjectMapper;

public class HttpTweetProxy extends HttpBaseProxy {
    public enum TwitterPostXTarget {
        TweetGetRequest,
        TweetSampleRequest,
        TweetLatestOfUserRequest
    }

    public enum TwitterPutXTarget {
        TweetPublishRequest,
        RetweetPublishRequest
    }

    public HttpTweetProxy(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        handleInvalid(exchange);
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        TwitterPostXTarget target;
        try {
            target = TwitterPostXTarget.valueOf(HttpProxyUtils.getXTarget(exchange));
        } catch (IllegalArgumentException ex) {
            handleInvalid(exchange);
            return;
        }
        switch (target) {
            case TweetGetRequest -> handleTweetGet(exchange);
            case TweetSampleRequest -> handleTweetSampleRequest(exchange);
            case TweetLatestOfUserRequest -> handleTweetLatestOfUserRequest(exchange);
            default -> handleInvalid(exchange);
        }
    }

    @Override
    protected void handlePut(HttpExchange exchange) throws IOException {
        TwitterPutXTarget target;
        try {
            target = TwitterPutXTarget.valueOf(HttpProxyUtils.getXTarget(exchange));
        } catch (IllegalArgumentException ex) {
            handleInvalid(exchange);
            return;
        }
        switch (target) {
            case TweetPublishRequest -> handleTweetPublish(exchange);
            case RetweetPublishRequest -> handleRetweetPublish(exchange);
            default -> handleInvalid(exchange);
        }
    }

    private void handleTweetPublish(HttpExchange exchange) throws IOException {
        TweetPut.TweetPublishRequest request = getBody(exchange, TweetPut.TweetPublishRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        TweetResponse response = controller.publishTweet(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

    private void handleRetweetPublish(HttpExchange exchange) throws IOException {
        TweetPut.RetweetPublishRequest request = getBody(exchange, TweetPut.RetweetPublishRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        TweetResponse response = controller.publishTweet(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

    private void handleTweetGet(HttpExchange exchange) throws IOException {
        TweetPost.TweetGetRequest request = getBody(exchange, TweetPost.TweetGetRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        TweetResponse response = controller.getTweet(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

    private void handleTweetSampleRequest(HttpExchange exchange) throws IOException {
        TweetPost.TweetSampleRequest request = getBody(exchange, TweetPost.TweetSampleRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        TweetResponse response = controller.getTweetSample(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

    private void handleTweetLatestOfUserRequest(HttpExchange exchange) throws IOException {
        TweetPost.TweetLatestOfUserRequest request = getBody(exchange, TweetPost.TweetLatestOfUserRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        TweetResponse response = controller.getLatestTweet(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

}
