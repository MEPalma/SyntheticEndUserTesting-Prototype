package gui.frontend.menu;

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

public class SideMenu extends Div {
    public static class SideMenuData {
        public final String handle;
        public final String base64img;
        public final int newAlerts;

        public SideMenuData(String handle, String base64img, int newAlerts) {
            this.handle = handle;
            this.base64img = base64img;
            this.newAlerts = newAlerts;
        }
    }

    public interface SideMenuListener {
        void onViewTweets();

        void onViewAlerts();

        void onViewUsers();

        void onLogout();
    }

    private SideMenuListener listener;

    private static final Dimension SIDE_ITEM_DIM = Photo.BIG_SQUARE;
    private final Photo userPhoto;
    private final Text userHandle;
    private final Text newAlertsCnt;

    private SideMenuData data;

    public SideMenu() {
        super(null, defaultStyle());
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        this.setLayout(boxLayout);
        Style sideMenuStyle = defaultStyle();
        sideMenuStyle.setBorder(BorderFactory.createEmptyBorder(0, 12, 2, 2));
        sideMenuStyle.applyTo(this);
        //
        this.userPhoto = new Photo(SIDE_ITEM_DIM);
        //
        Style userHandleStyle = Text.defaultStyle();
        userHandleStyle.setFont(UIConstants.FONT_TITLE_S);
        this.userHandle = new Text(userHandleStyle);
        //
        Style newCntStyle = Text.defaultStyle();
        newCntStyle.setForeground(UIConstants.BLUE);
        //
        this.newAlertsCnt = new Text(newCntStyle);
        //
        Style cntNameStyle = Text.defaultStyle();
        cntNameStyle.setFont(UIConstants.FONT_TITLE_S);
        cntNameStyle.setForeground(UIConstants.BLACK);
        //
        Style blockStyle = Div.defaultStyle();
        blockStyle.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BLACK));
        //
        Dimension padDim = new Dimension(4, 4);
        //
        {
            this.add(Box.createRigidArea(padDim));
            this.add(this.userPhoto);
            this.add(Box.createRigidArea(padDim));
            this.add(this.userHandle);
            Div separator = new Div(null, blockStyle);
            separator.setMaximumSize(new Dimension(SIDE_ITEM_DIM.width, 1));
            separator.setPreferredSize(separator.getMaximumSize());
            separator.setMaximumSize(separator.getMaximumSize());
            this.add(separator);
            this.add(Box.createRigidArea(padDim));
        }
        {
            Div tweetsDiv = new Div(new GridLayout(1, 1, 0, 0), blockStyle);
            tweetsDiv.setMaximumSize(SIDE_ITEM_DIM);
            tweetsDiv.setPreferredSize(SIDE_ITEM_DIM);
            tweetsDiv.setMaximumSize(SIDE_ITEM_DIM);
            tweetsDiv.add(new Text("Tweets", cntNameStyle));
            tweetsDiv.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    listener.onViewTweets();
                }
            });
            this.add(tweetsDiv);
            this.add(Box.createRigidArea(padDim));
        }
        {
            Div alertsDiv = new Div(new GridBagLayout(), blockStyle);
            alertsDiv.setMaximumSize(SIDE_ITEM_DIM);
            alertsDiv.setPreferredSize(SIDE_ITEM_DIM);
            alertsDiv.setMaximumSize(SIDE_ITEM_DIM);
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.FIRST_LINE_END;
            c.gridx = 0;
            c.gridy = 0;
            alertsDiv.add(this.newAlertsCnt, c);
            c.gridy = 1;
            c.anchor = GridBagConstraints.CENTER;
            alertsDiv.add(new Text("Alerts", cntNameStyle), c);
            // TODO: remove
            alertsDiv.setName("AlertsDiv");
            alertsDiv.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    listener.onViewAlerts();
                }
            });
            this.add(alertsDiv);
            this.add(Box.createRigidArea(padDim));
        }
        {
            Div usersDiv = new Div(new GridLayout(1, 1, 0, 0), blockStyle);
            usersDiv.setMaximumSize(SIDE_ITEM_DIM);
            usersDiv.setPreferredSize(SIDE_ITEM_DIM);
            usersDiv.setMaximumSize(SIDE_ITEM_DIM);
            usersDiv.add(new Text("Users", cntNameStyle));
            usersDiv.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    listener.onViewUsers();
                }
            });
            this.add(usersDiv);
            this.add(Box.createRigidArea(padDim));
        }
        {
            EButton logoutButton = new EButton("Logout");
            logoutButton.setMaximumSize(new Dimension(SIDE_ITEM_DIM.width, 24));
            logoutButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    listener.onLogout();
                }
            });
            this.add(Box.createVerticalGlue());
            this.add(logoutButton);
        }
    }

    public void setListener(SideMenuListener listener) {
        this.listener = listener;
    }

    public synchronized void updateData(SideMenuData data) {
        this.data = data;
        this.userPhoto.updateImage(data.base64img);
        this.userHandle.setText(data.handle);
        this.newAlertsCnt.setText(Integer.toString(data.newAlerts));
        repaint();
        revalidate();
    }

    public synchronized SideMenuData getData() {
        return this.data;
    }

}
