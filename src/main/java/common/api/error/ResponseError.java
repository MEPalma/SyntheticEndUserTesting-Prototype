package common.api.error;

public class ResponseError {
    private String type;
    private String message;
    private int code;

    public ResponseError() {
        this.type = getClass().getSimpleName();
        this.code = 400;
    }

    public ResponseError(String message) {
        this();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ResponseException except() {
        return new ResponseException(this);
    }
}
