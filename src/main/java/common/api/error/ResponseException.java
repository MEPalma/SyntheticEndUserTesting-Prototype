package common.api.error;

import java.io.IOException;

public class ResponseException extends IOException {
    private final ResponseError responseError;

    public ResponseException(ResponseError responseError) {
        this.responseError = responseError;
    }

    public ResponseError getResponseError() {
        return responseError;
    }
}
