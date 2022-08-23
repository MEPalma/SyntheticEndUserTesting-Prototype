package backend.httpproxy;

import backend.controller.Controller;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import common.api.error.ResponseError;
import common.api.error.ResponseException;
import common.api.serialization.JSONObjectMapper;

import java.io.IOException;
import java.io.OutputStream;

public abstract class HttpBaseProxy implements HttpHandler {
    protected final Controller controller;

    public HttpBaseProxy(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpProxyUtils.HttpMethod method = HttpProxyUtils.getMethod(exchange);
        try {
            switch (method) {
                case GET:
                    handleGet(exchange);
                    break;
                case POST:
                    handlePost(exchange);
                    break;
                case PUT:
                    handlePut(exchange);
                    break;
                default: {
                    handleInvalid(exchange);
                    break;
                }
            }
        } catch (ResponseException re) {
            this.handleInvalid(exchange, re.getResponseError());
        }
    }

    protected void handleInvalid(HttpExchange exchange, ResponseError responseError) throws IOException {
        String response = JSONObjectMapper.JSONObjectMapper.writeValueAsString(responseError);
        respond(exchange, responseError.getCode(), response);
    }

    protected void handleInvalid(HttpExchange exchange) throws IOException {
        this.handleInvalid(exchange, new ResponseError("Invalid request."));
    }

    protected void respond(HttpExchange exchange, String response) throws IOException {
        respond(exchange, 200, response);
    }

    protected void respond(HttpExchange exchange, int code, String response) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(code, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    protected <T> T getBody(HttpExchange exchange, Class<T> valueType) throws ResponseException {
        String body = HttpProxyUtils.getBody(exchange);
        try {
            return JSONObjectMapper.JSONObjectMapper.readValue(body, valueType);
        } catch (Exception ex) {
            ResponseError err = null;
            try {
                 err = JSONObjectMapper.JSONObjectMapper.readValue(body, ResponseError.class);
            } catch (Exception ignore) {
                // Ignore.
            }
            if (err != null) {
                throw err.except();
            } else {
                // TODO log?
                ex.printStackTrace();
                throw new ResponseException(new ResponseError("Invalid request."));
            }
        }
    }

    protected abstract void handleGet(HttpExchange exchange) throws IOException;

    protected abstract void handlePost(HttpExchange exchange) throws IOException;

    protected abstract void handlePut(HttpExchange exchange) throws IOException;
}
