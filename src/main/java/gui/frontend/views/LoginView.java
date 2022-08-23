package gui.frontend.views;

import gui.frontend.components.Style;
import gui.frontend.components.base.Div;
import gui.frontend.components.base.PasswdBox;
import gui.frontend.components.base.Text;
import gui.frontend.components.base.TextBox;
import gui.frontend.components.base.EButton;
import gui.frontend.constants.UIConstants;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginView extends Div {

    public interface LoginViewListener {
        void onSignInRequest(String handle, String passwd);
    }

    private final TextBox handleBox;
    private final PasswdBox passwdBox;
    private final EButton signInEButton;


    private final LoginViewListener loginViewListener;

    public LoginView(LoginViewListener loginViewListener) {
        super(new GridBagLayout(), defaultStyle());

        this.loginViewListener = loginViewListener;

        Dimension dim = new Dimension(200, 30);

        this.handleBox = new TextBox();
        this.handleBox.setPreferredSize(dim);

        this.passwdBox = new PasswdBox();
        this.passwdBox.setPreferredSize(dim);

        this.signInEButton = new EButton("Sign In");
        this.signInEButton.setPreferredSize(dim);

        this.signInEButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String handle = handleBox.getText();
                String passwd = new String(passwdBox.getPassword()); // TODO..
                loginViewListener.onSignInRequest(handle, passwd);
            }
        });

        Style errorNoteStyle = Text.defaultStyle();
        errorNoteStyle.setFont(UIConstants.FONT_TITLE_M);
        errorNoteStyle.setForeground(UIConstants.RED);

        {
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.CENTER;
            c.gridwidth = 1;
            c.gridheight = 1;
            c.gridx = 0;
            c.gridy = 0;
            c.insets.bottom = 10;

            Text handleText = new Text("Handle");
            add(handleText, c);

            c.gridy += 1;
            add(this.handleBox, c);

            Text passwdText = new Text("Password");
            c.gridy += 1;
            add(passwdText, c);

            c.gridy += 1;
            add(this.passwdBox, c);

            c.gridy += 1;
            add(this.signInEButton, c);
        }
    }

    public synchronized void clear() {
        handleBox.setText("");
        passwdBox.setText("");
    }

}
