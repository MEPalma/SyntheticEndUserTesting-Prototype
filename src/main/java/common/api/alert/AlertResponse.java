package common.api.alert;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import common.api.alert.putalert.PutAlertResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AlertResponse.class, name = "AlertResponse"),
        @JsonSubTypes.Type(value = PutAlertResponse.class, name = "PutAlertResponse"),
})
public interface AlertResponse {
}
