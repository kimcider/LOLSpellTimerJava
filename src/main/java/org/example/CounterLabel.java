package org.example;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

@Getter
@Setter
public class CounterLabel extends JLabel {
    ImageIcon icon;
    private Flash flash;
    private Timer timer;

    public CounterLabel(ImageIcon icon, Flash flash) {
        super(icon);
        this.icon = icon;
        this.flash = flash;
    }


    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    public void startTimer() {
        if (timer != null) {
            timer.start();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!flash.isOn()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            int remainingTime = flash.getCoolTime();
            if (remainingTime > 0) {
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 20));
                FontMetrics metrics = g2d.getFontMetrics();
                String text = String.valueOf(remainingTime);
                int x = (getWidth() - metrics.stringWidth(text)) / 2;
                int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
                g2d.drawString(text, x, y);

            }
        }
    }
}
