package common.alert;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FollowAlert.class, name = "FollowAlert")
})
public class FollowAlert implements Alert {
    public String id;
    public String userId;
    public long epochCreated;
    public boolean viewed;

    public FollowAlert() {
    }

    public FollowAlert(String id, String userId, long epochCreated, boolean viewed) {
        this.id = id;
        this.userId = userId;
        this.epochCreated = epochCreated;
        this.viewed = viewed;
    }
}
