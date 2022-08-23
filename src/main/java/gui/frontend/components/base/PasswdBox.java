package gui.frontend.components.base;

import gui.frontend.components.Style;

import javax.swing.*;
import java.awt.*;

public class PasswdBox extends JPasswordField {

    public PasswdBox() {
        this(defaultStyle());
        setPreferredSize(new Dimension(100, 80));
    }

    public PasswdBox(Style style) {
        style.applyTo(this);
    }

    public static Style defaultStyle() {
        return TextBox.defaultStyle();
    }

}
