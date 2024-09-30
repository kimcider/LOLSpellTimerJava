package org.example;

import org.example.connection.AbstractWebSocketConnector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URISyntaxException;


public class CounterLabel extends JLabel {
    private AbstractWebSocketConnector connector;
    ImageIcon icon;
    Liner liner;
    Timer timer;

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
                        // TODO: liner.useFlash(); 이렇게 하면안되고, connector.useFlash(liner)로 하면 되네...
                        // 이거 왜이러지? 이걸 검출할 수 있는 테스트도 작성하기.
                        // 이거 단순히 liner.useFlash가 구현이 안되어있어서그렇다....

                         liner.useFlash();
                        //connector.useFlash(liner);
//                        connector.useFlash(liner);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    public void startCount() {
        if (liner.flash.on) {
            liner.flash.on = false;
            liner.flash.coolTime = liner.flash.flashCoolTime;

            liner.flashIcon.repaint();
            if (liner.flashIcon.timer != null) {
                liner.flashIcon.timer.stop();
            }

            liner.flashIcon.timer = new Timer(1000, e -> {
                liner.flash.coolTime -= 1;
                liner.flashIcon.repaint();

                if (liner.flash.coolTime <= 0) {
                    liner.flash.on = true;
                    try {
                        liner.flashOn();
//                        connector.flashOn(liner);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    if (liner.flashIcon.timer != null) {
                        liner.flashIcon.timer.stop();
                    }
                }

            });

            liner.flashIcon.timer.start();
        } else {
            liner.flash.on = true;
            liner.flashIcon.repaint();
            liner.flashIcon.timer.stop();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(!liner.flash.on){
            Graphics2D g2d = (Graphics2D)g;
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            int remainingTime = liner.flash.coolTime;
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
