package backend.httpproxy;

import backend.controller.Controller;
import com.sun.net.httpserver.HttpExchange;
import common.api.alert.putalert.PutAlert;
import common.api.alert.putalert.PutAlertResponse;

import java.io.IOException;

import static common.api.serialization.JSONObjectMapper.JSONObjectMapper;

public class HttpAlertsProxy extends HttpBaseProxy {
    public enum AlertsPutXTarget {
        PutAlertAsViewedRequest
    }

    public HttpAlertsProxy(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        handleInvalid(exchange);
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        handleInvalid(exchange);
    }

    @Override
    protected void handlePut(HttpExchange exchange) throws IOException {
        AlertsPutXTarget target;
        try {
            target = AlertsPutXTarget.valueOf(HttpProxyUtils.getXTarget(exchange));
        } catch (IllegalArgumentException ex) {
            handleInvalid(exchange);
            return;
        }
        switch (target) {
            case PutAlertAsViewedRequest -> handlePutAlertAsViewedRequest(exchange);
            default -> handleInvalid(exchange);
        }
    }

    private void handlePutAlertAsViewedRequest(HttpExchange exchange) throws IOException {
        PutAlert.PutAlertAsViewedRequest request = getBody(exchange, PutAlert.PutAlertAsViewedRequest.class);
        String loginToken = HttpProxyUtils.getAuthorizationToken(exchange);
        PutAlertResponse response = controller.injectAlertAsViewed(loginToken, request);
        String strResponse = JSONObjectMapper.writeValueAsString(response);
        respond(exchange, strResponse);
    }

}
