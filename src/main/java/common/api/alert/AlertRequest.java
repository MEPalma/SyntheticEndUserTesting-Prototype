package common.api.alert;

import common.comms.request.PutRequest;

public interface AlertRequest extends PutRequest {
    public static final String CONTEXT = "/alert";
}
