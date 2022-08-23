package gui.backend.controller;

import common.alert.Alert;
import common.alert.FollowAlert;
import common.alert.LikeAlert;
import common.alert.RetweetAlert;
import common.api.alert.putalert.PutAlert;
import common.api.error.ResponseError;
import common.api.error.ResponseException;
import common.api.follow.putfollow.FollowPut;
import common.api.like.postlike.PostLike;
import common.api.like.putlike.PutLike;
import common.api.tweet.posttweet.TweetPost;
import common.api.tweet.puttweet.TweetPut;
import common.api.twitteruser.postuser.UserGet;
import common.api.twitteruser.postuser.UserSignOut;
import common.api.twitteruser.postuser.UserSignin;
import common.api.twitteruser.putuser.UserSignup;
import common.comms.request.PostRequest;
import common.comms.request.PutRequest;
import common.comms.response.PostResponse;
import common.comms.response.PutResponse;
import common.utils.DateUtils;
import gui.backend.comm.connection.ServerConnection;
import gui.backend.comm.connection.ServerConnectionListener;
import gui.frontend.components.base.Div;
import gui.frontend.components.ext.ErrorBanner;
import gui.frontend.components.ext.alert.AlertItemCmp;
import gui.frontend.components.ext.profile.ProfileItemCmp;
import gui.frontend.components.ext.tweet.RetweetCmp;
import gui.frontend.components.ext.tweet.TweetCmp;
import gui.frontend.constants.UIConstants;
import gui.frontend.dialog.LikesPopup;
import gui.frontend.menu.SideMenu;
import gui.frontend.views.*;

import javax.swing.*;
import java.util.*;
import java.util.function.Predicate;

import static common.utils.ProfileImageGen.randomBase64SquareImage;

public class Controller {
    private final ServerConnection serverConnection;
    private String userToken;
    private String userHandle;
    private long minEpochCreated;

    private final Map<String, UserGet.UserGetResponseSuccess> userByHandle;

    private final ErrorBanner errorBanner;
    private final LikesPopup likesPopup;
    private final MainView mainView;
    private final TweetsView tweetsView;
    private final LoginView loginView;
    private final SignUpView signUpView;
    private final AccessView accessView;
    private final ProfileBrowseView profileBrowseView;
    private final AlertsView alertsView;
    private final SideMenu sideMenu;


    public Controller() {
        serverConnection = new ServerConnection();
        serverConnection.addServerConnectionListener(new ServerConnectionListener() {
            @Override
            public synchronized void onLoggedIn() {
                sideMenu.updateData(new SideMenu.SideMenuData(userHandle, userBase64imgOrDefault(userHandle), 0));
                mainView.setSideMenuVisible(true);
                alertsView.updateData(new AlertsView.AlertsViewData(new ArrayList<>(0)));
                updateTweets();
                updateView(tweetsView);
            }

            @Override
            public synchronized void onLoginError(Exception ex) {
                errorBanner.updateData(new ErrorBanner.ErrorBannerData("Error", ex.getMessage()));
            }

            @Override
            public synchronized void onAlert(Alert alert) {
                if (alert == null)
                    return;
                AlertItemCmp.AlertItemCmpData newAlertData = null;
                if (alert instanceof LikeAlert likeAlert) {
                    newAlertData =
                            new AlertItemCmp.AlertLikeCmpData(likeAlert, userBase64imgOrDefault(likeAlert.userId));
                    updateTweets();
                } else if (alert instanceof FollowAlert followAlert) {
                    newAlertData =
                            new AlertItemCmp.AlertFollowCmpData(followAlert, userBase64imgOrDefault(followAlert.userId));
                } else if (alert instanceof RetweetAlert retweetAlert) {
                    newAlertData =
                            new AlertItemCmp.AlertRetweetCmpData(retweetAlert, userBase64imgOrDefault(retweetAlert.retweetingUserId));
                    updateTweets();
                } else {
                    return;
                }
                var data = alertsView.getData();
                data.alertItemCmpData.add(newAlertData);
                alertsView.updateData(data);

                int newAlerts = 0;
                for (AlertItemCmp.AlertItemCmpData alertItemCmpData : data.alertItemCmpData)
                    if (!alertItemCmpData.isViewed())
                        ++newAlerts;
                var sideMenuData = sideMenu.getData();
                var newSideMenuData =
                        new SideMenu.SideMenuData(sideMenuData.handle, sideMenuData.base64img, newAlerts);
                sideMenu.updateData(newSideMenuData);
            }
        });

        this.profileBrowseView = new ProfileBrowseView();
        this.profileBrowseView.setListener(new ProfileBrowseView.ProfileBrowseViewListener() {
            @Override
            public ProfileItemCmp.ProfileItemCmpData onFollow(ProfileItemCmp.ProfileItemCmpData data) {
                ProfileItemCmp.ProfileItemCmpData newData = null;
                try {
                    PutResponse flResponse;
                    if (data.followed)
                        flResponse = new FollowPut.FollowRemoveRequest(userHandle, data.handle).send(userToken);
                    else
                        flResponse = new FollowPut.FollowAddRequest(userHandle, data.handle).send(userToken);
                    if (flResponse instanceof FollowPut.FollowResponseSuccess) {
                        var userGetRes = new UserGet.UserGetRequest(data.handle, userHandle).send(userToken);
                        if (userGetRes instanceof UserGet.UserGetResponseSuccess userGetResSucc) {
                            userByHandle.put(data.handle, userGetResSucc);
                            var profilePic = userBase64imgOrDefault(data.handle);
                            newData = new ProfileItemCmp.ProfileItemCmpData(
                                    profilePic, userGetResSucc.epochCreated, userGetResSucc.handle, userGetResSucc.following, userGetResSucc.followers, userGetResSucc.follows
                            );
                            updateTweets();
                        }
                    }
                } catch (ResponseException re) {
                    var err = re.getResponseError();
                    errorBanner.updateData(new ErrorBanner.ErrorBannerData(err.getType(), err.getMessage()));
                } catch (Exception ex) {
                    errorBanner.updateData(new ErrorBanner.ErrorBannerData("Error", ex.getMessage()));
                }
                return newData;
            }

            @Override
            public void onOpen(ProfileItemCmp.ProfileItemCmpData data) {

            }
        });

        this.alertsView = new AlertsView();
        this.alertsView.setAlertsViewListener(new AlertsView.AlertsViewListener() {
            @Override
            public AlertItemCmp.AlertItemCmpData onAction(AlertItemCmp.AlertItemCmpData alertItemCmpData) {
                AlertItemCmp.AlertItemCmpData resData = alertItemCmpData;
                if (!alertItemCmpData.isViewed()) {
                    try {
                        PutResponse response = send(new PutAlert.PutAlertAsViewedRequest(alertItemCmpData.getId()));
                        if (response instanceof PutAlert.PutAlertResponseSuccess) {
                            if (alertItemCmpData instanceof AlertItemCmp.AlertLikeCmpData alertLikeCmpData) {
                                resData = new AlertItemCmp.AlertLikeCmpData(alertLikeCmpData, true);
                            } else if (alertItemCmpData instanceof AlertItemCmp.AlertFollowCmpData alertFollowCmpData) {
                                resData = new AlertItemCmp.AlertFollowCmpData(alertFollowCmpData, true);
                            } else if (alertItemCmpData instanceof AlertItemCmp.AlertRetweetCmpData alertRetweetCmpData) {
                                resData = new AlertItemCmp.AlertRetweetCmpData(alertRetweetCmpData, true);
                            }
                            if (resData != alertItemCmpData) {
                                final AlertItemCmp.AlertItemCmpData fNewData = resData;
                                new Thread(() -> {
                                    var alertsData = alertsView.getData();
                                    alertsData.alertItemCmpData.remove(alertItemCmpData);
                                    alertsData.alertItemCmpData.add(fNewData);
                                    alertsView.updateData(alertsData);
                                    //
                                    int viewedCnt = (int) alertsView.getData().alertItemCmpData.stream().filter(Predicate.not(AlertItemCmp.AlertItemCmpData::isViewed)).count();
                                    var sideMenuData = sideMenu.getData();
                                    sideMenu.updateData(new SideMenu.SideMenuData(sideMenuData.handle, sideMenuData.base64img, viewedCnt));
                                }).start();
                            }
                        }
                    } catch (ResponseException re) {
                        var err = re.getResponseError();
                        errorBanner.updateData(new ErrorBanner.ErrorBannerData(err.getType(), err.getMessage()));
                    } catch (Exception ex) {
                        errorBanner.updateData(new ErrorBanner.ErrorBannerData("Error", ex.getMessage()));
                    }
                }
                if (alertItemCmpData instanceof AlertItemCmp.AlertLikeCmpData alertLikeCmpData) {
                    tweetsView.goToTweet(alertLikeCmpData.getTweetId());
                    updateView(tweetsView);
                }
                if (alertItemCmpData instanceof AlertItemCmp.AlertRetweetCmpData alertRetweetCmpData) {
                    tweetsView.goToTweet(alertRetweetCmpData.getRetweetTweetId());
                    updateView(tweetsView);
                }
                return alertItemCmpData;
            }
        });

        this.userByHandle = Collections.synchronizedMap(new HashMap<>());

        this.likesPopup = new LikesPopup();

        this.errorBanner = new ErrorBanner();

        this.sideMenu = new SideMenu();
        this.sideMenu.setListener(new SideMenu.SideMenuListener() {
            @Override
            public void onViewTweets() {
                updateTweets();
                updateView(tweetsView);
            }

            @Override
            public void onViewAlerts() {
                updateView(alertsView);
            }

            @Override
            public void onViewUsers() {
                updateUsers();
                updateView(profileBrowseView);
            }

            @Override
            public void onLogout() {
                performSignOut();
            }
        });

        this.mainView = new MainView(this.errorBanner, this.sideMenu);

        this.tweetsView = new TweetsView(new TweetsView.TweetsViewListener() {
            @Override
            public synchronized void onTweetCreate(String text, String base64Img) {
                performTweetCreation(text, base64Img);
            }

            @Override
            public synchronized void onRetweetCreate(String tweetId, String text, String base64Img) {
                performRetweetCreation(tweetId, text, base64Img);
            }

            @Override
            public synchronized TweetCmp.TweetCmpData onTweetLike(TweetCmp.TweetCmpData tweetCmpData) {
                TweetCmp.TweetCmpData newData = null;
                try {
                    PutResponse likeResponse;
                    if (tweetCmpData.liked)
                        likeResponse = new PutLike.LikeRemoveRequest(userHandle, tweetCmpData.tweetId).send(userToken);
                    else
                        likeResponse = new PutLike.LikeAddRequest(userHandle, tweetCmpData.tweetId).send(userToken);
                    if (likeResponse instanceof PutLike.LikeResponseSuccess) {
                        List<String> reqIds = new ArrayList<>(1);
                        reqIds.add(tweetCmpData.tweetId);
                        var tweetGetRes = new TweetPost.TweetGetRequest(userHandle, reqIds).send(userToken);
                        if (tweetGetRes instanceof TweetPost.TweetGetResponseSuccess tweetGetResSucc) {
                            if (tweetGetResSucc.tweets.size() == 1) {
                                TweetPost.TweetGetResponseItem tweetVersion = tweetGetResSucc.tweets.get(0);
                                newData =
                                        new TweetCmp.TweetCmpData(userBase64imgOrDefault(tweetVersion.authorHandle), tweetVersion);
                            }
                        }
                    }
                } catch (ResponseException re) {
                    var err = re.getResponseError();
                    errorBanner.updateData(new ErrorBanner.ErrorBannerData(err.getType(), err.getMessage()));
                } catch (Exception ex) {
                    errorBanner.updateData(new ErrorBanner.ErrorBannerData("Error", ex.getMessage()));
                }
                return newData;
            }

            @Override
            public synchronized void onViewTweetLikes(String tweetId) {
                showLikes(tweetId);
            }
        });

        this.loginView = new LoginView(this::performSignIn);
        this.signUpView = new SignUpView(new SignUpView.SignUpViewListener() {
            @Override
            public void onSignUpRequest(String handle, String passwd) {
                performSignUp(handle, passwd);
            }

            @Override
            public void onError(String title, String message) {
                errorBanner.updateData(new ErrorBanner.ErrorBannerData(title, message));
            }
        });
        this.accessView = new AccessView(new AccessView.AccessViewListener() {
            @Override
            public void onLogin() {
                updateView(loginView);
            }

            @Override
            public void onSignUp() {
                updateView(signUpView);
            }
        });

        updateView(this.accessView);
    }

    private void updateUsers() {
        new Thread(() -> {
            try {
                var res = new UserGet.UserGetAllRequest(userHandle).send(userToken);
                if (res instanceof UserGet.UserGetAllResponseSuccess userGetAll) {
                    List<ProfileItemCmp.ProfileItemCmpData> profileItemCmpData = new ArrayList<>(userGetAll.users.size() - 1);
                    for (UserGet.UserGetResponseSuccess user : userGetAll.users) {
                        if (!user.handle.equals(userHandle)) {
                            userByHandle.put(user.handle, user);
                            var profilePic = userBase64imgOrDefault(user.handle);
                            profileItemCmpData.add(new ProfileItemCmp.ProfileItemCmpData(
                                    profilePic, user.epochCreated, user.handle, user.following, user.followers, user.follows
                            ));
                        }
                    }
                    profileBrowseView.updateData(new ProfileBrowseView.ProfileBrowseViewData(profileItemCmpData));
                } else {
                    throw new UserSignin.UserSigninFailed().except();
                }
            } catch (ResponseException re) {
                var err = re.getResponseError();
                errorBanner.updateData(new ErrorBanner.ErrorBannerData(err.getType(), err.getMessage()));
            } catch (Exception ex) {
                errorBanner.updateData(new ErrorBanner.ErrorBannerData("Error", ex.getMessage()));
            }
        }).start();
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    public MainView getMainView() {
        return mainView;
    }

    public String getUserToken() {
        return userToken;
    }

    private void performSignIn(String handle, String passwd) {
        new Thread(() -> {
            UserSignin.UserSigninSuccess signinSuccess = null;
            try {
                var res = send(new UserSignin.UserSigninRequest(handle, passwd));
                if (res instanceof UserSignin.UserSigninSuccess resSucc) {
                    signinSuccess = resSucc;
                    userHandle = handle;
                } else {
                    throw new UserSignin.UserSigninFailed().except();
                }
            } catch (ResponseException re) {
                var err = re.getResponseError();
                errorBanner.updateData(new ErrorBanner.ErrorBannerData(err.getType(), err.getMessage()));
            } catch (Exception ex) {
                errorBanner.updateData(new ErrorBanner.ErrorBannerData("Error", ex.getMessage()));
            }
            if (signinSuccess != null) {
                loginView.clear();
                userToken = signinSuccess.token;
                serverConnection.signin(userToken, handle);
            }
        }).start();
    }

    private void performSignOut() {
        String tmpUserHandle = userHandle;
        String tmpUserToken = userToken;
        userHandle = null;
        userToken = null;
        new Thread(() -> {
            mainView.setSideMenuVisible(false);
            updateView(accessView);
            try {
                send(new UserSignOut.UserSignOutRequest(tmpUserHandle, tmpUserToken));
                alertsView.updateData(new AlertsView.AlertsViewData(new ArrayList<>(0)));
                tweetsView.updateData(new TweetsView.TweetsViewData(DateUtils.getEpochTimeNowMinusDays(3), new ArrayList<>(0)));
                tweetsView.cleanInputs();
                profileBrowseView.updateData(new ProfileBrowseView.ProfileBrowseViewData(new ArrayList<>(0)));
                this.userByHandle.clear();
            } catch (ResponseException re) {
                var err = re.getResponseError();
                errorBanner.updateData(new ErrorBanner.ErrorBannerData(err.getType(), err.getMessage()));
            } catch (Exception ex) {
                errorBanner.updateData(new ErrorBanner.ErrorBannerData("Error", ex.getMessage()));
            } finally {
                serverConnection.close();
            }
        }).start();
    }

    private void performSignUp(String handle, String passwd) {
        new Thread(() -> {
            UserSignup.UserSignupResponseSuccess signupSuccess = null;
            try {
                String img = randomBase64SquareImage();
                var res = send(new UserSignup.UserSignupRequest(handle, passwd, img));
                if (res instanceof UserSignup.UserSignupResponseSuccess resSucc)
                    signupSuccess = resSucc;
            } catch (ResponseException re) {
                var err = re.getResponseError();
                errorBanner.updateData(new ErrorBanner.ErrorBannerData(err.getType(), err.getMessage()));
            } catch (Exception ex) {
                errorBanner.updateData(new ErrorBanner.ErrorBannerData("Error", ex.getMessage()));
            }
            if (signupSuccess != null) {
                signUpView.clear();
                userHandle = handle;
                minEpochCreated = DateUtils.getEpochTimeNowMinusDays(3);
                updateView(loginView);
            }
        }).start();
    }

    private void performTweetCreation(String text, String base64Img) {
        new Thread(() -> {
            TweetPut.TweetPublishResponseSuccess createSuccess = null;
            try {
                var res = send(new TweetPut.TweetPublishRequest(userHandle, text, base64Img));
                if (res instanceof TweetPut.TweetPublishResponseSuccess resSucc)
                    createSuccess = resSucc;
                else
                    throw new TweetPut.TweetPublishResponseFailed("").except();
            } catch (ResponseException re) {
                var err = re.getResponseError();
                errorBanner.updateData(new ErrorBanner.ErrorBannerData(err.getType(), err.getMessage()));
            } catch (Exception ex) {
                errorBanner.updateData(new ErrorBanner.ErrorBannerData("Error", ex.getMessage()));
            }
            if (createSuccess != null) {
                updateTweets();
            }
        }).start();
    }

    private void performRetweetCreation(String tweetId, String text, String base64Img) {
        new Thread(() -> {
            TweetPut.TweetPublishResponseSuccess createSuccess = null;
            try {
                var res = send(new TweetPut.RetweetPublishRequest(userHandle, text, base64Img, tweetId));
                if (res instanceof TweetPut.TweetPublishResponseSuccess resSucc)
                    createSuccess = resSucc;
                else
                    throw new TweetPut.TweetPublishResponseFailed("").except();
            } catch (ResponseException re) {
                var err = re.getResponseError();
                errorBanner.updateData(new ErrorBanner.ErrorBannerData(err.getType(), err.getMessage()));
            } catch (Exception ex) {
                errorBanner.updateData(new ErrorBanner.ErrorBannerData("Error", ex.getMessage()));
            }
            if (createSuccess != null) {
                updateTweets();
            }
        }).start();
    }

    private void updateTweets() {
        try {
            var res = send(new TweetPost.TweetSampleRequest(userHandle, minEpochCreated));
            if (res instanceof TweetPost.TweetGetResponseSuccess resSucc) {
                Map<String, TweetPost.TweetGetResponseItem> missingTweetRetweets = new HashMap<>();
                Map<String, TweetPost.TweetGetResponseItem> tweetBytId = new HashMap<>(resSucc.tweets.size());
                for (var tweet : resSucc.tweets)
                    tweetBytId.put(tweet.tweetId, tweet);
                //
                List<TweetCmp.TweetCmpData> tweetDatas = new ArrayList<>(tweetBytId.size());
                for (TweetPost.TweetGetResponseItem tweet : resSucc.tweets) {
                    if (tweet.retweetOf == null) {
                        String authorBase64Img = userBase64imgOrDefault(tweet.authorHandle);
                        tweetDatas.add(new TweetCmp.TweetCmpData(authorBase64Img, tweet));
                    } else {
                        var retweeted = tweetBytId.getOrDefault(tweet.retweetOf, null);
                        if (retweeted == null) {
                            missingTweetRetweets.put(tweet.tweetId, tweet);
                        } else {
                            // TODO get image.
                            String rAuthorBase64Img = userBase64imgOrDefault(tweet.authorHandle);
                            String tAuthorBase64Img = userBase64imgOrDefault(retweeted.authorHandle);
                            tweetDatas.add(new RetweetCmp.RetweetCmpData(rAuthorBase64Img, tweet, tAuthorBase64Img, retweeted));
                        }
                    }
                }
                if (missingTweetRetweets.size() > 0) {
                    Set<String> missingRetweetsIds = new HashSet<>(missingTweetRetweets.size());
                    for (var tweet : missingTweetRetweets.values())
                        missingRetweetsIds.add(tweet.retweetOf);
                    var retweetedById = getTweets(missingRetweetsIds);
                    for (TweetPost.TweetGetResponseItem tweet : missingTweetRetweets.values()) {
                        var retweeted = retweetedById.getOrDefault(tweet.retweetOf, null);
                        if (retweeted != null) {
                            String rAuthorBase64Img = userBase64imgOrDefault(tweet.authorHandle);
                            String tAuthorBase64Img = userBase64imgOrDefault(retweeted.authorHandle);
                            tweetDatas.add(new RetweetCmp.RetweetCmpData(rAuthorBase64Img, tweet, tAuthorBase64Img, retweeted));
                        }
                    }
                }
                tweetDatas.sort(Collections.reverseOrder());
                TweetsView.TweetsViewData data = new TweetsView.TweetsViewData(minEpochCreated, tweetDatas);
                tweetsView.updateData(data);
            }
        } catch (ResponseException re) {
            var err = re.getResponseError();
            errorBanner.updateData(new ErrorBanner.ErrorBannerData(err.getType(), err.getMessage()));
        } catch (Exception ex) {
            errorBanner.updateData(new ErrorBanner.ErrorBannerData("Error", ex.getMessage()));
        }
    }

    private void showLikes(String tweetId) {
        try {
            var handles = (PostLike.LikeGetResponseHandles) new PostLike.LikeGetHandlesRequest(tweetId).send(userToken);
            Map<String, String> base64ImgByHandles = new HashMap<>(handles.handles.size());
            for (String handle : handles.handles)
                base64ImgByHandles.put(handle, userBase64imgOrDefault(handle));

            likesPopup.updateData(new LikesPopup.LikesDialogData(base64ImgByHandles));
            likesPopup.setVisible(true);
        } catch (ResponseException re) {
            var err = re.getResponseError();
            errorBanner.updateData(new ErrorBanner.ErrorBannerData(err.getType(), err.getMessage()));
        } catch (Exception ex) {
            errorBanner.updateData(new ErrorBanner.ErrorBannerData("Error", ex.getMessage()));
        }
    }

    private String userBase64imgOrDefault(String handle) {
        var user = getUser(handle);
        return (user == null || user.base64Img == null) ? UIConstants.UNKNOWN_USER_BASE64 : user.base64Img;
    }

    public UserGet.UserGetResponseSuccess getUser(String handle) {
        try {
            var user = userByHandle.getOrDefault(handle, null);
            if (user == null) {
                var res = new UserGet.UserGetRequest(handle, userHandle).send(userToken);
                if (res instanceof UserGet.UserGetResponseSuccess resSucc) {
                    userByHandle.put(resSucc.handle, resSucc);
                    return resSucc;
                }
            } else
                return user;
        } catch (ResponseException re) {
            var err = re.getResponseError();
            errorBanner.updateData(new ErrorBanner.ErrorBannerData(err.getType(), err.getMessage()));
        } catch (Exception ex) {
            errorBanner.updateData(new ErrorBanner.ErrorBannerData("Error", ex.getMessage()));
        }
        return null;
    }

    public Map<String, TweetPost.TweetGetResponseItem> getTweets(Set<String> tweetIds) {
        Map<String, TweetPost.TweetGetResponseItem> tweets = new HashMap<>(tweetIds.size());
        try {
            var res = new TweetPost.TweetGetRequest(userHandle, tweetIds).send(userToken);
            if (res instanceof TweetPost.TweetGetResponseSuccess resSucc) {
                for (var tweet : resSucc.tweets)
                    tweets.put(tweet.tweetId, tweet);
            }
        } catch (ResponseException re) {
            var err = re.getResponseError();
            errorBanner.updateData(new ErrorBanner.ErrorBannerData(err.getType(), err.getMessage()));
        } catch (Exception ex) {
            errorBanner.updateData(new ErrorBanner.ErrorBannerData("Error", ex.getMessage()));
        }
        return tweets;
    }

    public PostResponse send(PostRequest request) throws ResponseException {
        try {
            return request.send(this.userToken);
        } catch (ResponseException re) {
            throw re;
        } catch (Exception e) {
            throw new ResponseError(e.getMessage()).except();
        }
    }

    public PutResponse send(PutRequest request) throws ResponseException {
        try {
            return request.send(this.userToken);
        } catch (ResponseException re) {
            throw re;
        } catch (Exception e) {
            throw new ResponseError(e.getMessage()).except();
        }
    }

    public synchronized void updateView(Div div) {
//        SwingUtilities.invokeLater(() -> {
            errorBanner.updateData(null);
            mainView.updateView(div);
//        });
    }

}
