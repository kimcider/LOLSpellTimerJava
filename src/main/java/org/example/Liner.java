package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.example.connection.AbstractWebSocketConnector;
import org.example.connection.Connector;

import javax.swing.*;
import java.awt.*;

import static org.example.Board.imageMargin;
import static org.example.Board.imageSize;

@Setter
@Getter
@JsonIgnoreProperties({"connector", "lineIcon", "flashIcon", "positionY"})
public class Liner {
    private AbstractWebSocketConnector connector;
    private JLabel lineIcon;
    //private CounterLabel flashIcon;
    public static int positionY = imageMargin;

    private String name;
    private Flash flash;

    public Liner() {
        flash = new Flash();
        connector = Connector.getInstance();
    }

    public Liner(String name, AbstractWebSocketConnector connector) {
        this.connector = connector;
        this.name = name;
        flash = new Flash(this, connector);

        lineIcon = getImage(name + ".jpg", imageMargin, positionY);
        //flashIcon = getCounterImage("flash.jpg", imageMargin + imageSize + imageMargin, positionY);
        positionY += imageSize + imageMargin;
    }

    public void useFlash() {
        flash.startCount(this, connector);
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

    @Override
    public boolean equals(Object obj) {
        Liner other = (Liner) obj;
        if (name.equals(other.name) && flash.equals(other.flash)) {
            return true;
        }
        return false;
    }
}
