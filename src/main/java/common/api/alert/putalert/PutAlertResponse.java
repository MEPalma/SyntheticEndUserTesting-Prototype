package common.api.alert.putalert;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.alert.AlertResponse;
import common.comms.response.PutResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PutAlertResponse.class, name = "PutAlertResponse"),
        @JsonSubTypes.Type(value = PutAlert.PutAlertResponseSuccess.class, name = "PutAlertResponseSuccess"),
        @JsonSubTypes.Type(value = PutAlert.PutAlertResponseFailure.class, name = "PutAlertResponseFailure"),
        @JsonSubTypes.Type(value = PutAlert.PutAlertRePutAlertResponseFailureNoSuchAlert.class, name = "PutAlertRePutAlertResponseFailureNoSuchAlert"),
        @JsonSubTypes.Type(value = PutAlert.PutAlertResponseFailed.class, name = "PutAlertResponseFailed"),
})
public interface PutAlertResponse extends PutResponse, AlertResponse {
}
