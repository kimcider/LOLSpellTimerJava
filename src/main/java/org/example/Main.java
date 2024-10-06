package org.example;

import org.example.connection.Connector;

import javax.swing.*;

public class Main extends JFrame {
    static Board mn;

    public static void main(String[] args) {
        Connector.getInstance();
        mn = new Board();
    }
}

