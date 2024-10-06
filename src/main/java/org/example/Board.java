package org.example;

import org.example.connection.Connector;
import org.example.liner.Liner;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;
import static org.example.Setting.*;

public class Board extends JWindow {
    private Connector connector;


    private Point initialClick = null;

    public static Map<String, Liner> linerList = new HashMap<String, Liner>();

    private void setIconPosition() {
        iconPositionY = imageMargin;
        if (!iconReverse) {
            lineIconX = imageMargin;
            cosmicInsightIconX = lineIconX + imageSize + smallImageMargin;
            ionianBootsIconX = lineIconX + imageSize + smallImageMargin;
            spellIconX = cosmicInsightIconX + smallImageSize + smallImageMargin;
        } else {
            spellIconX = imageMargin;
            cosmicInsightIconX = spellIconX + imageSize + smallImageMargin;
            ionianBootsIconX = spellIconX + imageSize + smallImageMargin;
            lineIconX = cosmicInsightIconX + smallImageSize + smallImageMargin;
        }
    }

    public Board() {
        connector = Connector.getInstance();
        setBackground(new Color(0, 0, 0, 100));
        setLayout(null);
        setSize(boardWidth, boardHeight);
        setAlwaysOnTop(true);
        setLocationRelativeTo(null);

        setIconPosition();

        if (SystemTray.isSupported()) {
            setVisible(true);
            SystemTray tray = SystemTray.getSystemTray();
            TrayIcon trayIcon = getTrayIcon();

            try {
                tray.add(trayIcon);  // 트레이에 아이콘 추가
            } catch (AWTException e) {
                exit(0);
            }
        }

        ArrayList<String> listNames = new ArrayList<>();
        listNames.add("top");
        listNames.add("jg");
        listNames.add("mid");
        listNames.add("bot");
        listNames.add("sup");

        for (String name : listNames) {
            Liner liner = new Liner(name, connector);
            linerList.put(name, liner);
            add(liner.getLineIcon());
            add(liner.getCosmicInsightIcon());
            add(liner.getIonianBootsIcon());
            add(liner.getFlash().getSpellIcon());
        }
        connector.setLinerList(linerList);

        repaint();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    initialClick = e.getPoint();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // 왼쪽 버튼이 눌린 상태에서만 드래그 동작 실행
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int windowX = getLocation().x;
                    int windowY = getLocation().y;

                    int xMoved = e.getX() - (initialClick != null ? initialClick.x : 0);
                    int yMoved = e.getY() - (initialClick != null ? initialClick.y : 0);

                    int newX = windowX + xMoved;
                    int newY = windowY + yMoved;

                    setLocation(newX, newY);
                }
            }
        });
    }

    private @NotNull TrayIcon getTrayIcon() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/flash.jpg"));  // 아이콘 경로

        Image image = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);


        TrayIcon trayIcon = new TrayIcon(image, "LoLSpellTimer");
        // 트레이 아이콘의 팝업 메뉴
        PopupMenu popupMenu = new PopupMenu();
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connector.close();
                exit(0);  // 종료 메뉴
            }
        });
        popupMenu.add(exitItem);
        trayIcon.setPopupMenu(popupMenu);
        return trayIcon;
    }
}
