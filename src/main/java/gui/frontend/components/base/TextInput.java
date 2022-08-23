package gui.frontend.components.base;

import gui.frontend.components.Style;
import gui.frontend.constants.UIConstants;

import javax.swing.*;
import java.awt.*;

public class TextInput extends JEditorPane {

    public TextInput() {
        this(defaultStyle());
        setMinimumSize(new Dimension(100, 80));
    }

    public TextInput(Style style) {
        super();
        style.applyTo(this);
    }

    public static Style defaultStyle() {
        return new Style(
                UIConstants.FONT_TEXT,
                UIConstants.BLACK,
                UIConstants.WHITE,
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        );
    }

}
