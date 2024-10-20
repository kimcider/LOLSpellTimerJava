package org.example;

public class Setting {
    public static boolean iconReverse = false;
    public static int imageSize = 40;
    public static int reloadImageSize = 1;

    public static int imageMargin = 1;
    public static int smallImageMargin = 1;
    public static int smallImageSize = 1;
    public static int reloadImageMargin = 1;

    public static int iconPositionX;
    public static int iconPositionY;

    public static int boardWidth;
    public static int boardHeight;
    public static int reloadBoardWidth;
    public static int reloadBoardHeight;

    public static int coolTimeReducer;
    public static int spell1IconX;
    public static int spell2IconX;

    public static int reloadIconPositionX;
    public static int reloadIconPositionY;

    public static void setIconPosition() {
        imageMargin = imageSize / 4;
        smallImageMargin = imageMargin / 2;
        smallImageSize = (imageSize / 2) - (smallImageMargin / 2);

        boardWidth = (imageSize + imageMargin) * 3 + imageMargin + smallImageSize;
        boardHeight = (imageSize + imageMargin) * 5 + imageMargin;

        iconPositionY = imageMargin;
        if (!iconReverse) {
            iconPositionX = imageMargin;
            coolTimeReducer = iconPositionX + imageSize + smallImageMargin;
            spell1IconX = coolTimeReducer + smallImageSize + smallImageMargin;
            spell2IconX = spell1IconX + imageSize + imageMargin;
        } else {
            spell2IconX = imageMargin;
            spell1IconX = imageMargin + imageSize + imageMargin;
            coolTimeReducer = spell1IconX + imageSize + smallImageMargin;
            iconPositionX = coolTimeReducer + smallImageSize + smallImageMargin;
        }

        reloadImageSize = imageSize * 3 / 4;
        reloadImageMargin = reloadImageSize / 6;
        reloadBoardWidth = reloadImageSize * 5 + reloadImageMargin * 6;
        reloadBoardHeight = reloadImageSize * 2 + reloadImageMargin * 3;
        reloadIconPositionX = reloadImageMargin;
        reloadIconPositionY = reloadImageMargin;
    }

}
