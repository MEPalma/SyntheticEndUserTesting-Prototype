package gui.frontend.components.ext.tweet;

import gui.frontend.components.Style;
import gui.frontend.components.base.Div;
import gui.frontend.components.base.EButton;
import gui.frontend.components.base.Text;
import gui.frontend.constants.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class TweetActionsCmp extends Div {
    public static class TweetActionsCmpData {
        public final String tweetId;
        public final int likes;
        public final boolean liked;

        public TweetActionsCmpData(String tweetId, int likes, boolean liked) {
            this.tweetId = tweetId;
            this.likes = likes;
            this.liked = liked;
        }
    }

    public interface TweetActionsCmpListener {
        void onLike(TweetActionsCmpData data);
        void onViewLikes(TweetActionsCmpData data);
        void onRetweet(TweetActionsCmpData data);
    }

    private final EButton likeEButton;
    private final EButton retweetEButton;
    private final Text likeCnt;

    private TweetActionsCmpData data;
    private TweetActionsCmpListener listener;

    public TweetActionsCmp() {
        super(null, defaultPaddedStyle());
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        setLayout(boxLayout);
        //
        Style likeCntStyle = Text.defaultStyle();
        likeCntStyle.setFont(UIConstants.FONT_TITLE_S);
        this.likeCnt = new Text("0 Likes", likeCntStyle);
        this.likeCnt.setVerticalAlignment(Text.CENTER);
        this.likeCnt.setVerticalTextPosition(Text.CENTER);
        this.likeCnt.addMouseListener(new MouseAdapter() {
            private final AtomicBoolean processing = new AtomicBoolean(false);
            @Override
            public void mouseClicked(MouseEvent e) {
                if (processing.compareAndSet(false, true))
                    new Thread(() -> {
                        var actionsCmpListener = listener;
                        if (actionsCmpListener != null)
                            actionsCmpListener.onViewLikes(getData());
                        processing.set(false);
                    }).start();
            }
        });
        //
        this.likeEButton = new EButton("Like");
        this.likeEButton.setPreferredSize(new Dimension(60, 24));
        this.likeEButton.setMaximumSize(this.likeEButton.getPreferredSize());
        this.likeEButton.addMouseListener(new MouseAdapter() {
            private final AtomicBoolean processing = new AtomicBoolean(false);

            @Override
            public void mouseClicked(MouseEvent e) {
                if (processing.compareAndSet(false, true))
                    new Thread(() -> {
                        var actionsCmpListener = listener;
                        if (actionsCmpListener != null)
                            actionsCmpListener.onLike(getData());
                        processing.set(false);
                    }).start();
            }
        });
        //
        Style retweetButtonStyle = EButton.defaultStyle();
        retweetButtonStyle.setBackground(UIConstants.GREEN);
        this.retweetEButton = new EButton("Retweet", retweetButtonStyle);
        this.retweetEButton.setPreferredSize(new Dimension(80, 24));
        this.retweetEButton.setMaximumSize(this.retweetEButton.getPreferredSize());
        this.retweetEButton.addMouseListener(new MouseAdapter() {
            private final AtomicBoolean processing = new AtomicBoolean(false);

            @Override
            public void mouseClicked(MouseEvent e) {
                if (processing.compareAndSet(false, true))
                    new Thread(() -> {
                        var actionsCmpListener = listener;
                        if (actionsCmpListener != null)
                            actionsCmpListener.onRetweet(getData());
                        processing.set(false);
                    }).start();
            }
        });
        //
        Dimension padDim = new Dimension(4, 4);
        add(Box.createRigidArea(padDim));
        add(this.likeCnt);
        add(Box.createRigidArea(padDim));
        add(this.likeEButton);
        add(Box.createRigidArea(padDim));
        add(Box.createHorizontalGlue());
        add(this.retweetEButton);
        add(Box.createRigidArea(padDim));
    }

    private void showLikedButton(boolean isLiked) {
        Style style = EButton.defaultStyle();
        if (isLiked) {
            style.setBackground(UIConstants.BLACK);
            style.setForeground(UIConstants.WHITE);
        }
        style.applyTo(this.likeEButton);
    }


    public void setListener(TweetActionsCmpListener listener) {
        this.listener = listener;
    }

    public synchronized void updateData(TweetActionsCmpData data) {
        this.data = data;
        showLikedButton(data.liked);
        this.likeCnt.setText(data.likes + " Likes");
        repaint();
        revalidate();
    }

    public synchronized TweetActionsCmpData getData() {
        return this.data;
    }

}
