package backend.controller.dataexchange;

import backend.persistence.entity.Tweet;
import backend.persistence.entity.TwitterUser;
import common.utils.DateUtils;
import common.api.error.ResponseException;
import common.api.tweet.TweetResponse;
import common.api.tweet.posttweet.TweetPost;
import common.api.tweet.puttweet.TweetPut;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.security.InvalidParameterException;
import java.util.*;

public class TweetExchange {

    private static boolean isEmptyTweet(TweetPut.TweetPublishRequest publishRequest) {
        boolean hasTxt = publishRequest.txt != null && !publishRequest.txt.isBlank();
        boolean hasImg = publishRequest.base64Img != null && !publishRequest.base64Img.isBlank();
        return !(hasTxt || hasImg);
    }

    public static TweetResponse injectTweet(
            EntityManager entityManager,
            TweetPut.TweetPublishRequest publishRequest
    ) throws ResponseException {
        if (publishRequest == null || isEmptyTweet(publishRequest))
            throw new TweetPut.TweetPublishResponseEmpty().except();

        TwitterUser author = UserExchange.getTwitterUser(entityManager, publishRequest.handle);
        if (author == null)
            throw new TweetPut.TweetPublishResponseNoSuchAuthor(publishRequest.handle).except();

        Tweet tweet;
        try {
            tweet = new Tweet();
            tweet.setEpochCreated(DateUtils.getEpochTimeNow());
            tweet.setAuthor(author);
            tweet.setText(publishRequest.txt);
            tweet.setBase64Img(publishRequest.base64Img);
            tweet.setRetweetOf(null);
            //
            entityManager.getTransaction().begin();
            entityManager.persist(tweet);
            entityManager.getTransaction().commit();
            entityManager.close();
        } catch (Exception ex) {
            throw new TweetPut.TweetPublishResponseFailed(ex.getMessage()).except();
        } finally {
            entityManager.close();
        }

        return new TweetPut.TweetPublishResponseSuccess(tweet.getId().toString());
    }

    public static TweetResponse injectRetweet(
            EntityManager entityManager,
            TweetPut.RetweetPublishRequest publishRequest
    ) throws ResponseException {
        if (publishRequest == null || publishRequest.retweetOf == null)
            throw new TweetPut.TweetPublishResponseEmpty().except();

        UUID tweetUUID;
        try {
            tweetUUID = UUID.fromString(publishRequest.retweetOf);
        } catch (InvalidParameterException ipe) {
            throw new TweetPut.TweetPublishResponseInvalidRetweetReference(publishRequest.retweetOf).except();
        }

        Tweet maybeTweet = getTweet(entityManager, tweetUUID);
        if (maybeTweet == null)
            throw new TweetPut.TweetPublishResponseInvalidRetweetReference(publishRequest.retweetOf).except();

        TwitterUser author = UserExchange.getTwitterUser(entityManager, publishRequest.handle);
        if (author == null)
            throw new TweetPut.TweetPublishResponseNoSuchAuthor(publishRequest.handle).except();

        Tweet retweet;
        try {
            retweet = new Tweet();
            retweet.setAuthor(author);
            retweet.setEpochCreated(DateUtils.getEpochTimeNow());
            retweet.setText(publishRequest.txt);
            retweet.setBase64Img(publishRequest.base64Img);
            retweet.setRetweetOf(maybeTweet);
            //
            entityManager.getTransaction().begin();
            entityManager.persist(retweet);
            entityManager.getTransaction().commit();
            entityManager.close();
        } catch (Exception ex) {
            throw new TweetPut.TweetPublishResponseFailed(ex.getMessage()).except();
        } finally {
            entityManager.close();
        }
        return new TweetPut.TweetPublishResponseSuccess(retweet.getId().toString());
    }

    public static Tweet getTweet(EntityManager entityManager, UUID uuid) {
        return entityManager.find(Tweet.class, uuid);
    }

    public static Tweet getTweet(EntityManager entityManager, String strId) {
        UUID uuid;
        try {
            uuid = UUID.fromString(strId);
            return getTweet(entityManager, uuid);
        } catch (Exception ex) {
            return null;
        }
    }

    public static TweetResponse getTweet(
            EntityManager entityManager,
            TweetPost.TweetGetRequest request
    ) {
        Set<String> tweetIds = new HashSet<>(request.tweetIds);
        List<TweetPost.TweetGetResponseItem> items = new ArrayList<>(tweetIds.size());
        for (String tweetId : tweetIds) {
            TweetPost.TweetGetResponseItem item = new TweetPost.TweetGetResponseItem();
            item.tweetId = tweetId;
            try {
                Tweet tweet = getTweet(entityManager, UUID.fromString(tweetId));
                item.authorHandle = tweet.getAuthor().getHandle();
                item.text = tweet.getText();
                item.base64Img = tweet.getBase64Img();
                item.epochCreated = tweet.getEpochCreated();
                if (tweet.getRetweetOf() != null)
                    item.retweetOf = tweet.getRetweetOf().getId().toString();
                entityManager.detach(tweet);
                List<String> likeHandles = LikeExchange.getLikes(entityManager, tweetId);
                item.likes = likeHandles.size();
                item.liked = likeHandles.contains(request.handle);
            } catch (Exception ex) {
                // Ignore, this tweet will not be populated.
            }
            items.add(item);
        }
        items.sort(Comparator.comparingLong(o -> o.epochCreated));
        return new TweetPost.TweetGetResponseSuccess(items);
    }

    public static TweetResponse getLatestTweet(
            EntityManager entityManager,
            TweetPost.TweetLatestOfUserRequest request
    ) throws ResponseException {
        try {
            entityManager.getTransaction().setRollbackOnly();
            entityManager.getTransaction().begin();
            List<Tweet> tweets =
                    Tweet.sqlGetTweetsOfUser(
                            entityManager,
                            request.handle,
                            request.minEpochCreated
                    ).getResultList();
            List<TweetPost.TweetGetResponseItem> responseItems = new ArrayList<>(tweets.size());
            for (Tweet tweet : tweets) {
                var item = new TweetPost.TweetGetResponseItem();
                item.tweetId = tweet.getId().toString();
                item.authorHandle = tweet.getAuthor().getHandle();
                item.text = tweet.getText();
                item.base64Img = tweet.getBase64Img();
                item.epochCreated = tweet.getEpochCreated();
                if (tweet.getRetweetOf() != null)
                    item.retweetOf = tweet.getRetweetOf().getId().toString();
                List<String> likeHandles = LikeExchange.getLikes(entityManager, tweet.getId());
                item.likes = likeHandles.size();
                item.liked = likeHandles.contains(request.handle);
                responseItems.add(item);
            }
            responseItems.sort(Comparator.comparingLong(o -> o.epochCreated));
            return new TweetPost.TweetGetResponseSuccess(responseItems);
        } catch (PersistenceException pe) {
            // TODO log.
            pe.printStackTrace();
            throw new TweetPost.TweetGetResponseFailed(pe.getMessage()).except();
        } catch (Exception ex) {
            throw new TweetPost.TweetGetResponseFailed(ex.getMessage()).except();
        } finally {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

    public static TweetResponse getTweetSample(
            EntityManager entityManager,
            TweetPost.TweetSampleRequest request
    ) throws ResponseException {
        try {
            entityManager.getTransaction().setRollbackOnly();
            Set<Tweet> tweets = new HashSet<>();
            tweets.addAll(
                    Tweet.sqlGetTweetsOfUser(
                            entityManager,
                            request.handle,
                            request.minEpochCreated
                    ).getResultList()
            );
            tweets.addAll(
                    Tweet.sqlSampleRetweetsToUser(
                            entityManager,
                            request.handle,
                            request.minEpochCreated
                    ).getResultList()
            );
            tweets.addAll(
                    Tweet.sqlSampleTweetsOfFollowing(
                            entityManager,
                            request.handle,
                            request.minEpochCreated
                    ).getResultList()
            );
            List<TweetPost.TweetGetResponseItem> responseItems = new ArrayList<>(tweets.size());
            for (Tweet tweet : tweets) {
                var item = new TweetPost.TweetGetResponseItem();
                item.tweetId = tweet.getId().toString();
                item.authorHandle = tweet.getAuthor().getHandle();
                item.text = tweet.getText();
                item.base64Img = tweet.getBase64Img();
                item.epochCreated = tweet.getEpochCreated();
                if (tweet.getRetweetOf() != null)
                    item.retweetOf = tweet.getRetweetOf().getId().toString();
                List<String> likeHandles = LikeExchange.getLikes(entityManager, tweet.getId());
                item.likes = likeHandles.size();
                item.liked = likeHandles.contains(request.handle);
                responseItems.add(item);
            }
            responseItems.sort(Comparator.comparingLong(o -> o.epochCreated));
            return new TweetPost.TweetGetResponseSuccess(responseItems);
        } catch (PersistenceException pe) {
            // TODO log.
            pe.printStackTrace();
            throw new TweetPost.TweetGetResponseFailed(pe.getMessage()).except();
        } catch (Exception ex) {
            throw new TweetPost.TweetGetResponseFailed(ex.getMessage()).except();
        } finally {
            entityManager.getTransaction().rollback();
            entityManager.close();
        }
    }

}
