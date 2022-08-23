package backend.httpproxy;

import backend.controller.Controller;
import com.sun.net.httpserver.HttpExchange;
import static common.api.serialization.JSONObjectMapper.JSONObjectMapper;

import common.api.twitteruser.UserResponse;
import common.api.twitteruser.postuser.UserGet;
import common.api.twitteruser.postuser.UserSignOut;
import common.api.twitteruser.postuser.UserSignin;
import common.api.twitteruser.putuser.UserSignup;

import java.io.IOException;

public class HttpUserProxy extends HttpBaseProxy {
    public enum UserPutXTarget {
        UserSignupRequest
    }

    public enum UserPostXTarget {
        UserSigninRequest,
        UserSignOutRequest,
        UserGetRequest,
        UserGetAllRequest
    }

    public HttpUserProxy(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        this.handleInvalid(exchange);
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        UserPostXTarget target;
        try {
            target = UserPostXTarget.valueOf(HttpProxyUtils.getXTarget(exchange));
        } catch (IllegalArgumentException ex) {
            handleInvalid(exchange);
            return;
        }
        switch (target) {
            case UserSigninRequest -> handleUserSignin(exchange);
            case UserSignOutRequest -> handleUserSignOut(exchange);
            case UserGetRequest -> handleUserGet(exchange);
            case UserGetAllRequest -> handleUserGetAll(exchange);
            default -> handleInvalid(exchange);
        }
    }

    @Override
    protected void handlePut(HttpExchange exchange) throws IOException {
        UserPutXTarget target;
        try {
            target = UserPutXTarget.valueOf(HttpProxyUtils.getXTarget(exchange));
        } catch (IllegalArgumentException ex) {
            handleInvalid(exchange);
            return;
        }
        switch (target) {
            case UserSignupRequest -> handleUserSignup(exchange);
            default -> handleInvalid(exchange);
        }
    }

    private void handleUserSignup(HttpExchange exchange) throws IOException {
        UserSignup.UserSignupRequest request = getBody(exchange, UserSignup.UserSignupRequest.class);
        UserResponse response = controller.signup(request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

    private void handleUserSignin(HttpExchange exchange) throws IOException {
        UserSignin.UserSigninRequest request = getBody(exchange, UserSignin.UserSigninRequest.class);
        UserResponse response = controller.signin(request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

    private void handleUserSignOut(HttpExchange exchange) throws IOException {
        UserSignOut.UserSignOutRequest request = getBody(exchange, UserSignOut.UserSignOutRequest.class);
        UserResponse response = controller.signOut(request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

    private void handleUserGet(HttpExchange exchange) throws IOException {
        UserGet.UserGetRequest request = getBody(exchange, UserGet.UserGetRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        UserResponse response = controller.getUser(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

    private void handleUserGetAll(HttpExchange exchange) throws IOException {
        UserGet.UserGetAllRequest request = getBody(exchange, UserGet.UserGetAllRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        UserResponse response = controller.getAllUser(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

}
