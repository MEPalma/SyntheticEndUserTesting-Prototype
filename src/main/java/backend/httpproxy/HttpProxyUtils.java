package backend.httpproxy;

import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;

public class HttpProxyUtils {
    public static final String AuthorizationHeader = "Authorization";
    public static final String XTarget = "x-tw-target";

    public enum HttpMethod {
        GET, POST, PUT, UNKNOWN;
    }

    public static String getAuthorizationToken(HttpExchange exchange) {
        return exchange.getRequestHeaders().getFirst(AuthorizationHeader);
    }

    public static String getXTarget(HttpExchange exchange) {
        return exchange.getRequestHeaders().getFirst(XTarget);
    }

    public static HttpMethod getMethod(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        try {
            return HttpMethod.valueOf(method.toUpperCase());
        } catch (Exception ex) {
            return HttpMethod.UNKNOWN;
        }
    }

    public static String getBody(HttpExchange exchange) {
        try {
            return IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }

}
