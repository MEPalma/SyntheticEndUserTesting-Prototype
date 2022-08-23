package common.api.alert.putalert;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.error.ResponseError;

public class PutAlert {

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = PutAlert.PutAlertAsViewedRequest.class, name = "PutAlertAsViewedRequest")
    })
    public static class PutAlertAsViewedRequest implements PutAlertRequest {
        public String alertId;

        public PutAlertAsViewedRequest() {

        }

        public PutAlertAsViewedRequest(String alertId) {
            this.alertId = alertId;
        }
    }

    public static class PutAlertResponseSuccess implements PutAlertResponse {
        public String alertId;

        public PutAlertResponseSuccess() {
        }

        public PutAlertResponseSuccess(String alertId) {
            this.alertId = alertId;
        }
    }

    public abstract static class PutAlertResponseFailure extends ResponseError {
        public PutAlertResponseFailure() {
        }

        public PutAlertResponseFailure(String failure) {
            super(failure);
        }
    }

    public static class PutAlertResponseFailed extends PutAlertResponseFailure {
        public PutAlertResponseFailed() {
        }

        public PutAlertResponseFailed(String failure) {
            super(failure);
        }
    }

    public static class PutAlertRePutAlertResponseFailureNoSuchAlert extends PutAlertResponseFailure {
        public PutAlertRePutAlertResponseFailureNoSuchAlert() {
        }

        public PutAlertRePutAlertResponseFailureNoSuchAlert(String alertId) {
            super("No such alert with id " + alertId);
        }
    }

}


