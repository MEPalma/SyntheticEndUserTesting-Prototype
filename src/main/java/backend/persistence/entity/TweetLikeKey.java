package backend.persistence.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Embeddable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TweetLikeKey implements Serializable {
    private static final long serialVersionUID = 1L;

    @OneToOne
    private Tweet tweet;

    @OneToOne
    private TwitterUser twitterUser;

    public TweetLikeKey() {

    }

    public TweetLikeKey(Tweet tweet, TwitterUser twitterUser) {
        this.tweet = tweet;
        this.twitterUser = twitterUser;
    }

    public Tweet getTweet() {
        return tweet;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }

    public TwitterUser getTwitterUser() {
        return twitterUser;
    }

    public void setTwitterUser(TwitterUser twitterUser) {
        this.twitterUser = twitterUser;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TweetLikeKey that = (TweetLikeKey) o;
        return hashCode() == that.hashCode();
    }

    @Override
    public int hashCode() {
        return (tweet.getId() + twitterUser.getHandle()).hashCode();
    }
}
