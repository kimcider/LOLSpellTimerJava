package org.example;

public class Setting {
    public static boolean iconReverse = true;
    public static int imageSize = 40;

    public static final int imageMargin = imageSize / 4;
    public static final int smallImageMargin = imageMargin / 2;
    public static final int smallImageSize = (imageSize / 2) - (smallImageMargin / 2);

    public static int iconPositionY;

    public static final int boardWidth = (imageSize + imageMargin) * 2 + imageMargin + smallImageSize;
    public static final int boardHeight = (imageSize + imageMargin) * 5 + imageMargin;
    public static int lineIconX;
    public static int cosmicInsightIconX;
    public static int ionianBootsIconX;
    public static int spellIconX;
}
