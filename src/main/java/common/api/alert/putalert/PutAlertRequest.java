package common.api.alert.putalert;

import common.api.alert.AlertRequest;
import common.comms.request.PutRequest;

public interface PutAlertRequest extends AlertRequest, PutRequest {
    default String getContext() {
        return AlertRequest.CONTEXT;
    }
}
