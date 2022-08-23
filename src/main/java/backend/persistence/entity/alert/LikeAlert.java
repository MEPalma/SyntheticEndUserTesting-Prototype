package backend.persistence.entity.alert;

import backend.persistence.entity.Tweet;
import backend.persistence.entity.TwitterUser;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class LikeAlert extends Alert {
    @OneToOne
    private TwitterUser likeUser;

    @OneToOne
    private Tweet likedTweet;

    public LikeAlert() {
    }

    public LikeAlert(TwitterUser likeUser, Tweet likedTweet) {
        this.likeUser = likeUser;
        this.likedTweet = likedTweet;
    }

    public TwitterUser getLikeUser() {
        return likeUser;
    }

    public void setLikeUser(TwitterUser likeUser) {
        this.likeUser = likeUser;
    }

    public Tweet getLikedTweet() {
        return likedTweet;
    }

    public void setLikedTweet(Tweet likedTweet) {
        this.likedTweet = likedTweet;
    }

    public common.alert.LikeAlert toApiAlert() {
        return new common.alert.LikeAlert(
                getId().toString(),
                likedTweet.getId().toString(),
                likeUser.getHandle(),
                getEpochCreated(),
                isViewed()
        );
    }
}
