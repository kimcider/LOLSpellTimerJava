package org.example.display;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import static org.example.Setting.*;

public class ReloadSpellPage extends JWindow {
    private static ReloadSpellPage instance = null;

    JPanel panel;
    @Getter
    @Setter
    private String spellName;
    @Setter
    Runnable runnable;

    HashMap<JLabel, String> spellIcons = new HashMap<>();

    private ReloadSpellPage() {
        initUI();

        initSpellIcon();

        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event instanceof MouseEvent) {
                    MouseEvent mouseEvent = (MouseEvent) event;

                    // 마우스 클릭 시점에서 처리
                    if (mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
                        Point mousePoint = mouseEvent.getLocationOnScreen();

                        // 클릭한 위치가 JWindow 내부가 아닌 경우
                        if (!getBounds().contains(mousePoint)) {
                            setVisible(false); // JWindow 닫기
                            dispose(); // JWindow 리소스 해제
                        }
                    }
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK);
    }

    public static ReloadSpellPage getInstance() {
        if (instance == null) {
            instance = new ReloadSpellPage();
        }
        instance.setVisible(true);
        return instance;
    }

    private void initUI() {
        setSize(reloadBoardWidth, reloadBoardHeight);
        setBackground(new Color(0, 0, 0, 100));
        setLayout(null);
        setAlwaysOnTop(true);


        setVisible(true);

        panel = new JPanel();
        panel.setLayout(null);
        panel.setSize(boardWidth, boardHeight);
        panel.setBackground(new Color(0, 0, 0, 0));
        setContentPane(panel);

    }

    private void initSpellIcon() {
        spellIcons.put(getIcon("clean.jpg", reloadIconPositionX, reloadIconPositionY), "Cleanse");
        spellIcons.put(getIcon("exhaustion.jpg", reloadIconPositionX, reloadIconPositionY), "Exhaustion");
        spellIcons.put(getIcon("flash.jpg", reloadIconPositionX, reloadIconPositionY), "Flash");
        spellIcons.put(getIcon("ghost.jpg", reloadIconPositionX, reloadIconPositionY), "Ghost");
        spellIcons.put(getIcon("heal.jpg", reloadIconPositionX, reloadIconPositionY), "Heal");

        reloadIconPositionX = reloadImageMargin;
        reloadIconPositionY += reloadImageSize + reloadImageMargin;

        spellIcons.put(getIcon("tp.jpg", reloadIconPositionX, reloadIconPositionY), "Teleport");
        spellIcons.put(getIcon("superTp.jpg", reloadIconPositionX, reloadIconPositionY), "UpgradeTeleport");
        spellIcons.put(getIcon("ignite.jpg", reloadIconPositionX, reloadIconPositionY), "Ignite");
        spellIcons.put(getIcon("barrier.jpg", reloadIconPositionX, reloadIconPositionY), "Barrier");

        for (JLabel label : spellIcons.keySet()) {
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        spellName = spellIcons.get(label);
                        runnable.run();
                        setVisible(false);
                    }
                }
            });
        }
    }

    private JLabel getIcon(String path, int x, int y) {
        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource(path));
        Image scaledImage = imageIcon.getImage().getScaledInstance(reloadImageSize, reloadImageSize, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel result = new JLabel(scaledIcon);
        result.setLocation(0, 0);
        result.setLocation(x, y);
        result.setSize(reloadImageSize, reloadImageSize);
        panel.add(result);

        reloadIconPositionX += reloadImageSize + reloadImageMargin;

        return result;
    }


}
