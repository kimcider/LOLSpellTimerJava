package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.exit;

public class Board extends JWindow {
    public static Connector connector;

    static final int imageSize = 40;
    static final int imageMargin = 10;
    private Point initialClick = null;

    Map<String, Liner> linerList = new HashMap<String, Liner>();


    private ObjectMapper mapper = new ObjectMapper();

    public Board(Connector connector) {
        this.connector = connector;
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

        ArrayList<String> listNames = new ArrayList();
        listNames.add("top");
        listNames.add("jg");
        listNames.add("mid");
        listNames.add("bot");
        listNames.add("sup");

        for (int i = 0; i < listNames.size(); i++) {
            Liner liner = new Liner(listNames.get(i));
            linerList.put(listNames.get(i), liner);
            add(liner.lineIcon);
            add(liner.flashIcon);

            linerList.put(listNames.get(i), liner);
        }
        connector.setLinerList(linerList);


        repaint();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int windowX = getLocation().x;
                int windowY = getLocation().y;

                int xMoved = e.getX() - (initialClick != null ? initialClick.x : 0);
                int yMoved = e.getY() - (initialClick != null ? initialClick.y : 0);

                int newX = windowX + xMoved;
                int newY = windowY + yMoved;

                setLocation(newX, newY);
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

    public String getJsonLineList() throws JsonProcessingException {
        List newList = linerList.values().stream().toList();
        return mapper.writeValueAsString(newList);
    }
}
