package com.dither.demo;

import javafx.concurrent.Task;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Color extends Task<Void> {
    private int r;
    private int g;
    private int b;
    private List<File> files;
    private String path;

    public Color(List<File> files) {
        this.files = files;
    }

    public Color(int c) {
        java.awt.Color color = new java.awt.Color(c);
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
    }

    public Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color(List<File> allFiles, String path) {
        this.files = allFiles;
        this.path = path;
    }

    public Color add(Color color) {
        return new Color(r + color.r, g + color.g, b + color.b);
    }

    public int reduce(int c) {
        return Math.max(0, Math.min(255, c));
    }

    public int diff(Color color) {
        int Rdiff = color.r - r;
        int Gdiff = color.g - g;
        int Bdiff = color.b - b;
        int distanceSquared = Rdiff * Rdiff + Gdiff * Gdiff + Bdiff * Bdiff;
        return distanceSquared;
    }

    public Color mul(double d) {
        return new Color((int) (d * r), (int) (d * g), (int) (d * b));
    }

    public Color sub(Color o) {
        return new Color(r - o.r, g - o.g, b - o.b);
    }

    public java.awt.Color toColor() {
        return new java.awt.Color(reduce(r), reduce(g), reduce(b));
    }

    public int toRGB() {
        return toColor().getRGB();
    }

    private static Color findClosestPaletteColor(Color c, Color[] palette) {
        Color closest = palette[0];

        for (Color n : palette) {
            if (n.diff(c) < closest.diff(c)) {
                closest = n;
            }
        }
        return closest;
    }

    public static BufferedImage floydSteinbergDithering(BufferedImage img) throws IOException {

        Color[] palette = new Color[]{
                new Color(0, 0, 0), // black
                new Color(0, 0, 255), // green
                new Color(0, 255, 0), // blue
                new Color(0, 255, 255), // cyan
                new Color(255, 0, 0), // red
                new Color(255, 0, 255), // purple
                new Color(255, 255, 0), // yellow
                new Color(255, 255, 255)  // white
        };

        int w = img.getWidth();
        int h = img.getHeight();

        Color[][] d = new Color[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                d[y][x] = new Color(img.getRGB(x, y));
            }
        }

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {

                Color oldColor = d[y][x];
                Color newColor = findClosestPaletteColor(oldColor, palette);
                img.setRGB(x, y, newColor.toColor().getRGB());

                Color err = oldColor.sub(newColor);

                if (x + 1 < w) {
                    d[y][x + 1] = d[y][x + 1].add(err.mul(7.0 / 16));
                }

                if (x - 1 >= 0 && y + 1 < h) {
                    d[y + 1][x - 1] = d[y + 1][x - 1].add(err.mul(3.0 / 16));
                }

                if (y + 1 < h) {
                    d[y + 1][x] = d[y + 1][x].add(err.mul(5.0 / 16));
                }

                if (x + 1 < w && y + 1 < h) {
                    d[y + 1][x + 1] = d[y + 1][x + 1].add(err.mul(1.0 / 16));
                }
            }
        }
        return img;
    }

    @Override
    protected Void call() throws Exception {
        int count = 0;
        for (int i = 0; i < files.size(); i++) {
            String finalName = path + "Dithered" + files.get(i).getName();
            BufferedImage dithered = floydSteinbergDithering(ImageIO.read(new File(String.valueOf(files.get(i)))));
            floydSteinbergDithering(dithered);
            save(dithered,"PNG",new File(finalName));
            updateProgress(count, files.size() - 1);
            count++;
        }

        return null;
    }

    private void save(BufferedImage image, String extension,File savedFile) throws IOException {
        ImageIO.write(image, extension, new File(String.valueOf(savedFile)));
    }
}





