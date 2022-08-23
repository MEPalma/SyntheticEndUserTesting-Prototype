package common.alert;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LikeAlert.class, name = "LikeAlert")
})
public class LikeAlert implements Alert {
    public String id;
    public String tweetId;
    public String userId;
    public long epochCreated;
    public boolean viewed;

    public LikeAlert() {
    }

    public LikeAlert(String id, String tweetId, String userId, long epochCreated, boolean viewed) {
        this.id = id;
        this.tweetId = tweetId;
        this.userId = userId;
        this.epochCreated = epochCreated;
        this.viewed = viewed;
    }
}
