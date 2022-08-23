package gui.frontend.components.ext.tweet;

import common.utils.ProfileImageGen;
import gui.frontend.components.Style;
import gui.frontend.components.base.Div;
import gui.frontend.components.base.EButton;
import gui.frontend.components.base.TextInput;
import gui.frontend.constants.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class TweetCreateCmp extends Div {

    public interface TweetCreateCmpListener {
        void onCreate(String text, String base64Img);
    }

    private final TweetCreateCmpListener listener;

    private final TextInput textInput;
    private final EButton publishBtn;
    private final JCheckBox attachImage;

    public TweetCreateCmp(TweetCreateCmpListener listener) {
        super(new BorderLayout(10, 10), defaultPaddedStyle());
        //
        this.listener = listener;
        //
        Style textInputStyle = TextInput.defaultStyle();
        textInputStyle.setBorder(BorderFactory.createLineBorder(UIConstants.BLACK, 1, true));
        textInputStyle.setFont(UIConstants.FONT_TITLE_S);
        this.textInput = new TextInput(textInputStyle);
        this.textInput.setMinimumSize(new Dimension(100, 60));
        this.textInput.setPreferredSize(this.textInput.getMinimumSize());
        //
        this.attachImage = new JCheckBox("Attach Image");
        textInputStyle.applyTo(this.attachImage);
        //
        this.publishBtn = new EButton("Tweet");
        this.publishBtn.addMouseListener(new MouseAdapter() {
            private final AtomicBoolean processing = new AtomicBoolean(false);

            @Override
            public void mouseClicked(MouseEvent e) {
                if (processing.compareAndSet(false, true)) {
                    new Thread(() -> {
                        try {
                            String text = textInput.getText().strip();
                            String image = attachImage.isSelected() ? ProfileImageGen.randomBase64SquareImage() : null;
                            listener.onCreate(text, image);
                            clean();
                        } catch (Exception ex) {
                            // TODO: log
                            ex.printStackTrace();
                        } finally {
                            processing.set(false);
                        }
                    }).start();
                }
            }
        });
        //
        {
            add(this.textInput, BorderLayout.CENTER);
            {
                Div cmdsDiv = new Div(new BorderLayout(4, 0), defaultPaddedStyle());
                cmdsDiv.add(this.publishBtn, BorderLayout.CENTER);
                cmdsDiv.add(this.attachImage, BorderLayout.EAST);
                add(cmdsDiv, BorderLayout.SOUTH);
            }
        }
    }

    public void clean() {
        textInput.setText("");
        attachImage.setSelected(false);
    }

}
