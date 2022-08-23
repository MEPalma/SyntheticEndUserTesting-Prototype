package backend.controller;

import backend.controller.alert.AlertDispatcher;
import backend.controller.alert.AlertRef;
import backend.controller.client.ClientSessionManager;
import backend.controller.dataexchange.*;
import backend.persistence.entity.Tweet;
import backend.persistence.entity.TwitterUser;
import backend.persistence.entity.alert.Alert;
import backend.persistence.entity.alert.FollowAlert;
import backend.persistence.entity.alert.LikeAlert;
import backend.persistence.entity.alert.RetweetAlert;
import backend.utils.Hashing;
import common.api.alert.putalert.PutAlert;
import common.api.alert.putalert.PutAlertResponse;
import common.api.error.ResponseException;
import common.api.error.UnauthorizedError;
import common.api.follow.FollowResponse;
import common.api.follow.postfollow.FollowGet;
import common.api.follow.putfollow.FollowPut;
import common.api.like.LikeResponse;
import common.api.like.postlike.PostLike;
import common.api.like.putlike.PutLike;
import common.api.tweet.TweetResponse;
import common.api.tweet.posttweet.TweetPost;
import common.api.tweet.puttweet.TweetPut;
import common.api.twitteruser.UserResponse;
import common.api.twitteruser.postuser.UserGet;
import common.api.twitteruser.postuser.UserSignOut;
import common.api.twitteruser.postuser.UserSignin;
import common.api.twitteruser.putuser.UserSignup;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static backend.persistence.db.Database.H_SESSION_FACTORY;

public final class Controller {
    private final Session session;
    private final EntityManagerFactory emf;

    private final ClientSessionManager clientSessionManager;
    private final AlertDispatcher alertDispatcher;

    public Controller(int connectionPort) throws IOException {
        this.session = H_SESSION_FACTORY.openSession();
        this.emf = this.session.getEntityManagerFactory();

        this.clientSessionManager = new ClientSessionManager(
                connectionPort,
                new ClientSessionManager.ClientSessionManagerListener() {
                    @Override
                    public void connected(String token, String handle) {
                        new Thread(() -> loadAlertsForDispatch(token, handle)).start();
                    }
                });
        new Thread(this.clientSessionManager).start();

        this.alertDispatcher = new AlertDispatcher(this.clientSessionManager);
    }

    public UserResponse signup(UserSignup.UserSignupRequest signupRequest)
            throws ResponseException {
        return UserExchange.injectUser(this.emf.createEntityManager(), signupRequest);
    }

    public UserResponse signin(UserSignin.UserSigninRequest signinRequest) throws ResponseException {
        if (UserExchange.isNotValidUserHandle(signinRequest.handle))
            throw new UserSignin.UserSigninFailed().except();

        TwitterUser maybeUser;
        {
            EntityManager em = this.emf.createEntityManager();
            maybeUser = UserExchange.getTwitterUser(em, signinRequest.handle);
            if (maybeUser == null)
                throw new UserSignin.UserSigninFailed().except();
            em.detach(maybeUser);
            em.close();
        }

        boolean isValidPasswd =
                Hashing.isPasswd(signinRequest.passwd, maybeUser.getPasswdSalt(), maybeUser.getPasswdHash());
        if (!isValidPasswd)
            throw new UserSignin.UserSigninFailed().except();

        String newToken = UUID.randomUUID().toString();

        if (this.clientSessionManager.canSignIn(signinRequest.handle)) {
            this.clientSessionManager.addLoginToken(signinRequest.handle, newToken);

            return new UserSignin.UserSigninSuccess(newToken);
        } else {
            throw new UserSignin.UserSigninFailed().except();
        }
    }

    private void loadAlertsForDispatch(String token, String handle) {
        try {
            isLoggedInToken(token, handle);
            var alerts = AlertsExchange.loadAlertsOf(this.emf.createEntityManager(), handle);
            if (alerts != null)
                for (Alert alert : alerts)
                    this.alertDispatcher.injectAlert(new AlertRef(handle, alert));
        } catch (Exception ex) {
            // TODO: log
            ex.printStackTrace();
        }
    }

    public UserResponse signOut(UserSignOut.UserSignOutRequest signOutRequest) throws ResponseException {
        if (UserExchange.isNotValidUserHandle(signOutRequest.handle))
            throw new UserSignOut.UserSignOutFailed().except();

        try {
            UUID.fromString(signOutRequest.token);
        } catch (Exception ex) {
            throw new UserSignOut.UserSignOutFailed().except();
        }

        this.clientSessionManager.removeLoginToken(signOutRequest.token);
        return new UserSignOut.UserSignOutSuccess();
    }

    private String isLoggedInToken(String token) throws ResponseException {
        String handle = this.clientSessionManager.handleOfToken(token);
        if (handle == null || !this.clientSessionManager.isLoggedInSession(handle, token))
            throw new UnauthorizedError().except();
        return handle;
    }

    private void isLoggedInToken(String token, String requiredHandle) throws ResponseException {
        String handle = isLoggedInToken(token);
        if (!handle.equals(requiredHandle))
            throw new UnauthorizedError().except();
    }

    public UserResponse getUser(String token, UserGet.UserGetRequest request) throws ResponseException {
        isLoggedInToken(token, request.reqHandle);
        EntityManager em = this.emf.createEntityManager();

        TwitterUser maybeUser = UserExchange.getTwitterUser(em, request.handle);
        if (maybeUser == null)
            throw new UserGet.UserGetResponseNoSuchUser(request.handle).except();

        FollowGet.FollowGetResponseCount count =
                (FollowGet.FollowGetResponseCount) getFollowCount(token, new FollowGet.FollowGetFollowersRequest(request.handle));

        FollowGet.FollowGetResponseHandles followers =
                (FollowGet.FollowGetResponseHandles) getFollowers(token, new FollowGet.FollowGetFollowersRequest(request.handle));

        UserGet.UserGetResponseSuccess res = new UserGet.UserGetResponseSuccess();
        res.handle = maybeUser.getHandle();
        res.base64Img = maybeUser.getBase64Img();
        res.epochCreated = maybeUser.getEpochCreated();
        res.followers = count.followers;
        res.following = count.following;
        res.follows = followers.followerHandles.contains(request.reqHandle);
        return res;
    }

    public UserResponse getAllUser(String token, UserGet.UserGetAllRequest request) throws ResponseException {
        isLoggedInToken(token, request.handle);
        EntityManager em = this.emf.createEntityManager();

        List<TwitterUser> users = UserExchange.getAllTwitterUser(em);
        if (users == null)
            throw new UserGet.UserGetResponseFailed(request.handle).except();

        List<UserGet.UserGetResponseSuccess> reses = new ArrayList<>(users.size());
        for (TwitterUser user : users) {
            FollowGet.FollowGetResponseCount count =
                    (FollowGet.FollowGetResponseCount) getFollowCount(token, new FollowGet.FollowGetFollowersRequest(user.getHandle()));
            FollowGet.FollowGetResponseHandles followers =
                    (FollowGet.FollowGetResponseHandles) getFollowers(token, new FollowGet.FollowGetFollowersRequest(user.getHandle()));
            UserGet.UserGetResponseSuccess res = new UserGet.UserGetResponseSuccess();
            res.handle = user.getHandle();
            res.base64Img = user.getBase64Img();
            res.epochCreated = user.getEpochCreated();
            res.followers = count.followers;
            res.following = count.following;
            res.follows = followers.followerHandles.contains(request.handle);
            reses.add(res);
        }
        return new UserGet.UserGetAllResponseSuccess(reses);
    }

    public TweetResponse publishTweet(String token, TweetPut.TweetPublishRequest publishRequest) throws ResponseException {
        isLoggedInToken(token, publishRequest.handle);
        return TweetExchange.injectTweet(this.emf.createEntityManager(), publishRequest);
    }

    public TweetResponse publishTweet(String token, TweetPut.RetweetPublishRequest publishRequest) throws ResponseException {
        isLoggedInToken(token, publishRequest.handle);
        TweetResponse tweetResponse = TweetExchange.injectRetweet(this.emf.createEntityManager(), publishRequest);
        if (tweetResponse instanceof TweetPut.TweetPublishResponseSuccess tweetResponseSucc) {
            try {
                RetweetAlert alert = AlertsExchange.injectRetweetAlert(this.emf.createEntityManager(), tweetResponseSucc.tweetId, publishRequest);
                this.alertDispatcher.injectAlert(new AlertRef(alert.getTwitterUser().getHandle(), alert));
            } catch (Exception ex) {
                // TODO: log
                ex.printStackTrace();
            }
        }
        return tweetResponse;
    }

    public TweetResponse getTweet(String token, TweetPost.TweetGetRequest getRequest) throws ResponseException {
        isLoggedInToken(token, getRequest.handle);
        return TweetExchange.getTweet(this.emf.createEntityManager(), getRequest);
    }

    public TweetResponse getTweetSample(String token, TweetPost.TweetSampleRequest sampleRequest) throws ResponseException {
        isLoggedInToken(token, sampleRequest.handle);
        return TweetExchange.getTweetSample(this.emf.createEntityManager(), sampleRequest);
    }

    public TweetResponse getLatestTweet(String token, TweetPost.TweetLatestOfUserRequest request) throws ResponseException {
        isLoggedInToken(token, request.handle);
        return TweetExchange.getLatestTweet(this.emf.createEntityManager(), request);
    }

    public LikeResponse injectLike(String token, PutLike.LikeAddRequest addRequest) throws ResponseException {
        isLoggedInToken(token, addRequest.handle);
        LikeResponse response = LikeExchange.injectLike(this.emf.createEntityManager(), addRequest);
        if (response instanceof PutLike.LikeResponseSuccess) {
            try {
                Tweet tweet = TweetExchange.getTweet(this.emf.createEntityManager(), addRequest.tweetId);
                if (tweet != null) {
                    LikeAlert alert = AlertsExchange.injectLikeAlert(
                            this.emf.createEntityManager(),
                            tweet.getAuthor().getHandle(),
                            addRequest
                    );
                    this.alertDispatcher.injectAlert(new AlertRef(tweet.getAuthor().getHandle(), alert));
                }
            } catch (Exception ex) {
                // TODO: log
                ex.printStackTrace();
            }
        }
        return response;
    }

    public LikeResponse removeLike(String token, PutLike.LikeRemoveRequest rmRequest) throws ResponseException {
        isLoggedInToken(token, rmRequest.handle);
        return LikeExchange.removeLike(this.emf.createEntityManager(), rmRequest);
    }

    public LikeResponse getLikes(String token, PostLike.LikeGetHandlesRequest request) throws ResponseException {
        isLoggedInToken(token);
        return LikeExchange.getLikes(this.emf.createEntityManager(), request);
    }

    public FollowResponse injectFollow(String token, FollowPut.FollowAddRequest followRequest) throws ResponseException {
        isLoggedInToken(token, followRequest.followingHandler);
        FollowResponse response = FollowExchange.injectFollow(this.emf.createEntityManager(), followRequest);
        if (response instanceof FollowPut.FollowResponseSuccess) {
            try {
                FollowAlert alert = AlertsExchange.injectFollowAlert(this.emf.createEntityManager(), followRequest);
                this.alertDispatcher.injectAlert(new AlertRef(followRequest.followedHandler, alert));
            } catch (Exception ignore) {
            }
        }
        return response;
    }

    public FollowResponse removeFollow(String token, FollowPut.FollowRemoveRequest followRequest) throws ResponseException {
        isLoggedInToken(token, followRequest.followingHandler);
        return FollowExchange.removeFollow(this.emf.createEntityManager(), followRequest);
    }

    public FollowResponse getFollowers(String token, FollowGet.FollowGetFollowersRequest followGetFollowersRequest) throws ResponseException {
        isLoggedInToken(token);
        return FollowExchange.getFollowers(this.emf.createEntityManager(), followGetFollowersRequest);
    }

    public FollowResponse getFollowing(String token, FollowGet.FollowGetFollowingRequest followGetFollowingRequest) throws ResponseException {
        isLoggedInToken(token);
        return FollowExchange.getFollowing(this.emf.createEntityManager(), followGetFollowingRequest);
    }

    public FollowResponse getFollowCount(String token, FollowGet.FollowGetFollowersRequest followGetFollowersRequest) throws ResponseException {
        isLoggedInToken(token);
        return FollowExchange.getFollowCount(this.emf.createEntityManager(), followGetFollowersRequest);
    }

    public PutAlertResponse injectAlertAsViewed(String token, PutAlert.PutAlertAsViewedRequest request) throws ResponseException {
        isLoggedInToken(token);
        EntityManager em = this.emf.createEntityManager();
        Alert alert;
        try {
            alert = AlertsExchange.getAlert(em, request.alertId);
        } catch (Exception ex) {
            throw new PutAlert.PutAlertRePutAlertResponseFailureNoSuchAlert(request.alertId).except();
        }
        em.getTransaction().begin();
        try {
            alert.setViewed(true);
            em.persist(alert);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw new PutAlert.PutAlertResponseFailed(ex.getMessage()).except();
        } finally {
            em.close();
        }
        return new PutAlert.PutAlertResponseSuccess(request.alertId);
    }
}
