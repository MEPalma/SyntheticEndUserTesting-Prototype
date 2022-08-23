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

public class RetweetCreateCmp extends Div {

    public static class RetweetCreateCmpData {
        public final TweetCmp.TweetCmpData tweetCmpData;

        public RetweetCreateCmpData(TweetCmp.TweetCmpData tweetCmpData) {
            this.tweetCmpData = tweetCmpData;
        }
    }

    public interface RetweetCreateCmpListener {
        void onCreate(String text, String base64Img, RetweetCreateCmpData retweetCreateCmpData);
        void onCancel();
    }

    private final RetweetCreateCmpListener retweetCreateCmpListener;

    private final TextInput textInput;
    private final EButton publishBtn;
    private final EButton cancelBtn;
    private final TweetCmp tweetCmp;
    private final JCheckBox attachImage;
    private RetweetCreateCmpData data;


    public RetweetCreateCmp(RetweetCreateCmpListener retweetCreateCmpListener) {
        super(new BorderLayout(10, 10), defaultPaddedStyle());
        //
        this.retweetCreateCmpListener = retweetCreateCmpListener;
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
        this.publishBtn = new EButton("Retweet");
        this.publishBtn.addMouseListener(new MouseAdapter() {
            private final AtomicBoolean processing = new AtomicBoolean(false);

            @Override
            public void mouseClicked(MouseEvent e) {
                if (processing.compareAndSet(false, true)) {
                    new Thread(() -> {
                        try {
                            var tmpData = getData();
                            String text = textInput.getText().strip();
                            String image = attachImage.isSelected() ? ProfileImageGen.randomBase64SquareImage() : null;
                            retweetCreateCmpListener.onCreate(text, image, tmpData);
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
        Style cancelBtnStyle = EButton.defaultStyle();
        cancelBtnStyle.setBackground(UIConstants.RED);
        this.cancelBtn = new EButton("Cancel", cancelBtnStyle);
        this.cancelBtn.addMouseListener(new MouseAdapter() {
            private final AtomicBoolean processing = new AtomicBoolean(false);

            @Override
            public void mouseClicked(MouseEvent e) {
                if (processing.compareAndSet(false, true)) {
                    new Thread(() -> {
                        try {
                            retweetCreateCmpListener.onCancel();
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
        this.tweetCmp = new TweetCmp();
        //
        {
            add(this.tweetCmp, BorderLayout.NORTH);
            add(this.textInput, BorderLayout.CENTER);
            {
                Div cmdsDiv = new Div(new GridLayout(1, 2, 4, 0), defaultPaddedStyle());
                cmdsDiv.add(this.cancelBtn);
                cmdsDiv.add(this.publishBtn);
                {
                    Div outerCmdDiv = new Div(new BorderLayout(4, 0), defaultPaddedStyle());
                    outerCmdDiv.add(cmdsDiv, BorderLayout.CENTER);
                    cmdsDiv.add(this.attachImage, BorderLayout.EAST);
                    add(outerCmdDiv, BorderLayout.SOUTH);
                }
            }
        }
    }

    public synchronized RetweetCreateCmpData getData() {
        return data;
    }

    public synchronized void updateData(RetweetCreateCmpData data) {
        this.data = data;
        this.tweetCmp.updateData(data.tweetCmpData);
        this.textInput.setText("");
        attachImage.setSelected(false);
        revalidate();
        repaint();
    }

    public void clean() {
        textInput.setText("");
        attachImage.setSelected(false);
    }

}
