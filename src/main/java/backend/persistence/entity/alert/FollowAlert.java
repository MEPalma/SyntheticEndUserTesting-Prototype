package backend.persistence.entity.alert;

import backend.persistence.entity.TwitterUser;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class FollowAlert extends Alert {
    @OneToOne
    private TwitterUser follower;

    public FollowAlert() {
    }

    public FollowAlert(TwitterUser follower) {
        this.follower = follower;
    }

    public TwitterUser getFollower() {
        return follower;
    }

    public void setFollower(TwitterUser follower) {
        this.follower = follower;
    }

    public common.alert.FollowAlert toApiAlert() {
        return new common.alert.FollowAlert(getId().toString(), follower.getHandle(), getEpochCreated(), isViewed());
    }
}
