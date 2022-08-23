package backend.httpproxy;

import backend.controller.Controller;
import com.sun.net.httpserver.HttpExchange;
import common.api.like.LikeResponse;
import common.api.like.postlike.PostLike;
import common.api.like.putlike.PutLike;

import java.io.IOException;

import static common.api.serialization.JSONObjectMapper.JSONObjectMapper;

public class HttpLikeProxy extends HttpBaseProxy {
    public enum LikePostXTarget {
        LikeGetHandlesRequest
    }

    public enum LikePutXTarget {
        LikeAddRequest,
        LikeRemoveRequest
    }

    public HttpLikeProxy(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        handleInvalid(exchange);
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        LikePostXTarget target;
        try {
            target = LikePostXTarget.valueOf(HttpProxyUtils.getXTarget(exchange));
        } catch (IllegalArgumentException ex) {
            handleInvalid(exchange);
            return;
        }
        switch (target) {
            case LikeGetHandlesRequest -> handleLikeGet(exchange);
            default -> handleInvalid(exchange);
        }
    }

    @Override
    protected void handlePut(HttpExchange exchange) throws IOException {
        LikePutXTarget target;
        try {
            target = LikePutXTarget.valueOf(HttpProxyUtils.getXTarget(exchange));
        } catch (IllegalArgumentException ex) {
            handleInvalid(exchange);
            return;
        }
        switch (target) {
            case LikeAddRequest -> handleLikeAdd(exchange);
            case LikeRemoveRequest -> handleLikeRemove(exchange);
            default -> handleInvalid(exchange);
        }
    }

    private void handleLikeAdd(HttpExchange exchange) throws IOException {
        PutLike.LikeAddRequest request = getBody(exchange, PutLike.LikeAddRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        LikeResponse response = controller.injectLike(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

    private void handleLikeRemove(HttpExchange exchange) throws IOException {
        PutLike.LikeRemoveRequest request = getBody(exchange, PutLike.LikeRemoveRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        LikeResponse response = controller.removeLike(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

    private void handleLikeGet(HttpExchange exchange) throws IOException {
        PostLike.LikeGetHandlesRequest request = getBody(exchange, PostLike.LikeGetHandlesRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        LikeResponse response = controller.getLikes(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

}
