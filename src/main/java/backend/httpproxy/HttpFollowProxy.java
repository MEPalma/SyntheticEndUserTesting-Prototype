package backend.httpproxy;

import backend.controller.Controller;
import com.sun.net.httpserver.HttpExchange;
import common.api.follow.FollowResponse;
import common.api.follow.putfollow.FollowPut;
import common.api.follow.postfollow.FollowGet;

import java.io.IOException;

import static common.api.serialization.JSONObjectMapper.JSONObjectMapper;

public class HttpFollowProxy extends HttpBaseProxy {
    public enum FollowPutXTarget {
        FollowAddRequest,
        FollowRemoveRequest;
    }

    public enum FollowPostXTarget {
        FollowGetFollowersRequest,
        FollowGetFollowingRequest,
        FollowGetCountRequest
    }

    public HttpFollowProxy(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        handleInvalid(exchange);
    }

    @Override
    protected void handlePut(HttpExchange exchange) throws IOException {
        FollowPutXTarget target;
        try {
            target = FollowPutXTarget.valueOf(HttpProxyUtils.getXTarget(exchange));
        } catch (IllegalArgumentException ex) {
            handleInvalid(exchange);
            return;
        }
        switch (target) {
            case FollowAddRequest -> handleFollowAdd(exchange);
            case FollowRemoveRequest -> handleFollowRemove(exchange);
            default -> handleInvalid(exchange);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        FollowPostXTarget target;
        try {
            target = FollowPostXTarget.valueOf(HttpProxyUtils.getXTarget(exchange));
        } catch (IllegalArgumentException ex) {
            handleInvalid(exchange);
            return;
        }
        switch (target) {
            case FollowGetFollowersRequest -> handleFollowGetFollowers(exchange);
            case FollowGetFollowingRequest -> handleFollowGetFollowing(exchange);
            case FollowGetCountRequest -> handleFollowGetCount(exchange);
            default -> handleInvalid(exchange);
        }
    }

    private void handleFollowAdd(HttpExchange exchange) throws IOException {
        FollowPut.FollowAddRequest request = getBody(exchange, FollowPut.FollowAddRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        FollowResponse response = controller.injectFollow(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

    private void handleFollowRemove(HttpExchange exchange) throws IOException {
        FollowPut.FollowRemoveRequest request = getBody(exchange, FollowPut.FollowRemoveRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        FollowResponse response = controller.removeFollow(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

    private void handleFollowGetFollowers(HttpExchange exchange) throws IOException {
        FollowGet.FollowGetFollowersRequest request = getBody(exchange, FollowGet.FollowGetFollowersRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        FollowResponse response = controller.getFollowers(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

    private void handleFollowGetFollowing(HttpExchange exchange) throws IOException {
        FollowGet.FollowGetFollowingRequest request = getBody(exchange, FollowGet.FollowGetFollowingRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        FollowResponse response = controller.getFollowing(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

    private void handleFollowGetCount(HttpExchange exchange) throws IOException {
        FollowGet.FollowGetFollowersRequest request = getBody(exchange, FollowGet.FollowGetFollowersRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        FollowResponse response = controller.getFollowCount(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

}
