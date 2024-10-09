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
        panel.setLayout(new BorderLayout());

        JLabel instructionLabel = new JLabel("코드를 입력하세요:");
        JTextField codeInputField = new JTextField(20); // 코드 입력 필드

        JButton submitButton = new JButton("확인");
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
        this.setModal(true); // 모달로 설정하여 다른 창을 클릭할 수 없게 함
        this.setSize(300, 150);
        this.setLocationRelativeTo(null); // 화면 중앙에 배치
        this.setVisible(true);
    }

    // 입력된 코드를 반환하는 메소드
    public String getCode() {
        return code;
    }
}
