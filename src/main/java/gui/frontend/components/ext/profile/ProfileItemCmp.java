package gui.frontend.components.ext.profile;

import common.utils.DateUtils;
import common.api.twitteruser.postuser.UserGet;
import gui.frontend.components.Style;
import gui.frontend.components.base.Div;
import gui.frontend.components.base.EButton;
import gui.frontend.components.base.Photo;
import gui.frontend.components.base.Text;
import gui.frontend.constants.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProfileItemCmp extends Div {

    public static class ProfileItemCmpData {
        public final String base64Img;
        public final int epochCreated;
        public final String handle;
        public final long following;
        public final long followers;
        public final boolean followed;


        public ProfileItemCmpData(String base64Img, int epochCreated, String handle, long following, long followers, boolean followed) {
            this.base64Img = base64Img;
            this.epochCreated = epochCreated;
            this.handle = handle;
            this.following = following;
            this.followers = followers;
            this.followed = followed;
        }

        public ProfileItemCmpData(UserGet.UserGetResponseSuccess user) {
            this(user.base64Img, user.epochCreated, user.handle, user.following, user.followers, user.follows);
        }
    }

    public interface ProfileItemCmpListener {
        void onFollow(ProfileItemCmpData data);
    }

    private final Photo photo;
    private final Text handle;
    private final Text joinDate;
    private final Text followingCnt;
    private final Text followersCnt;
    private final EButton followBnt;

    private ProfileItemCmpListener listener;
    private ProfileItemCmpData data;

    public ProfileItemCmp() {
        super(null, defaultPaddedStyle());
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        setLayout(boxLayout);
        //
        photo = new Photo(Photo.SMALL_SQUARE);
        photo.setMaximumSize(photo.getPreferredSize());
        //
        Style handleStyle = Text.defaultStyle();
        handleStyle.setFont(UIConstants.FONT_TITLE_S);
        handle = new Text(handleStyle);
        //
        joinDate = new Text();
        followingCnt = new Text();
        followersCnt = new Text();
        followBnt = new EButton("Follow");
        followBnt.setPreferredSize(new Dimension(80, 24));
        followBnt.setMaximumSize(this.followBnt.getPreferredSize());
        followBnt.addMouseListener(new MouseAdapter() {
            private final AtomicBoolean processing = new AtomicBoolean(false);

            @Override
            public void mouseClicked(MouseEvent e) {
                if (processing.compareAndSet(false, true))
                    new Thread(() -> {
                        var list = listener;
                        if (list != null)
                            list.onFollow(getData());
                        processing.set(false);
                    }).start();
            }
        });
        //
        Dimension padDim = new Dimension(4, 4);
        add(Box.createRigidArea(padDim));
        add(this.photo);
        add(Box.createRigidArea(padDim));
        add(this.handle);
        add(Box.createHorizontalGlue());
        {
            Div followDiv = new Div(new GridLayout(2, 1, 2, 0), defaultStyle());
            followDiv.setPreferredSize(new Dimension(100, 36));
            followDiv.setMaximumSize(followDiv.getPreferredSize());
            followDiv.add(this.followingCnt);
            followDiv.add(this.followersCnt);
            add(followDiv);
        }
        add(Box.createRigidArea(padDim));
        add(this.followBnt);
        add(Box.createRigidArea(padDim));
    }

    private void showFollowButton(boolean isFollowed) {
        Style style = EButton.defaultStyle();
        if (isFollowed) {
            style.setBackground(UIConstants.BLACK);
            style.setForeground(UIConstants.WHITE);
        }
        style.applyTo(this.followBnt);
    }


    public void setListener(ProfileItemCmpListener listener) {
        this.listener = listener;
    }

    public synchronized ProfileItemCmpData getData() {
        return data;
    }

    public synchronized void updateData(ProfileItemCmpData data) {
        this.data = data;
        this.photo.updateImage(data.base64Img);
        this.handle.setText(data.handle);
        this.joinDate.setText("Joined " + DateUtils.formatEpochDate(data.epochCreated));
        this.followingCnt.setText("Following " + data.following);
        this.followersCnt.setText("Followers " + data.followers);
        showFollowButton(data.followed);
        repaint();
        revalidate();
    }
}
