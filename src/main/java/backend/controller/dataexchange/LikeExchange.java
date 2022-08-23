package backend.controller.dataexchange;

import common.api.error.ResponseException;
import backend.persistence.entity.Tweet;
import backend.persistence.entity.TweetLike;
import backend.persistence.entity.TweetLikeKey;
import backend.persistence.entity.TwitterUser;
import common.api.like.LikeResponse;
import common.api.like.postlike.PostLikeResponse;
import common.api.like.putlike.PutLike;
import common.api.like.postlike.PostLike;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LikeExchange {

    private static TweetLikeKey getTweetLikeKey(
            EntityManager entityManager,
            String handle,
            String tweetId
    ) throws ResponseException {
        TwitterUser twitterUser = UserExchange.getTwitterUser(entityManager, handle);
        if (twitterUser == null)
            throw new PutLike.LikeResponseNoSuchUser(handle).except();

        UUID tweetUUID;
        try {
            tweetUUID = UUID.fromString(tweetId);
        } catch (IllegalArgumentException ex) {
            throw new PutLike.LikeResponseNoSuchTweet(tweetId).except();
        }

        Tweet tweet = TweetExchange.getTweet(entityManager, tweetUUID);
        if (tweet == null)
            throw new PutLike.LikeResponseNoSuchTweet(tweetId).except();

        return new TweetLikeKey(tweet, twitterUser);
    }

    public static LikeResponse injectLike(
            EntityManager entityManager,
            PutLike.LikeAddRequest addRequest
    ) throws ResponseException {
        TweetLikeKey tweetLikeKey = LikeExchange.getTweetLikeKey(entityManager, addRequest.handle, addRequest.tweetId);
        try {
            TweetLike tweetLike = new TweetLike();
            tweetLike.setTweetLikeKey(tweetLikeKey);

            entityManager.getTransaction().begin();
            entityManager.persist(tweetLike);
            entityManager.getTransaction().commit();
            entityManager.close();
        } catch (Exception ex) {
            throw new PutLike.LikeResponseFailed(ex.getMessage()).except();
        } finally {
            entityManager.close();
        }
        return new PutLike.LikeResponseSuccess();
    }

    public static LikeResponse removeLike(
            EntityManager entityManager,
            PutLike.LikeRemoveRequest rmRequest
    ) throws ResponseException {
        TweetLikeKey tweetLikeKey = LikeExchange.getTweetLikeKey(entityManager, rmRequest.handle, rmRequest.tweetId);
        try {
            TweetLike tweetLike = new TweetLike();
            tweetLike.setTweetLikeKey(tweetLikeKey);

            entityManager.getTransaction().begin();
            entityManager.remove(tweetLike);
            entityManager.getTransaction().commit();
            entityManager.close();
        } catch (Exception ex) {
            throw new PutLike.LikeResponseFailed(ex.getMessage()).except();
        } finally {
            entityManager.close();
        }
        return new PutLike.LikeResponseSuccess();
    }

    public static LikeResponse getLikes(
            EntityManager entityManager,
            PostLike.LikeGetHandlesRequest request
    ) throws ResponseException {
        try {
            entityManager.getTransaction().begin();
            entityManager.getTransaction().setRollbackOnly();
            List<String> likes = LikeExchange.getLikes(entityManager, request.tweetId);
            return new PostLike.LikeGetResponseHandles(likes);
        } catch (Exception ex) {
            throw new PostLike.LikeGetResponseFailed(ex.getMessage()).except();
        } finally {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    public static List<String> getLikes(EntityManager entityManager, String tweetId) throws ResponseException {
        try {
            UUID tweetuuId = UUID.fromString(tweetId);
            return getLikes(entityManager, tweetuuId);
        } catch (Exception ex) {
            throw new PostLike.LikeGetResponseFailed(ex.getMessage()).except();
        }
    }

    public static List<String> getLikes(EntityManager entityManager, UUID tweetId) throws ResponseException {
        try {
            return TweetLike.sqlGetLikeHandlesOfTweet(entityManager, tweetId).getResultList();
        } catch (Exception ex) {
            throw new PostLike.LikeGetResponseFailed(ex.getMessage()).except();
        }
    }

}
