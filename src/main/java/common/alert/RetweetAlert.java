package common.alert;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RetweetAlert.class, name = "RetweetAlert")
})
public class RetweetAlert implements Alert {
    public String id;
    public String retweetingUserId;
    public String retweetTweetId;
    public long epochCreated;
    public boolean viewed;

    public RetweetAlert() {

    }

    public RetweetAlert(String id, String retweetingUserId, String retweetTweetId, long epochCreated, boolean viewed) {
        this.id = id;
        this.retweetingUserId = retweetingUserId;
        this.retweetTweetId = retweetTweetId;
        this.epochCreated = epochCreated;
        this.viewed = viewed;
    }
}
