package org.example;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static org.example.Board.*;

public class Line {
    private static int positionY = 10;

    JLabel lineIcon;
    @JsonIgnore
    public CounterLabel flashIcon;
    public Liner liner;

    public Line(String name) {
        liner = new Liner(name);

        lineIcon = getImage(name + ".jpg", 10, positionY);
        flashIcon = getCounterImage("flash.jpg", 10 + imageSize + imageMargin, positionY);
        positionY += imageSize + imageMargin;

        flashIcon.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                connector.startCountFlash(lineList.get(name));
                try {
                    connector.useFlash(liner);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private JLabel getImage(String path, int x, int y) {
        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource(path));
        Image scaledImage = imageIcon.getImage().getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel result = new JLabel(scaledIcon);

        result.setLocation(x, y);
        result.setSize(imageSize, imageSize);
        return result;
    }

    private CounterLabel getCounterImage(String path, int x, int y) {
        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource(path));
        Image scaledImage = imageIcon.getImage().getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        CounterLabel result = new CounterLabel(scaledIcon, liner.flash);

        result.setLocation(x, y);
        result.setSize(imageSize, imageSize);
        return result;
    }
}
