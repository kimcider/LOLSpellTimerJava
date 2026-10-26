package org.example;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

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

    /** 저장된 창 위치가 없음을 나타내는 값 */
    public static final int UNSET = Integer.MIN_VALUE;

    /** 오버레이 창의 화면상 위치. 아이콘 배치용 iconPositionX/Y와는 다른 값이다. */
    public static int windowX = UNSET;
    public static int windowY = UNSET;

    private static final Path CONFIG_FILE =
            Paths.get(System.getProperty("user.home"), ".lolspelltimer", "settings.properties");

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

    /**
     * 사용자 홈의 설정 파일에서 크기/좌우반전/창 위치를 읽어온다.
     * 파일이 없거나 깨져 있으면 기본값을 그대로 쓴다. 첫 실행이 여기에 해당한다.
     */
    public static void load() {
        if (!Files.isReadable(CONFIG_FILE)) {
            return;
        }
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(CONFIG_FILE)) {
            properties.load(in);
        } catch (Exception e) {
            return;
        }

        imageSize = parseInt(properties.getProperty("imageSize"), imageSize);
        iconReverse = parseBoolean(properties.getProperty("iconReverse"), iconReverse);
        windowX = parseInt(properties.getProperty("windowX"), UNSET);
        windowY = parseInt(properties.getProperty("windowY"), UNSET);
    }

    /**
     * 현재 설정을 사용자 홈에 저장한다.
     * 저장에 실패해도 프로그램 동작에는 영향이 없어야 하므로 예외를 삼킨다.
     */
    public static void save() {
        Properties properties = new Properties();
        properties.setProperty("imageSize", String.valueOf(imageSize));
        properties.setProperty("iconReverse", String.valueOf(iconReverse));
        if (windowX != UNSET && windowY != UNSET) {
            properties.setProperty("windowX", String.valueOf(windowX));
            properties.setProperty("windowY", String.valueOf(windowY));
        }

        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            try (OutputStream out = Files.newOutputStream(CONFIG_FILE)) {
                properties.store(out, "LoLSpellTimer user settings");
            }
        } catch (Exception e) {
            // 읽기 전용 경로 등으로 저장이 실패해도 무시한다
        }
    }

    /**
     * 저장된 위치가 현재 모니터 구성에서 실제로 보이는 위치인지 검사한다.
     * 모니터를 빼거나 해상도를 바꾼 뒤에 창이 화면 밖에 뜨는 것을 막는다.
     */
    public static boolean isVisibleOnScreen(int x, int y) {
        if (x == UNSET || y == UNSET) {
            return false;
        }
        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for (GraphicsDevice device : devices) {
            Rectangle bounds = device.getDefaultConfiguration().getBounds();
            if (bounds.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    static Path getConfigFile() {
        return CONFIG_FILE;
    }

    private static int parseInt(String value, int fallback) {
        if (value == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static boolean parseBoolean(String value, boolean fallback) {
        if (value == null) {
            return fallback;
        }
        return Boolean.parseBoolean(value.trim());
    }
}
