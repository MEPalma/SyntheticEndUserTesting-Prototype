package gui.frontend.components.base;

import gui.frontend.components.Style;
import gui.frontend.constants.UIConstants;

import javax.swing.*;
import java.awt.*;

public class TextBox extends JTextField {

    public TextBox() {
        this(defaultStyle());
        setPreferredSize(new Dimension(200, 30));
    }

    public TextBox(Style style) {
        style.applyTo(this);
    }

    public static Style defaultStyle() {
        return new Style(
                UIConstants.FONT_TEXT,
                UIConstants.BLACK,
                UIConstants.WHITE,
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BLACK)
        );
    }

}
