package backend.persistence.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Embeddable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FollowKey implements Serializable {
    private static final long serialVersionUID = 1L;
    @OneToOne
    private TwitterUser followingTwitterUser;
    @OneToOne
    private TwitterUser followedTwitterUser;

    public FollowKey() {

    }

    public FollowKey(TwitterUser followingTwitterUser, TwitterUser followedTwitterUser) {
        this.followingTwitterUser = followingTwitterUser;
        this.followedTwitterUser = followedTwitterUser;
    }

    public TwitterUser getFollowingTwitterUser() {
        return followingTwitterUser;
    }

    public void setFollowingTwitterUser(TwitterUser followingTwitterUser) {
        this.followingTwitterUser = followingTwitterUser;
    }

    public TwitterUser getFollowedTwitterUser() {
        return followedTwitterUser;
    }

    public void setFollowedTwitterUser(TwitterUser followedTwitterUser) {
        this.followedTwitterUser = followedTwitterUser;
    }
}
