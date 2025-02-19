package org.example.liner.spell;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

@Getter
@Setter
public class CounterLabel extends JLabel {
    ImageIcon icon;
    private Spell spell;
    private Timer timer;

    public CounterLabel(ImageIcon icon, Spell spell) {
        super(icon);
        this.icon = icon;
        this.spell = spell;
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

    private boolean temp = true;

    @Override
    public void paintComponent(Graphics g) {
        Color red = new Color(255, 0, 0, 180);
        Color black = new Color(0, 0, 0, 150);

        super.paintComponent(g);
        if (!spell.isOn()) {
            Graphics2D g2d = (Graphics2D) g;

            if (spell.getCoolTime() <= 20) {
                if (temp) {
                    g2d.setColor(black);
                } else {
                    g2d.setColor(red);
                }
            } else {
                g2d.setColor(black);
            }

            temp = !temp;

            g2d.fillRect(0, 0, getWidth(), getHeight());

            int remainingTime = spell.getCoolTime();
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
