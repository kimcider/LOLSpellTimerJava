package org.example.display;

import org.example.connection.Connector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;

import static java.lang.System.exit;

public class StartPage extends JWindow {
    private final Runnable onStart;

    String buttonTitle1 = "솔로 랭크";
    String buttonTitle2 = "방 생성";
    String buttonTitle3 = "방 입장";

    public StartPage(Runnable onStart) {
        this.onStart = onStart;
        setAlwaysOnTop(true);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10)); // 3개의 버튼을 세로로 배치, 버튼 간 간격 추가
        panel.setBackground(Color.GRAY); // 배경색 설정

        JButton soloRankButton = createStyledButton(buttonTitle1);
        JButton newGameButton = createStyledButton(buttonTitle2);
        JButton codeInputButton = createStyledButton(buttonTitle3);

        soloRankButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        newGameButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        codeInputButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        soloRankButton.addActionListener(new ButtonClickListener());
        newGameButton.addActionListener(new ButtonClickListener());
        codeInputButton.addActionListener(new ButtonClickListener());

        panel.add(soloRankButton);
        panel.add(newGameButton);
        panel.add(codeInputButton);

        this.add(panel);

        this.setSize(120, 100);
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
            String hashValue = "";
//            Connector.serverURI = "localhost:8080";
            Connector.serverURI = "ec2-13-124-87-54.ap-northeast-2.compute.amazonaws.com:8080";
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
