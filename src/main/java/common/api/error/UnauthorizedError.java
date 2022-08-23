package common.api.error;

public class UnauthorizedError extends ResponseError {
    public UnauthorizedError() {
        super("Unauthorized");
    }
}
