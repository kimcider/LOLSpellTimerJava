package org.example;

import javax.swing.*;
import java.awt.*;

public class CounterLabel extends JLabel {
    ImageIcon icon;
    Flash flash;
    Timer timer;

    public CounterLabel(ImageIcon icon, Flash flash) {
        super(icon);
        this.icon = icon;
        this.flash = flash;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(!flash.on){
            Graphics2D g2d = (Graphics2D)g;
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            int remainingTime = flash.coolTime;
            if(remainingTime > 0){
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
