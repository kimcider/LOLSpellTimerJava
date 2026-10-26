package org.example.display;

import org.example.Setting;
import org.example.connection.Connector;
import org.example.liner.Liner;

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
    private static Board board = null;
    private Connector connector;
    ArrayList<String> listNames = new ArrayList<>();
    JPanel contentPane = new JPanel();


    private Point initialClick = null;

    public static Map<String, Liner> linerList = new HashMap<String, Liner>();

    public static Board getInstance() {
        if (board == null) {
            board = new Board();
        }
        return board;
    }

    private Board() {
        setIconPosition();

        connector = Connector.getInstance();
        setBackground(new Color(0, 0, 0, 100));
        setLayout(null);
        setSize(boardWidth, boardHeight);
        setAlwaysOnTop(true);
        restoreLocation();

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
            contentPane.add(liner.getSpell1().getSpellIcon());
            contentPane.add(liner.getSpell2().getSpellIcon());
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

            @Override
            public void mouseReleased(MouseEvent e) {
                // 드래그 중에는 매 이벤트마다 저장하지 않고, 손을 뗐을 때 한 번만 저장한다
                if (SwingUtilities.isLeftMouseButton(e)) {
                    saveLocation();
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

    private TrayIcon getTrayIcon() {
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
        exitItem.addActionListener(e -> {
            saveLocation();
            connector.close();
            exit(0);  // 종료 메뉴
        });

        popupMenu.add(exitItem);
        trayIcon.setPopupMenu(popupMenu);
        return trayIcon;
    }

    private void repositionBoard() {
        Setting.iconReverse = !Setting.iconReverse;
        reloadBoard();
        Setting.save();
    }

    private void resizeBoard(int size) {
        Setting.imageSize = size;
        reloadBoard();
        Setting.save();
    }

    /** 저장된 위치가 현재 모니터 구성에서 보이는 자리면 복원하고, 아니면 화면 중앙에 띄운다. */
    private void restoreLocation() {
        if (Setting.isVisibleOnScreen(Setting.windowX, Setting.windowY)) {
            setLocation(Setting.windowX, Setting.windowY);
        } else {
            setLocationRelativeTo(null);
        }
    }

    private void saveLocation() {
        Setting.windowX = getLocation().x;
        Setting.windowY = getLocation().y;
        Setting.save();
    }

    public void reloadBoard() {
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
            contentPane.add(liner.getSpell1().getSpellIcon());
            contentPane.add(liner.getSpell2().getSpellIcon());
            iconPositionY += imageSize + imageMargin;
        }
        setContentPane(contentPane);
        revalidate();
        repaint();
    }
}
