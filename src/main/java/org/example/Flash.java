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
@JsonIgnoreProperties({"mapper", "flashCoolTime", "flashIcon"})
public class Flash {
    private static ObjectMapper mapper = new ObjectMapper();
    public static final int flashCoolTime = 300;
    private int coolTime = flashCoolTime;
    private boolean on = true;

    CounterLabel flashIcon;

    public Flash() {
    }

    public Flash(Liner liner, AbstractWebSocketConnector connector) {
        flashIcon = getCounterImage("flash.jpg", imageMargin + imageSize + imageMargin, positionY, liner, connector);
    }

    public void on() {
        on = true;
    }

    public void off() {
        on = false;
    }

    public void startCount(Liner liner) {
        if (on) {
            off();
            setCoolTime(flashCoolTime);

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
        } else {
            on();
            flashIcon.repaint();
            flashIcon.stopTimer();
        }
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
        if (on == other.on && coolTime == other.coolTime) {
            return true;
        }
        return false;
    }
}
