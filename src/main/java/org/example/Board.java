package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.connection.Connector;
import org.example.liner.Liner;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
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
    private static ObjectMapper mapper = new ObjectMapper();
    private Connector connector;
    ArrayList<String> listNames = new ArrayList<>();
    JPanel contentPane = new JPanel();


    private Point initialClick = null;

    public static Map<String, Liner> linerList = new HashMap<String, Liner>();

    private void setIconPosition() {
        imageMargin = imageSize / 4;
        smallImageMargin = imageMargin / 2;
        smallImageSize = (imageSize / 2) - (smallImageMargin / 2);

        boardWidth = (imageSize + imageMargin) * 2 + imageMargin + smallImageSize;
        boardHeight = (imageSize + imageMargin) * 5 + imageMargin;

        iconPositionY = imageMargin;
        if (!iconReverse) {
            lineIconX = imageMargin;
            coolTimeReducer = lineIconX + imageSize + smallImageMargin;
            spellIconX = coolTimeReducer + smallImageSize + smallImageMargin;
        } else {
            spellIconX = imageMargin;
            coolTimeReducer = spellIconX + imageSize + smallImageMargin;
            lineIconX = coolTimeReducer + smallImageSize + smallImageMargin;
        }
    }

    public Board() {
        setIconPosition();

        connector = Connector.getInstance();
        setBackground(new Color(0, 0, 0, 100));
        setLayout(null);
        setSize(boardWidth, boardHeight);
        setAlwaysOnTop(true);
        setLocationRelativeTo(null);

        contentPane.setLayout(null);
        contentPane.setSize(boardWidth, boardHeight);
        contentPane.setBackground(new Color(0, 0, 0, 0));
        setContentPane(contentPane);


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


        listNames.add("top");
        listNames.add("jg");
        listNames.add("mid");
        listNames.add("bot");
        listNames.add("sup");

        for (String name : listNames) {
            Liner liner = new Liner(name, connector);
            linerList.put(name, liner);
            contentPane.add(liner.getLineIcon());
            contentPane.add(liner.getCosmicInsightIcon());
            contentPane.add(liner.getIonianBootsIcon());
            contentPane.add(liner.getFlash().getSpellIcon());
            iconPositionY += imageSize + imageMargin;
        }
        connector.setLinerList(linerList);
        repaint();

        try {
            //여기 try-catch 없으면 서버와 연결 안됐을때(솔랭모드일때) 오버레이가 움직이지 않음
            connector.getConnection().send(Connector.getInstance().wrapMethodJson("getLinerStatus", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        PopupMenu popupMenu = new PopupMenu();


        MenuItem getHashValue = new MenuItem("입장 코드 복사하기");
        getHashValue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hashValue = Connector.hashValue;

                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable transferable = new StringSelection(hashValue);
                clipboard.setContents(transferable, null);
            }
        });

        popupMenu.add(getHashValue);

        popupMenu.addSeparator();

        MenuItem makeBig = new MenuItem("크게");
        makeBig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resizeBoard(50);
            }
        });
        popupMenu.add(makeBig);

        MenuItem makeSmall = new MenuItem("작게");
        makeSmall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resizeBoard(40);
            }
        });
        popupMenu.add(makeSmall);

        MenuItem reverse = new MenuItem("좌우반전");
        reverse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repositionBoard();
            }
        });
        popupMenu.add(reverse);

        popupMenu.addSeparator();

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

    private void repositionBoard() {
        Setting.iconReverse = !Setting.iconReverse;
        reloadBoard();
    }

    private void resizeBoard(int size) {
        Setting.imageSize = size;
        reloadBoard();
    }

    private void reloadBoard() {
        setIconPosition();
        setSize(boardWidth, boardHeight);

        contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setSize(boardWidth, boardHeight);
        contentPane.setBackground(new Color(0, 0, 0, 0));

        for (String name : listNames) {
            Liner liner = linerList.get(name);
            liner.reloadIcon();
            contentPane.add(liner.getLineIcon());
            contentPane.add(liner.getCosmicInsightIcon());
            contentPane.add(liner.getIonianBootsIcon());
            contentPane.add(liner.getFlash().getSpellIcon());
            iconPositionY += imageSize + imageMargin;
        }
        setContentPane(contentPane);
        revalidate();
        repaint();
    }
}
