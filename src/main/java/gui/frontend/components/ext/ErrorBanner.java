package gui.frontend.components.ext;

import gui.frontend.components.Style;
import gui.frontend.components.base.Div;
import gui.frontend.components.base.Text;
import gui.frontend.constants.UIConstants;

import java.awt.*;

public class ErrorBanner extends Div {

    public static class ErrorBannerData {
        public final String title;
        public final String message;

        public ErrorBannerData(String title, String message) {
            this.title = title;
            this.message = message;
        }
    }

    private final Text title;
    private final Text message;

    public ErrorBanner() {
        setBackground(UIConstants.RED);

        Style titleStyle = Text.defaultStyle();
        titleStyle.setForeground(UIConstants.WHITE);
        titleStyle.setFont(UIConstants.FONT_TITLE_S);
        this.title = new Text(titleStyle);

        Style messageStyle = Text.defaultStyle();
        messageStyle.setForeground(UIConstants.WHITE);
        this.message = new Text(messageStyle);

        add(title, BorderLayout.NORTH);
        add(message, BorderLayout.CENTER);
    }

    public synchronized void updateData(ErrorBannerData data) {
        if (data == null) {
            this.title.setText("");
            this.message.setText("");
            this.setBackground(UIConstants.WHITE);
        } else {
            this.title.setText(data.title);
            this.message.setText(data.message);
            this.setBackground(UIConstants.RED);
        }
        repaint();
        revalidate();
    }


}
