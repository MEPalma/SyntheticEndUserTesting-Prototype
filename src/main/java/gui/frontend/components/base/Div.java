package gui.frontend.components.base;

import gui.frontend.components.Style;
import gui.frontend.constants.UIConstants;

import javax.swing.*;
import java.awt.*;

public class Div extends JPanel {
    public Div() {
        this(new BorderLayout());
    }

    public Div(LayoutManager layoutManager) {
        this(layoutManager, defaultStyle());
    }

    public Div(LayoutManager layoutManager, Style style) {
        super(layoutManager);
        style.applyTo(this);
    }

    public static Style defaultStyle() {
        return new Style(
                null,
                UIConstants.BLACK,
                UIConstants.WHITE,
                BorderFactory.createEmptyBorder()
        );
    }

    public static Style defaultPaddedStyle() {
        var style = defaultStyle();
        style.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        return style;
    }

}
