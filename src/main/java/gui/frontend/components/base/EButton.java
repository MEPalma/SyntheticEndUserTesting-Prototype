package gui.frontend.components.base;

import gui.frontend.components.Style;
import gui.frontend.constants.UIConstants;

import javax.swing.*;
import java.awt.*;

public class EButton extends Div {

    private final Text text;

    public EButton(String text) {
        this(text, EButton.defaultStyle());
    }

    public EButton(Style style) {
        this("", style);
    }

    public EButton(String text, Style style) {
        super(new GridLayout(1, 1, 0, 0), defaultStyle());

        Style textStyle = new Style(style.getFont(), style.getForeground(), style.getBackground(), BorderFactory.createEmptyBorder());
        this.text = new Text(text, textStyle);
        this.text.setHorizontalAlignment(Text.CENTER);
        setText(text);

        setMinimumSize(new Dimension(20, 20));
        style.applyTo(this);

        add(this.text);
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public String getText() {
        return this.text.getText();
    }

    public static Style defaultStyle() {
        return new Style(
                UIConstants.FONT_TITLE_S,
                UIConstants.WHITE,
                UIConstants.BLUE,
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
        );
    }
}
