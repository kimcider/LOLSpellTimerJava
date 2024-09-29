package org.example;

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

public class Board extends JWindow {
    public static Connector connector;

    static final int imageSize = 40;
    static final int imageMargin = 10;
    private Point initialClick = null;

    Map<String, Liner> linerList = new HashMap<String, Liner>();

    public Board(Connector connector) {
        Board.connector = connector;
        setBackground(new Color(0, 0, 0, 100));
        setLayout(null);
        setSize((imageSize + imageMargin) * 2 + imageMargin, (imageSize + imageMargin) * 5 + imageMargin);
        setAlwaysOnTop(true);
        setLocationRelativeTo(null);

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
            Liner liner = new Liner(name);
            linerList.put(name, liner);
            add(liner.lineIcon);
            add(liner.flashIcon);
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
