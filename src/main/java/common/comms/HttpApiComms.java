package common.comms;

import backend.Backend;
import backend.httpproxy.HttpProxyUtils;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpApiComms {
    public static final String API_ENDPOINT;
    static {
        // TODO: load from env?
        API_ENDPOINT = "localhost";
    }

    public static final int BACKEND_CONN_ENDPOINT_PORT = 9587;
    public static final int BACKEND_API_ENDPOINT_PORT = 9588;

    public enum HttpMethod {
        GET,
        POST,
        PUT
    }

    public static String getUri(String context) {
        return "http://" + API_ENDPOINT + ":" + BACKEND_API_ENDPOINT_PORT + context;
    }

    public static String post(String context, String target, String token, String request) throws IOException {
        return send(new HttpPost(getUri(context)), target, token, request);
    }

    public static String put(String context, String target, String token, String request) throws IOException {
        return send(new HttpPut(getUri(context)), target, token, request);
    }

    public static String send(
            HttpEntityEnclosingRequestBase r,
            String target,
            String token,
            String request
    ) throws IOException {
        var c = HttpClientBuilder.create().build();
        r.setHeader("Content-Type", "application/json");
        r.setHeader(HttpProxyUtils.XTarget, target);
        if (token != null)
            r.setHeader(HttpProxyUtils.AuthorizationHeader, token);
        r.setEntity(new ByteArrayEntity(request.getBytes(StandardCharsets.UTF_8)));
        var httpSignupRes = c.execute(r);
        return EntityUtils.toString(httpSignupRes.getEntity());
    }

}
