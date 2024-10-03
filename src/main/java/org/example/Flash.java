package org.example;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.example.connection.AbstractWebSocketConnector;

import javax.swing.*;
import java.awt.*;

import static org.example.Board.imageMargin;
import static org.example.Board.imageSize;
import static org.example.Liner.positionY;

@Getter
@Setter
@JsonIgnoreProperties({"mapper", "flashIcon", "on"})
public class Flash {
    private static ObjectMapper mapper = new ObjectMapper();
    public int flashCoolTime = 300; // 이거 200일때도 바뀌는거 테스트함수 만들기
    private int coolTime = 0;

    CounterLabel flashIcon;

    public Flash() {
    }

    public Flash(Liner liner, AbstractWebSocketConnector connector) {
        flashIcon = getCounterImage("flash.jpg", imageMargin + imageSize + imageMargin, positionY, liner, connector);
    }

    public boolean isOn() {
        return coolTime == 0;
    }

    public void on() {
        coolTime = 0;
    }

    public void off() {
        if (coolTime == 0) {
            coolTime = flashCoolTime;
        }
    }

    public void startCount(Liner liner) {
        flashIcon.repaint();

        flashIcon.stopTimer();
        flashIcon.setTimer(new Timer(1000, e -> {
            coolTime--;
            flashIcon.repaint();

            if (getCoolTime() <= 0) {
                on();
                liner.sendLinerStatus();
                flashIcon.stopTimer();
            }
        }));
        flashIcon.startTimer();
    }

    public void stopCount() {
        flashIcon.repaint();
        flashIcon.stopTimer();

    }

    private CounterLabel getCounterImage(String path, int x, int y, Liner liner, AbstractWebSocketConnector connector) {
        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource(path));
        Image scaledImage = imageIcon.getImage().getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        CounterLabel result = new CounterLabel(scaledIcon, this);

        result.setLocation(x, y);
        result.setSize(imageSize, imageSize);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        Flash other = (Flash) obj;
        if (flashCoolTime == other.flashCoolTime && isOn() == other.isOn()) {
            return true;
        }
        return false;
    }
}
