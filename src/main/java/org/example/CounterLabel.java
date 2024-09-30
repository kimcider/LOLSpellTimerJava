package org.example;

import org.example.connection.AbstractWebSocketConnector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class CounterLabel extends JLabel {
    private AbstractWebSocketConnector connector;
    ImageIcon icon;
    private Liner liner;
    private Timer timer;

    public CounterLabel(ImageIcon icon, Liner liner, AbstractWebSocketConnector connector) {
        super(icon);
        this.connector = connector;
        this.icon = icon;
        this.liner = liner;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    startCount();
                    try {
                        liner.getFlash().sendFlashStatus(liner, connector);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    public void startCount() {
        if (liner.getFlash().isOn()) {
            liner.getFlash().off();
            liner.getFlash().setCoolTime(liner.getFlash().flashCoolTime);

            liner.getFlashIcon().repaint();
            if (liner.getFlashIcon().timer != null) {
                liner.getFlashIcon().timer.stop();
            }

            liner.getFlashIcon().timer = new Timer(1000, e -> {
                liner.getFlash().setCoolTime(liner.getFlash().getCoolTime() - 1);
                liner.getFlashIcon().repaint();

                if (liner.getFlash().getCoolTime() <= 0) {
                    liner.getFlash().on();
                    try {
                        liner.getFlash().sendFlashStatus(liner, connector);
//                        connector.flashOn(liner);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    if (liner.getFlashIcon().timer != null) {
                        liner.getFlashIcon().timer.stop();
                    }
                }

            });

            liner.getFlashIcon().timer.start();
        } else {
            liner.getFlash().on();
            liner.getFlashIcon().repaint();
            liner.getFlashIcon().timer.stop();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!liner.getFlash().isOn()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            int remainingTime = liner.getFlash().getCoolTime();
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
