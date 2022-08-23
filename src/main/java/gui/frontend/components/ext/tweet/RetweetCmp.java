package gui.frontend.components.ext.tweet;

import common.api.tweet.posttweet.TweetPost;
import gui.frontend.components.Style;

import javax.swing.*;

public class RetweetCmp extends TweetCmp {
    public static class RetweetCmpData extends TweetCmpData {
        public final TweetCmpData ogTweetData;

        public RetweetCmpData(String authorBase64Img, TweetPost.TweetGetResponseItem tweet, String ogAuthorBase64Img, TweetPost.TweetGetResponseItem ogTweetData) {
            super(authorBase64Img, tweet);
            this.ogTweetData = new TweetCmpData(ogAuthorBase64Img, ogTweetData);
        }
    }

    private final TweetCmp ogTweet;

    public RetweetCmp() {
        super();
        //
        this.ogTweet = new TweetCmp();
        Style ogTweetStyle = defaultStyle();
        ogTweetStyle.setBorder(BorderFactory.createEmptyBorder(12, 24, 0, 24));
        ogTweetStyle.applyTo(this.ogTweet);
        add(this.ogTweet);
        add(Box.createRigidArea(separatorDim));
    }

    @Override
    public synchronized void updateData(TweetCmpData data) {
        if (data instanceof RetweetCmpData retweetData)
            this.ogTweet.updateData(retweetData.ogTweetData);
        super.updateData(data);
    }

}
