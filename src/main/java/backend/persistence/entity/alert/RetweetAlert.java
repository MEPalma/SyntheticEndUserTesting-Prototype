package backend.persistence.entity.alert;

import backend.persistence.entity.Tweet;
import backend.persistence.entity.TwitterUser;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class RetweetAlert extends Alert {

    @OneToOne
    private TwitterUser retweetingUser;

    @OneToOne
    private Tweet retweetTweet;

    public RetweetAlert() {
    }

    public RetweetAlert(TwitterUser retweetingUser, Tweet retweetTweet) {
        this.retweetingUser = retweetingUser;
        this.retweetTweet = retweetTweet;
    }

    public TwitterUser getRetweetingUser() {
        return retweetingUser;
    }

    public void setRetweetingUser(TwitterUser retweetingUser) {
        this.retweetingUser = retweetingUser;
    }

    public Tweet getRetweetTweet() {
        return retweetTweet;
    }

    public void setRetweetTweet(Tweet retweetTweet) {
        this.retweetTweet = retweetTweet;
    }

    public common.alert.RetweetAlert toApiAlert() {
        return new common.alert.RetweetAlert(
                getId().toString(),
                retweetingUser.getHandle(),
                retweetTweet.getId().toString(),
                getEpochCreated(),
                isViewed()
        );
    }
}
