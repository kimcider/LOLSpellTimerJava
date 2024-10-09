package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CodeInputWindow extends JDialog {
    private String code;

    public CodeInputWindow() {
        this.code = null; // 기본적으로 null로 초기화
        initUI();
    }

    private void initUI() {
        // 메인 패널 설정
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 0));
        panel.setBackground(new Color(0x3C3C3C));

        JLabel instructionLabel = new JLabel("코드 입력");
        instructionLabel.setForeground(Color.WHITE);
        instructionLabel.setBackground(new Color(0x3C3C3C));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(3, 20, 7, 20));

        JTextField codeInputField = new JTextField(20);
        codeInputField.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        codeInputField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        codeInputField.setBackground(new Color(0x2D2D2D));
        codeInputField.setForeground(Color.WHITE);

        JButton submitButton = new JButton("확인");
        submitButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        submitButton.setFocusPainted(false);
        submitButton.setBackground(new Color(0x3C3C3C));  // 버튼 배경 색상
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorder(BorderFactory.createEmptyBorder(3, 20, 3, 20));


        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputCode = codeInputField.getText();
                
                if(inputCode.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "코드를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(inputCode.length() != 20) {
                    JOptionPane.showMessageDialog(null, "잘못된 입장코드.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                code = inputCode; // 입력된 코드를 저장
                dispose(); // 창 닫기
            
            }
        });

        panel.add(instructionLabel, BorderLayout.NORTH);
        panel.add(codeInputField, BorderLayout.CENTER);
        panel.add(submitButton, BorderLayout.SOUTH);

        this.add(panel);

        // JDialog 크기 및 위치 설정
        this.setUndecorated(true);  // 기본 테두리 제거 (더 깔끔하게)
        this.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(0x3C3C3C), 2));  // 창 테두리 추가
        this.setSize(138, 130);
        this.setLocationRelativeTo(null);  // 화면 중앙에 배치
        this.setModal(true);  // 모달 창으로 설정
        this.setVisible(true);
    }

    // 입력된 코드를 반환하는 메소드
    public String getCode() {
        return code;
    }
}
