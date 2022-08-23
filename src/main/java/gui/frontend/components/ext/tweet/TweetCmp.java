package gui.frontend.components.ext.tweet;

import common.utils.DateUtils;
import common.api.tweet.posttweet.TweetPost;
import gui.frontend.components.Style;
import gui.frontend.components.base.Div;
import gui.frontend.components.base.Photo;
import gui.frontend.components.base.Text;
import gui.frontend.components.base.TextInput;
import gui.frontend.constants.UIConstants;

import javax.swing.*;
import java.awt.*;

public class TweetCmp extends Div {

    public static class TweetCmpData implements Comparable<TweetCmpData> {
        public final String tweetId;

        public final String authorHandle;
        public final String authorBase64Img;

        public final long epochCreated;
        public final String text;
        public final String base64Img;
        public final int likes;
        public final boolean liked;

        public TweetCmpData(String tweetId, String authorHandle, String authorBase64Img, long epochCreated, String text, String base64Img, int likes, boolean liked) {
            this.tweetId = tweetId;
            this.authorHandle = authorHandle;
            this.authorBase64Img = authorBase64Img;
            this.epochCreated = epochCreated;
            this.text = text;
            this.base64Img = base64Img;
            this.likes = likes;
            this.liked = liked;
        }

        public TweetCmpData(String authorBase64Img, TweetPost.TweetGetResponseItem tweet) {
            this(tweet.tweetId, tweet.authorHandle, authorBase64Img, tweet.epochCreated, tweet.text, tweet.base64Img, tweet.likes, tweet.liked);
        }

        @Override
        public int compareTo(TweetCmpData o) {
            return Long.compare(epochCreated, o.epochCreated);
        }
    }

    protected final Photo authorPhoto;
    protected final Text authorHandle;
    protected final Text dateCreated;
    protected final TextInput text;
    protected final Photo photo;
    protected final Dimension separatorDim;

    private TweetActionsCmp tweetActionsCmp;

    private TweetCmpData data;

    public TweetCmp() {
        super(null, defaultPaddedStyle());
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        setLayout(boxLayout);
        //
        separatorDim = new Dimension(4, 4);
        //
        this.authorPhoto = new Photo(Photo.SMALL_SQUARE);
        //
        Style authorHandleStyle = Text.defaultStyle();
        authorHandleStyle.setFont(UIConstants.FONT_TITLE_L);
        this.authorHandle = new Text(authorHandleStyle);
        //
        this.dateCreated = new Text();
        //
        Style textStyle = Text.defaultStyle();
        textStyle.setFont(UIConstants.FONT_TITLE_M);
        this.text = new TextInput(textStyle);
        this.text.setEditable(false);
        //
        this.photo = new Photo();
        //
        {
            Div headerDiv = new Div();
            BoxLayout headerBoxLayout = new BoxLayout(headerDiv, BoxLayout.LINE_AXIS);
            headerDiv.setLayout(headerBoxLayout);
            headerDiv.add(Box.createRigidArea(this.separatorDim));
            headerDiv.add(this.authorPhoto);
            headerDiv.add(Box.createRigidArea(this.separatorDim));
            headerDiv.add(this.authorHandle);
            headerDiv.add(Box.createHorizontalGlue());
            headerDiv.add(this.dateCreated);
            headerDiv.add(Box.createRigidArea(this.separatorDim));
            add(headerDiv);
        }
        add(Box.createRigidArea(separatorDim));
        add(this.text);
        add(Box.createRigidArea(separatorDim));
        add(this.photo);
        add(Box.createRigidArea(separatorDim));
    }

    public synchronized void updateData(TweetCmpData data) {
        this.data = data;
        this.authorPhoto.updateImage(data.authorBase64Img);
        this.authorHandle.setText(data.authorHandle);
        this.dateCreated.setText(DateUtils.formatEpochTime(data.epochCreated));
        this.text.setText(data.text);
        this.photo.updateImage(data.base64Img);
        TweetActionsCmp actionsCmp = this.tweetActionsCmp;
        if (actionsCmp != null)
            actionsCmp.updateData(new TweetActionsCmp.TweetActionsCmpData(data.tweetId, data.likes, data.liked));
        repaint();
        revalidate();
    }

    public synchronized TweetCmpData getData() {
        return this.data;
    }

    public TweetActionsCmp getTweetActionsCmp() {
        return tweetActionsCmp;
    }

    public void setTweetActionsCmp(TweetActionsCmp tweetActionsCmp) {
        this.tweetActionsCmp = tweetActionsCmp;
    }
}
