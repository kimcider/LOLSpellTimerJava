package org.example;

import org.example.connection.Connector;
import org.example.display.Board;
import org.example.display.StartPage;

import javax.swing.*;

public class Main extends JFrame {
    public static void main(String[] args) {
        // Board가 setIconPosition()으로 레이아웃을 계산하기 전에 저장된 설정을 먼저 읽어야 한다
        Setting.load();

        StartPage startPage = new StartPage(() -> {
            // 버튼 클릭 후 실행될 코드
            Connector.getInstance(); // WebSocket 연결
            Board.getInstance(); // 새로운 게임 보드 생성
        });
    }
}
