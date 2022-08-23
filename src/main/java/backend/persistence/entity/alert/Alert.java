package backend.persistence.entity.alert;

import backend.persistence.entity.TwitterUser;

import javax.persistence.*;
import java.util.UUID;

@Entity
public abstract class Alert {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    private TwitterUser twitterUser;

    @Column(nullable = false)
    private long epochCreated;

    @Column(nullable = false)
    private boolean viewed;

    public UUID getId() {
        return id;
    }

    public TwitterUser getTwitterUser() {
        return twitterUser;
    }

    public void setTwitterUser(TwitterUser twitterUser) {
        this.twitterUser = twitterUser;
    }

    public long getEpochCreated() {
        return epochCreated;
    }

    public void setEpochCreated(long epochCreated) {
        this.epochCreated = epochCreated;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public abstract common.alert.Alert toApiAlert();
}
