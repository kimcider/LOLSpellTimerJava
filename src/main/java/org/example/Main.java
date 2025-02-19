package org.example;

import org.example.connection.Connector;
import org.example.display.Board;
import org.example.display.StartPage;

import javax.swing.*;

public class Main extends JFrame {
    public static void main(String[] args) {
        StartPage startPage = new StartPage(() -> {
            // 버튼 클릭 후 실행될 코드
            Connector.getInstance(); // WebSocket 연결
            Board.getInstance(); // 새로운 게임 보드 생성
        });
    }
}
