package gui.frontend.components.base;

import gui.frontend.components.Style;
import gui.frontend.constants.UIConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class Photo extends JLabel {
    public static final Dimension SMALL_SQUARE = new Dimension(32, 32);
    public static final Dimension BIG_SQUARE = new Dimension(64, 64);
    public static final Dimension HUGE_SQUARE = new Dimension(256, 256);

    private final boolean isTemplateSize;

    public Photo() {
        this(null, null);
    }

    public Photo(Dimension dimension) {
        this(null, dimension);
    }

    public Photo(String base64img, Dimension dimension) {
        super("");
        setHorizontalTextPosition(JLabel.CENTER);
        setVerticalAlignment(JLabel.CENTER);
        setAlignmentX(JLabel.CENTER_ALIGNMENT);
        setAlignmentY(JLabel.CENTER_ALIGNMENT);
        if (dimension != null) {
            setMinimumSize(dimension);
            setPreferredSize(dimension);
            setMaximumSize(dimension);
        }
        isTemplateSize = dimension != null;
        updateImage(base64img);
    }

    public synchronized void updateImage(String base64Img) {
        if (base64Img == null || base64Img.isBlank())
            setIcon(null);
        else {
            try {
                byte[] btDataFile = Base64.getDecoder().decode(base64Img);
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(btDataFile));
                ImageIcon rawImageIcon = new ImageIcon(bufferedImage);

                Image image;
                // Resize image to fit.
                if (isTemplateSize) {
                    Dimension tDim = getPreferredSize();
                    image = rawImageIcon.getImage().getScaledInstance(tDim.width, tDim.height, Image.SCALE_DEFAULT);
                } else {
                    int iWidth = rawImageIcon.getIconWidth();
                    int iHeight = rawImageIcon.getIconHeight();

                    double a = HUGE_SQUARE.width * HUGE_SQUARE.height;
                    int tWidth = (int) Math.sqrt(a * iWidth / iHeight);
                    int tHeight = (int) a / tWidth;

                    image = rawImageIcon.getImage().getScaledInstance(tWidth, tHeight, Image.SCALE_DEFAULT);
                    setPreferredSize(new Dimension(tWidth, tHeight));
                }
                setIcon(new ImageIcon(image));
            } catch (IOException e) {
                // TODO: log.
                e.printStackTrace();
                setIcon(null);
            }
        }
        repaint();
    }

    public static Style defaultStyle() {
        return new Style(
                null,
                UIConstants.WHITE,
                UIConstants.WHITE,
                BorderFactory.createEmptyBorder()
        );
    }

}
