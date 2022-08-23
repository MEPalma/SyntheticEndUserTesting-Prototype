package common.utils;

import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Base64;

public class ProfileImageGen {
    public static final Dimension SQUARE = new Dimension(64, 64);

    public static String randomBase64SquareImage() {
        return randomBase64Image(SQUARE.width, SQUARE.height);
    }

    public static String randomBase64Image(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int a = (int) (Math.random() * 256);
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        int p = (a << 24) | (r << 16) | (g << 8) | b;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                img.setRGB(x, y, p);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (Exception ioe) {
            return null;
        }
    }
}
