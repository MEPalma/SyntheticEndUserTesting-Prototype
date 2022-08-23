package backend.httpproxy;

import backend.controller.Controller;
import com.sun.net.httpserver.HttpServer;
import common.api.alert.AlertRequest;
import common.api.follow.FollowRequest;
import common.api.like.LikeRequest;
import common.api.tweet.TweetRequest;
import common.api.twitteruser.UserRequest;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpProxy {
    private final HttpServer server;
    private final Controller controller;

    public HttpProxy(int port, Controller controller) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.controller = controller;
        this.server.createContext(UserRequest.CONTEXT, new HttpUserProxy(this.controller));
        this.server.createContext(TweetRequest.CONTEXT, new HttpTweetProxy(this.controller));
        this.server.createContext(LikeRequest.CONTEXT, new HttpLikeProxy(this.controller));
        this.server.createContext(FollowRequest.CONTEXT, new HttpFollowProxy(this.controller));
        this.server.createContext(AlertRequest.CONTEXT, new HttpAlertsProxy(this.controller));
        this.server.start();
    }
}
