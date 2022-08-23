package backend.controller.dataexchange;

import backend.persistence.entity.Tweet;
import backend.persistence.entity.TwitterUser;
import backend.persistence.entity.alert.Alert;
import backend.persistence.entity.alert.FollowAlert;
import backend.persistence.entity.alert.LikeAlert;
import backend.persistence.entity.alert.RetweetAlert;
import common.utils.DateUtils;
import common.api.follow.putfollow.FollowPut;
import common.api.like.putlike.PutLike;
import common.api.tweet.puttweet.TweetPut;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class AlertsExchange {

    public static class AlertExchangeException extends IOException {
        public AlertExchangeException(String failure) {
            super(failure);
        }
    }

    public static LikeAlert injectLikeAlert(
            EntityManager entityManager,
            String subjectUserId,
            PutLike.LikeAddRequest addRequest
    ) {
        TwitterUser subjUser = UserExchange.getTwitterUser(entityManager, subjectUserId);
        TwitterUser likeUser = UserExchange.getTwitterUser(entityManager, addRequest.handle);
        Tweet likedTweet = TweetExchange.getTweet(entityManager, addRequest.tweetId);
        long epochCreated = DateUtils.getEpochTimeNow();

        LikeAlert likeAlert = new LikeAlert();
        likeAlert.setTwitterUser(subjUser);
        likeAlert.setLikeUser(likeUser);
        likeAlert.setLikedTweet(likedTweet);
        likeAlert.setEpochCreated(epochCreated);
        likeAlert.setViewed(false);
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(likeAlert);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            new AlertExchangeException(ex.getMessage()).printStackTrace();
            // TODO log.
            return null;
        } finally {
            entityManager.close();
        }
        return likeAlert;
    }

    public static FollowAlert injectFollowAlert(
            EntityManager entityManager,
            FollowPut.FollowAddRequest followRequest
    ) {
        TwitterUser subjUser = UserExchange.getTwitterUser(entityManager, followRequest.followedHandler);
        TwitterUser followerUser = UserExchange.getTwitterUser(entityManager, followRequest.followingHandler);
        long epochCreated = DateUtils.getEpochTimeNow();

        FollowAlert followAlert = new FollowAlert();
        followAlert.setTwitterUser(subjUser);
        followAlert.setFollower(followerUser);
        followAlert.setEpochCreated(epochCreated);
        followAlert.setViewed(false);
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(followAlert);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            new AlertExchangeException(ex.getMessage()).printStackTrace();
            // TODO log.
            return null;
        } finally {
            entityManager.close();
        }
        return followAlert;
    }

    public static RetweetAlert injectRetweetAlert(
            EntityManager entityManager,
            String retweetingTweetId,
            TweetPut.RetweetPublishRequest retweetRequest
    ) {
        Tweet retweet = TweetExchange.getTweet(entityManager, retweetingTweetId);
        assert retweet != null;
        Tweet tweet = TweetExchange.getTweet(entityManager, retweetRequest.retweetOf);
        assert tweet != null;
        TwitterUser subjUser = UserExchange.getTwitterUser(entityManager, tweet.getAuthor().getHandle());
        TwitterUser retweetingUser = UserExchange.getTwitterUser(entityManager, retweetRequest.handle);
        long epochCreated = DateUtils.getEpochTimeNow();

        RetweetAlert retweetAlert = new RetweetAlert();
        retweetAlert.setTwitterUser(subjUser);
        retweetAlert.setRetweetingUser(retweetingUser);
        retweetAlert.setRetweetTweet(retweet);
        retweetAlert.setEpochCreated(epochCreated);
        retweetAlert.setViewed(false);
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(retweetAlert);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            new AlertExchangeException(ex.getMessage()).printStackTrace();
            // TODO log.
            return null;
        } finally {
            entityManager.close();
        }
        return retweetAlert;
    }

    public static Alert getAlert(EntityManager entityManager, String alertId) {
        return getAlert(entityManager, UUID.fromString(alertId));
    }

    public static Alert getAlert(EntityManager entityManager, UUID alertId) {
        Query q = entityManager.createQuery("SELECT a FROM Alert a where a.id=:alertId");
        q.setParameter("alertId", alertId);
        return (Alert) q.getSingleResult();
    }

    public static List<Alert> loadAlertsOf(EntityManager entityManager, String handle) {
        Query q = entityManager.createQuery("SELECT a FROM Alert a where a.twitterUser.handle=:handle");
        q.setParameter("handle", handle);
        return q.getResultList();
    }
}
