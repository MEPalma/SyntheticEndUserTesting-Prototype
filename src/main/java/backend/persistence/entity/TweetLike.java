package backend.persistence.entity;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TweetLike {
    @EmbeddedId
    private TweetLikeKey tweetLikeKey;

    @Column(nullable = false)
    private int epochCreated;

    public TweetLike() {

    }

    public TweetLike(TweetLikeKey tweetLikeKey, int epochCreated) {
        this.tweetLikeKey = tweetLikeKey;
        this.epochCreated = epochCreated;
    }

    public TweetLikeKey getTweetLikeKey() {
        return tweetLikeKey;
    }

    public void setTweetLikeKey(TweetLikeKey tweetLikeKey) {
        this.tweetLikeKey = tweetLikeKey;
    }

    public int getEpochCreated() {
        return epochCreated;
    }

    public void setEpochCreated(int epochCreated) {
        this.epochCreated = epochCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TweetLike tweetLike = (TweetLike) o;
        return hashCode() == tweetLike.hashCode();
    }

    @Override
    public int hashCode() {
        return (tweetLikeKey.getTweet().getId().toString() + tweetLikeKey.getTwitterUser().getHandle()).hashCode();
    }

    public static Query sqlGetLikeHandlesOfTweet(EntityManager entityManager, UUID tweetId) {
        final String template = """
                    SELECT tl.tweetLikeKey.twitterUser.handle
                    FROM TweetLike tl
                    WHERE tl.tweetLikeKey.tweet.id=:tweetId
                """;
        Query query = entityManager.createQuery(template);
        query.setParameter("tweetId", tweetId);
        return query;
    }

    public static Query sqlGetLikesCountOfTweet(EntityManager entityManager, String tweetId) {
        final String template = """
                    SELECT COUNT(tl.tweetLikeKey.twitterUser.handle)
                    FROM TweetLike tl
                    WHERE tl.tweetLikeKey.tweet.id=:tweetId
                """;
        Query query = entityManager.createQuery(template);
        query.setParameter("tweetId", tweetId);
        return query;
    }

}
