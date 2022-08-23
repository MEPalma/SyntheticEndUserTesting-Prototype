package gui.frontend.views;

import gui.frontend.components.Style;
import gui.frontend.components.base.Div;
import gui.frontend.components.base.Text;
import gui.frontend.components.ext.ErrorBanner;
import gui.frontend.constants.UIConstants;
import gui.frontend.menu.SideMenu;

import java.awt.*;

public class MainView extends Div {

    private final Div mainStage;
    private final ErrorBanner errorBanner;
    private final SideMenu sideMenu;

    public MainView(ErrorBanner errorBanner, SideMenu sideMenu) {
        super(new BorderLayout(10, 10));
        this.mainStage = new Div();
        this.errorBanner = errorBanner;
        this.sideMenu = sideMenu;
        this.errorBanner.setMinimumSize(new Dimension(0, 0));

        Style titleStyle = Text.defaultStyle();
        titleStyle.setFont(UIConstants.FONT_TITLE_L);
        titleStyle.setForeground(UIConstants.BLUE);
        titleStyle.setBackground(UIConstants.WHITE);

        add(new Text("SET_BaseSwingExample", titleStyle), BorderLayout.NORTH);
        add(this.mainStage, BorderLayout.CENTER);
        add(this.errorBanner, BorderLayout.SOUTH);
    }

    public synchronized void setSideMenuVisible(boolean visible) {
        remove(sideMenu);
        if (visible)
            add(sideMenu, BorderLayout.WEST);
        repaint();
        revalidate();
    }

    public synchronized void setErrorBannerVisible(boolean visible) {
        remove(errorBanner);
        if (visible)
            add(errorBanner, BorderLayout.SOUTH);
        repaint();
        revalidate();
    }

    public synchronized void updateView(Div div) {
        this.mainStage.removeAll();
        this.mainStage.add(div);
        this.mainStage.revalidate();
        this.mainStage.repaint();
        this.repaint();
        this.revalidate();
    }

}
