package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.example.connection.AbstractWebSocketConnector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.example.Board.*;

@Setter
@Getter
@JsonIgnoreProperties({"connector", "lineIcon", "flashIcon", "positionY"})
public class Liner {

    private AbstractWebSocketConnector connector;
    private JLabel lineIcon;
    private CounterLabel flashIcon;
    private static int positionY = imageMargin;

    private String name;
    private Flash flash;

    public Liner(){
        flash = new Flash();
        connector = Connector.getInstance();
    }

    public Liner(String name, AbstractWebSocketConnector connector){
        this.connector = connector;
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

        CounterLabel result = new CounterLabel(scaledIcon, this, connector);

        result.setLocation(x, y);
        result.setSize(imageSize, imageSize);
        return result;
    }


}
