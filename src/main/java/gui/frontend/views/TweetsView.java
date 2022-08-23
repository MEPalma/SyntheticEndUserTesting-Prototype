package gui.frontend.views;

import gui.frontend.components.Style;
import gui.frontend.components.base.Div;
import gui.frontend.components.base.ItemsList;
import gui.frontend.components.base.Text;
import gui.frontend.components.ext.tweet.*;
import gui.frontend.constants.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class TweetsView extends Div {

    public static class TweetsViewData {
        public final long latestTweetEpoch;
        public final Collection<TweetCmp.TweetCmpData> tweets;

        public TweetsViewData(long latestTweetEpoch, Collection<TweetCmp.TweetCmpData> tweets) {
            this.latestTweetEpoch = latestTweetEpoch;
            this.tweets = tweets;
        }
    }

    public interface TweetsViewListener {
        void onTweetCreate(String text, String base64Img);

        void onRetweetCreate(String tweetId, String text, String base64Img);

        TweetCmp.TweetCmpData onTweetLike(TweetCmp.TweetCmpData tweetCmpData);

        void onViewTweetLikes(String tweetId);
    }

    private final TweetsViewListener listener;
    private final TweetCreateCmp tweetCreateCmp;
    private final RetweetCreateCmp retweetCreateCmp;

    private final Map<String, TweetCmp> tweetCmpByTweetId;
    private final TweetActionsCmp.TweetActionsCmpListener tweetActionsCmpListener;

    private TweetsViewData data;
    private ItemsList<JComponent> tweetCmpsItemsList;
    private JScrollPane scrollPane;


    public TweetsView(TweetsViewListener listener) {
        this.listener = listener;
        this.tweetCmpByTweetId = new HashMap<>();

        this.tweetActionsCmpListener = new TweetActionsCmp.TweetActionsCmpListener() {
            @Override
            public void onLike(TweetActionsCmp.TweetActionsCmpData data) {
                TweetCmp tweetCmp = tweetCmpByTweetId.getOrDefault(data.tweetId, null);
                if (tweetCmp != null) {
                    TweetCmp.TweetCmpData newTweetCmpData = listener.onTweetLike(tweetCmp.getData());
                    if (newTweetCmpData != null)
                        tweetCmp.updateData(newTweetCmpData);
                }
            }

            @Override
            public void onViewLikes(TweetActionsCmp.TweetActionsCmpData data) {
                listener.onViewTweetLikes(data.tweetId);
            }

            @Override
            public void onRetweet(TweetActionsCmp.TweetActionsCmpData data) {
                var tweetCmp = tweetCmpByTweetId.getOrDefault(data.tweetId, null);
                if (tweetCmp != null) {
                    retweetCreateCmp.updateData(new RetweetCreateCmp.RetweetCreateCmpData(tweetCmp.getData()));
                    remove(tweetCreateCmp);
                    add(retweetCreateCmp, BorderLayout.SOUTH);
                    repaint();
                    revalidate();
                }
            }
        };

        this.tweetCreateCmp = new TweetCreateCmp(new TweetCreateCmp.TweetCreateCmpListener() {
            @Override
            public void onCreate(String text, String base64Img) {
                listener.onTweetCreate(text, base64Img);
            }
        });

        this.retweetCreateCmp = new RetweetCreateCmp(new RetweetCreateCmp.RetweetCreateCmpListener() {
            @Override
            public void onCreate(String text, String base64Img, RetweetCreateCmp.RetweetCreateCmpData retweetCreateCmpData) {
                listener.onRetweetCreate(retweetCreateCmpData.tweetCmpData.tweetId, text, base64Img);
                remove(retweetCreateCmp);
                add(tweetCreateCmp, BorderLayout.SOUTH);
                repaint();
                revalidate();
            }

            @Override
            public void onCancel() {
                remove(retweetCreateCmp);
                add(tweetCreateCmp, BorderLayout.SOUTH);
                repaint();
                revalidate();
            }
        });

        this.tweetCmpsItemsList = new ItemsList<>();

        this.scrollPane = new JScrollPane(
                this.tweetCmpsItemsList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        this.scrollPane.getVerticalScrollBar().setUnitIncrement(UIConstants.SCROLL_SPEED_INCREMENT);

        Style titleStyle = Text.defaultStyle();
        titleStyle.setFont(UIConstants.FONT_TITLE_M);
        titleStyle.setForeground(UIConstants.BLUE);
        Text title = new Text("Tweets", titleStyle);
        add(title, BorderLayout.NORTH);
        add(this.scrollPane, BorderLayout.CENTER);
        add(this.tweetCreateCmp, BorderLayout.SOUTH);
    }

    public synchronized void updateData(TweetsViewData data) {
        this.data = data;
        this.tweetCmpByTweetId.clear();

        List<JComponent> tweetCmps = new ArrayList<>(data.tweets.size());
        for (TweetCmp.TweetCmpData tweetCmpData : data.tweets) {
            TweetActionsCmp tweetActionsCmp = new TweetActionsCmp();
            tweetActionsCmp.setListener(this.tweetActionsCmpListener);
            TweetCmp tweetCmp =
                    (tweetCmpData instanceof RetweetCmp.RetweetCmpData) ? new RetweetCmp() : new TweetCmp();
            tweetCmp.setTweetActionsCmp(tweetActionsCmp);
            tweetCmp.updateData(tweetCmpData);

            tweetCmps.add(getTweetCell(tweetCmp, tweetActionsCmp));
            tweetCmpByTweetId.put(tweetCmpData.tweetId, tweetCmp);
        }
        this.tweetCmpsItemsList.setItems(tweetCmps);
        repaint();
        revalidate();
    }

    public synchronized void goToTweet(String tweetId) {
        var tweetCmp = this.tweetCmpByTweetId.getOrDefault(tweetId, null);
        if (tweetCmp != null)
            tweetCmpsItemsList.scrollRectToVisible(tweetCmp.getParent().getBounds());
    }

    private static Div getTweetCell(TweetCmp tweetCmp, TweetActionsCmp actionsCmp) {
        Style style = Div.defaultStyle();
        style.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BLACK));
        Div cell = new Div(null, style);
        BoxLayout boxLayout = new BoxLayout(cell, BoxLayout.Y_AXIS);
        cell.setLayout(boxLayout);
        cell.add(tweetCmp);
        cell.add(actionsCmp);
        return cell;
    }

    public void cleanInputs() {
        this.tweetCreateCmp.clean();
        this.retweetCreateCmp.clean();
    }

}
