package gui.frontend.views;

import gui.frontend.components.Style;
import gui.frontend.components.base.*;
import gui.frontend.constants.UIConstants;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SignUpView extends Div {

    public interface SignUpViewListener {
        void onSignUpRequest(String handle, String passwd);
        void onError(String title, String message);
    }

    private final TextBox handleBox;
    private final PasswdBox passwdBox;
    private final PasswdBox passwdBoxCheck;
    private final EButton signInButton;

    private final SignUpViewListener signUpViewListener;

    public SignUpView(SignUpViewListener signUpViewListener) {
        super(new GridBagLayout(), defaultStyle());

        this.signUpViewListener = signUpViewListener;

        Dimension dim = new Dimension(200, 30);

        this.handleBox = new TextBox();
        this.handleBox.setPreferredSize(dim);

        this.passwdBox = new PasswdBox();
        this.passwdBox.setPreferredSize(dim);

        this.passwdBoxCheck = new PasswdBox();
        this.passwdBoxCheck.setPreferredSize(dim);

        this.signInButton = new EButton("Sign Up");
        this.signInButton.setPreferredSize(dim);

        this.signInButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String handle = handleBox.getText();
                String passwd = new String(passwdBox.getPassword()); // TODO..
                String passwdCheck = new String(passwdBoxCheck.getPassword()); // TODO..
                if (passwd.equals(passwdCheck)) {
                    signUpViewListener.onSignUpRequest(handle, passwd);
                } else {
                    signUpViewListener.onError("Error","Passwords do not match");
                }
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

            Text passwdTextCheck = new Text("Verify Password");
            c.gridy += 1;
            add(passwdTextCheck, c);

            c.gridy += 1;
            add(this.passwdBoxCheck, c);

            c.gridy += 1;
            add(this.signInButton, c);
        }
    }

    public synchronized void clear() {
        handleBox.setText("");
        passwdBox.setText("");
        passwdBoxCheck.setText("");
    }

}
