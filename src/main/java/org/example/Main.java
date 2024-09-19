package org.example;

import javax.swing.*;
import java.net.URISyntaxException;

public class Main extends JFrame{
    static Board mn;
    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        mn = new Board();
        Connector connector = new Connector();
    }
}

