package gui.frontend.components.ext.alert;

import common.utils.DateUtils;
import common.alert.FollowAlert;
import common.alert.LikeAlert;
import common.alert.RetweetAlert;
import gui.frontend.components.Style;
import gui.frontend.components.base.Div;
import gui.frontend.components.base.Photo;
import gui.frontend.components.base.Text;
import gui.frontend.constants.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class AlertItemCmp extends Div {
    public static abstract class AlertItemCmpData implements Comparable<AlertItemCmpData> {
        private final String id;
        private final long epochCreated;
        private final String base64Img;
        private final String title;
        private final String text;
        private final boolean isViewed;

        public AlertItemCmpData(String id, long epochCreated, String base64Img, String title, String text, boolean isViewed) {
            this.id = id;
            this.epochCreated = epochCreated;
            this.base64Img = base64Img;
            this.title = title;
            this.text = text;
            this.isViewed = isViewed;
        }

        public String getId() {
            return id;
        }

        public String getBase64Img() {
            return base64Img;
        }

        public String getTitle() {
            return title;
        }

        public String getText() {
            return text;
        }

        public long getEpochCreated() {
            return epochCreated;
        }

        public boolean isViewed() {
            return isViewed;
        }

        @Override
        public int compareTo(AlertItemCmpData o) {
            return Long.compare(epochCreated, o.epochCreated);
        }
    }

    public static class AlertLikeCmpData extends AlertItemCmpData {
        private String tweetId;

        public AlertLikeCmpData(LikeAlert alert, String base64UserImg) {
            super(
                    alert.id,
                    alert.epochCreated,
                    base64UserImg,
                    "Like",
                    String.format("%s liked your tweet.", alert.userId),
                    alert.viewed
            );
            this.tweetId = alert.tweetId;
        }

        public AlertLikeCmpData(AlertLikeCmpData alert, boolean viewed) {
            super(alert.getId(), alert.getEpochCreated(), alert.getBase64Img(), alert.getTitle(), alert.getText(), viewed);
            this.tweetId = alert.tweetId;
        }

        public String getTweetId() {
            return tweetId;
        }
    }

    public static class AlertRetweetCmpData extends AlertItemCmpData {
        private String retweetTweetId;

        public AlertRetweetCmpData(RetweetAlert alert, String base64UserImg) {
            super(
                    alert.id,
                    alert.epochCreated,
                    base64UserImg,
                    "Retweet",
                    String.format("%s retweeted your tweet.", alert.retweetingUserId),
                    alert.viewed
            );
            this.retweetTweetId = alert.retweetTweetId;
        }

        public AlertRetweetCmpData(AlertRetweetCmpData alert, boolean viewed) {
            super(alert.getId(), alert.getEpochCreated(), alert.getBase64Img(), alert.getTitle(), alert.getText(), viewed);
            this.retweetTweetId = alert.retweetTweetId;
        }

        public String getRetweetTweetId() {
            return retweetTweetId;
        }
    }

    public static class AlertFollowCmpData extends AlertItemCmpData {
        public AlertFollowCmpData(FollowAlert alert, String base64UserImg) {
            super(
                    alert.id,
                    alert.epochCreated,
                    base64UserImg,
                    "Follow",
                    String.format("%s started following you.", alert.userId),
                    alert.viewed
            );
        }

        public AlertFollowCmpData(AlertFollowCmpData alert, boolean viewed) {
            super(alert.getId(), alert.getEpochCreated(), alert.getBase64Img(), alert.getTitle(), alert.getText(), viewed);
        }
    }

    public interface AlertItemCmpListener {
        void onAction(AlertItemCmpData data);
    }

    private static final Dimension padDim = new Dimension(4, 4);
    private final Div viewedSignal;
    private final Photo img;
    private final Text title;
    private final Text text;
    private final Text date;
    private AlertItemCmpListener listener;
    private AlertItemCmpData data;

    public AlertItemCmp() {
        super(null, Div.defaultPaddedStyle());
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        setLayout(boxLayout);
        //
        this.viewedSignal = new Div();
        this.viewedSignal.setPreferredSize(new Dimension(4, 32));
        this.viewedSignal.setMaximumSize(this.viewedSignal.getPreferredSize());
        this.viewedSignal.setMinimumSize(this.viewedSignal.getPreferredSize());
        //
        this.img = new Photo(Photo.SMALL_SQUARE);
        this.img.setMaximumSize(this.img.getPreferredSize());
        //
        Style titleStyle = Text.defaultStyle();
        titleStyle.setFont(UIConstants.FONT_TITLE_M);
        this.title = new Text(titleStyle);
        this.title.setHorizontalAlignment(Text.LEFT);
        //
        Style textStyle = Text.defaultStyle();
        textStyle.setFont(UIConstants.FONT_TITLE_S);
        this.text = new Text(textStyle);
        this.text.setHorizontalAlignment(Text.LEFT);
        //
        this.date = new Text();
        //
        add(this.viewedSignal);
        add(Box.createRigidArea(padDim));
        add(this.img);
        add(Box.createRigidArea(padDim));
        {
            Div detailsDiv = new Div(new BorderLayout(0, 2), defaultPaddedStyle());
            detailsDiv.add(this.title, BorderLayout.NORTH);
            detailsDiv.add(this.text, BorderLayout.CENTER);
            detailsDiv.add(this.date, BorderLayout.EAST);
            add(detailsDiv);
        }
        add(Box.createRigidArea(padDim));
        addMouseListener(new MouseAdapter() {
            private final AtomicBoolean processing = new AtomicBoolean(false);

            @Override
            public void mouseClicked(MouseEvent e) {
                if (processing.compareAndSet(false, true)) {
                    new Thread(() -> {
                        try {
                            var data = getData();
                            var listn = listener;
                            if (listn != null && data != null)
                                listn.onAction(data);
                        } catch (Exception ex) {
                            // TODO: log
                            ex.printStackTrace();
                        }
                        processing.set(false);
                    }).start();
                }
            }
        });
    }

    public void setListener(AlertItemCmpListener listener) {
        this.listener = listener;
    }

    private void setAlertViewed(boolean isViewed) {
        Style style = Div.defaultPaddedStyle();
        style.setBackground(isViewed ? this.getBackground() : UIConstants.BLUE);
        style.applyTo(this.viewedSignal);
    }

    public synchronized AlertItemCmpData getData() {
        return this.data;
    }

    public synchronized void updateData(AlertItemCmpData data) {
        this.data = data;
        //
        this.img.updateImage(data.getBase64Img());
        this.title.setText(data.getTitle());
        this.text.setText(data.getText());
        this.date.setText(DateUtils.formatEpochTime(data.epochCreated));
        this.setAlertViewed(data.isViewed());
        revalidate();
        repaint();
    }

}
