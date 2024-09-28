package org.example;

import javax.swing.*;
import java.net.URISyntaxException;

public class Main extends JFrame{
    static Board mn;
    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        Connector connector = new Connector();
        mn = new Board(connector);
    }
}

