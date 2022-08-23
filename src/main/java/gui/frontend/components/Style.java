package gui.frontend.components;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Style {
    private Font font;
    private Color foreground;
    private Color background;
    private Border border;

    public Style() {
    }

    public Style(Font font, Color foreground, Color background, Border border) {
        this.font = font;
        this.foreground = foreground;
        this.background = background;
        this.border = border;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border border) {
        this.border = border;
    }

    public void applyTo(JComponent component) {
        var background = this.getBackground();
        if (background != null)
            component.setBackground(background);
        var foreground = this.getForeground();
        if (foreground != null)
            component.setForeground(foreground);
        var font = this.getFont();
        if (font != null)
            component.setFont(font);
        var border = this.getBorder();
        if (border != null)
            component.setBorder(border);
        component.repaint();
        component.revalidate();
    }
}
