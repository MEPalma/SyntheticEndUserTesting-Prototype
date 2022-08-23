package gui.frontend.components.base;

import gui.frontend.components.Style;
import gui.frontend.constants.UIConstants;

import javax.swing.*;

public class Text extends JLabel {

    private String unformattedText;

    public Text() {
        this(Text.defaultStyle());
    }

    public Text(Style style) {
        this("", style);
    }

    public Text(String text) {
        this(text, Text.defaultStyle());
    }

    public Text(String text, Style style) {
        super(text, SwingConstants.CENTER);
        this.unformattedText = "";
        setHorizontalTextPosition(Text.CENTER);
        setVerticalTextPosition(Text.CENTER);
        setVerticalAlignment(Text.CENTER);
        setAlignmentX(Text.CENTER_ALIGNMENT);
        setAlignmentY(Text.CENTER_ALIGNMENT);
        setText(text);
        style.applyTo(this);
    }

    @Override
    public void setText(String text) {
        text = (text != null) ? text : "";
        this.unformattedText = text;
        String fText = String.format(
                "<html><div style='text-align:center;'>%s</div></html>",
                text
        );
        super.setText(fText);
    }

    @Override
    public String getText() {
        return this.unformattedText;
    }

    public static Style defaultStyle() {
        return new Style(
                UIConstants.FONT_TEXT,
                UIConstants.BLACK,
                UIConstants.WHITE,
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
        );
    }

}
