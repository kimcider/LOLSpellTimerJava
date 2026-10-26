package org.example.display;

import org.example.connection.Connector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.security.SecureRandom;

import static java.lang.System.exit;

public class StartPage extends JWindow {
    private final Runnable onStart;

    String buttonTitle1 = "솔로 랭크";
    String buttonTitle2 = "방 생성";
    String buttonTitle3 = "방 입장";
    String buttonTitle4 = "종료";

    public StartPage(Runnable onStart) {
        this.onStart = onStart;
        setAlwaysOnTop(true);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10)); // 4개의 버튼을 세로로 배치, 버튼 간 간격 추가
        panel.setBackground(Color.GRAY); // 배경색 설정

        JButton soloRankButton = createStyledButton(buttonTitle1);
        JButton newGameButton = createStyledButton(buttonTitle2);
        JButton codeInputButton = createStyledButton(buttonTitle3);
        JButton exitButton = createStyledButton(buttonTitle4);

        soloRankButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        newGameButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        codeInputButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        exitButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        soloRankButton.addActionListener(new ButtonClickListener());
        newGameButton.addActionListener(new ButtonClickListener());
        codeInputButton.addActionListener(new ButtonClickListener());
        exitButton.addActionListener(new ButtonClickListener());

        panel.add(soloRankButton);
        panel.add(newGameButton);
        panel.add(codeInputButton);
        panel.add(exitButton);

        this.add(panel);

        // ESC로도 종료할 수 있게 한다. JWindow라 작업표시줄 버튼도 제목표시줄 X도 없고,
        // 트레이 아이콘은 모드를 고른 뒤 Board가 만들기 때문에 이 화면에는 종료 수단이 따로 없다.
        getRootPane().registerKeyboardAction(
                e -> exit(0),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        this.setSize(120, 137);
        this.setLocationRelativeTo(null); // 화면 중앙에 배치
        this.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0x3D3D3D));
        button.setForeground(Color.WHITE); // 글자색 설정
        button.setFocusPainted(false); // 버튼 포커스 표시 제거
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // 버튼 패딩 설정
        return button;
    }

    // 버튼 클릭 리스너 클래스
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            String buttonText = source.getText();

            // 서버에 접속하기 전에 처리해야 하므로 다른 분기보다 먼저 검사한다
            if (buttonTitle4.equals(buttonText)) {
                exit(0);
            }

            String hashValue = "";
//            Connector.serverURI = "localhost:8080";
            Connector.serverURI = "168.138.32.227:8080";
            if (buttonTitle1.equals(buttonText)) {
                Connector.serverURI = "localhost:8089";
            } else if (buttonTitle2.equals(buttonText)) {
                hashValue = getHashValue();
            } else if (buttonTitle3.equals(buttonText)) {
                CodeInputWindow codeInputWindow = new CodeInputWindow();
                hashValue = codeInputWindow.getCode();

                if (hashValue == null || hashValue.isEmpty()) {
                    exit(0);
                }
            }
            Connector.hashValue = hashValue;
            dispose();
            onStart.run();
        }
    }

    public String getHashValue() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        int length = 20;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
