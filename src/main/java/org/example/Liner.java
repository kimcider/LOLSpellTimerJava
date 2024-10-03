package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.example.connection.AbstractWebSocketConnector;
import org.example.connection.Connector;
import org.example.spell.Flash;
import org.example.spell.Spell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static org.example.Board.imageMargin;
import static org.example.Board.imageSize;

@Setter
@Getter
@JsonIgnoreProperties({"connector", "lineIcon", "flashIcon", "positionY", "spell2"})
public class Liner {
    private static ObjectMapper mapper = new ObjectMapper();
    private AbstractWebSocketConnector connector;
    private JLabel lineIcon;
    //private CounterLabel flashIcon;
    public static int positionY = imageMargin;

    private String name;
    private Spell flash;
    private Spell spell2;

    private boolean cosmicInsight = false;
    private boolean ionianBoots = false;

    public Liner() {
        flash = new Flash();
        connector = Connector.getInstance();
    }

    public Liner(String name, AbstractWebSocketConnector connector) {
        this.connector = connector;
        this.name = name;
        flash = new Flash();
        flash.getSpellIcon().addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if(flash.isOn()){
                        flash.off();
                    }else{
                        flash.on();
                    }
                    flash.startCount(Liner.this);
                    sendLinerStatus();
                }
            }
        });

        lineIcon = getImage(name + ".jpg", imageMargin, positionY);
        positionY += imageSize + imageMargin;
    }

    public void sendLinerStatus() {
        try {
            String json = mapper.writeValueAsString(this);
            connector.sendMessage("sendLinerStatus", json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void touchFlash() {
        if(flash.isOn()){
            flash.off();
            flash.startCount(this);
        }else{
            flash.on();
            flash.stopCount();
        }
    }
    
    //TODO: 완성하기
    public void touchCosmicInsight(){
        //spell1.CosmicInsightOn/Off()
        //sepll2.CosmicInsightOn/Off()
    }
    public void buyIonianBoots(){
        //spell1.IonianBootsOn/Off();
        //spell2.IonianBootsOn/Off();
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

    public void setLiner(Liner model) {
        if(model == null){
            return;
        }

        flash.setSpell(model.getFlash());
        cosmicInsight = model.isCosmicInsight();
        ionianBoots = model.isIonianBoots();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }

        Liner other = (Liner) obj;
        if (name.equals(other.name) && flash.equals(other.flash)  && cosmicInsight == other.cosmicInsight && ionianBoots == other.ionianBoots) {
            return true;
        }
        return false;
    }
}
