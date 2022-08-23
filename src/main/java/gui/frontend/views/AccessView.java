package gui.frontend.views;

import gui.frontend.components.Style;
import gui.frontend.components.base.Div;
import gui.frontend.components.base.EButton;
import gui.frontend.constants.UIConstants;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AccessView extends Div {

    public interface AccessViewListener {
        void onLogin();

        void onSignUp();
    }

    private final AccessViewListener listener;
    private final EButton loginEButton;
    private final EButton signupEButton;

    public AccessView(AccessViewListener listener) {
        super(new GridBagLayout(), defaultStyle());
        this.listener = listener;

        Style loginButtonStyle = EButton.defaultStyle();
        loginButtonStyle.setBackground(UIConstants.GREEN);
        loginButtonStyle.setForeground(UIConstants.WHITE);
        this.loginEButton = new EButton("Login", loginButtonStyle);
        this.loginEButton.setPreferredSize(new Dimension(200, 30));
        this.loginEButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                listener.onLogin();
            }
        });

        Style signupButtonStyle = EButton.defaultStyle();
        signupButtonStyle.setBackground(UIConstants.BLUE);
        signupButtonStyle.setForeground(UIConstants.WHITE);
        this.signupEButton = new EButton("Signup", signupButtonStyle);
        this.signupEButton.setPreferredSize(loginEButton.getPreferredSize());
        this.signupEButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                listener.onSignUp();
            }
        });

        {
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.CENTER;
            c.gridwidth = 1;
            c.gridheight = 1;
            c.gridx = 0;
            c.gridy = 0;
            c.insets.bottom = 10;
            add(this.signupEButton, c);
            //
            c.gridy += 1;
            add(this.loginEButton, c);
        }
    }

}
