package org.example;

import org.example.connection.Connector;

import javax.swing.*;

public class Main extends JFrame {
    public static void main(String[] args) {
        StartPage startPage = new StartPage(() -> {
            // 버튼 클릭 후 실행될 코드
            Connector.getInstance(); // WebSocket 연결
            System.out.println(Connector.getInstance().getServerURI());
            new Board(); // 새로운 게임 보드 생성
        });
    }
}
