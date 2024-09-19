package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board extends JWindow {
    static final int imageSize = 40;
    static final int imageMargin = 10;
    private Point initialClick = null;

    Map<String, Liner> linerList = new HashMap<String, Liner>();
    static Map<String, Line> lineList = new HashMap<String, Line>();


    private ObjectMapper mapper = new ObjectMapper();

    public Board(){
        setBackground(new Color(0, 0, 0, 100));
        setLayout(null);
        setSize((imageSize + imageMargin) * 2 + imageMargin, (imageSize + imageMargin) * 5 + imageMargin);
        setAlwaysOnTop(true);
        setLocationRelativeTo(null);
        setVisible(true);

        int x = 10;
        int y = 10;

        ArrayList<String> listNames = new ArrayList();
        listNames.add("top");
        listNames.add("jg");
        listNames.add("mid");
        listNames.add("bot");
        listNames.add("sup");

        for(int i = 0; i < listNames.size(); i++){
            //linerList.put(listNames.get(i), new Liner(listNames.get(i)));

            Line line = new Line(listNames.get(i), x, y);
            lineList.put(listNames.get(i), line);
            add(line.lineIcon);
            add(line.flashIcon);

            y += imageSize + imageMargin;

            linerList.put(listNames.get(i), line.liner);
        }
        repaint();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int windowX = getLocation().x;
                int windowY = getLocation().y;

                int xMoved = e.getX() - (initialClick != null ? initialClick.x : 0);
                int yMoved = e.getY() - (initialClick != null ? initialClick.y : 0);

                int newX = windowX + xMoved;
                int newY = windowY + yMoved;

                setLocation(newX, newY);
            }
        });
    }

    public String getJsonLineList() throws JsonProcessingException {
        List newList = linerList.values().stream().toList();
        return mapper.writeValueAsString(newList);
    }
}
