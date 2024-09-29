package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static org.example.Board.*;

@Setter
@Getter
@JsonIgnoreProperties({"lineIcon", "flashIcon"})
public class Liner {
    private static int positionY = imageMargin;
    String name;
    Flash flash;

    JLabel lineIcon;
    public CounterLabel flashIcon;

    public Liner(){
        flash = new Flash();
    }

    public Liner(String name) {
        this.name = name;
        flash = new Flash();

        lineIcon = getImage(name + ".jpg", imageMargin, positionY);
        flashIcon = getCounterImage("flash.jpg", imageMargin + imageSize + imageMargin, positionY);
        positionY += imageSize + imageMargin;
    }

    public void startCount(){
        flashIcon.startCount();
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

        CounterLabel result = new CounterLabel(scaledIcon, this);

        result.setLocation(x, y);
        result.setSize(imageSize, imageSize);
        return result;
    }
}
