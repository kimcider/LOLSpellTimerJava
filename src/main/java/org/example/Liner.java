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

import static org.example.Board.*;

@Setter
@Getter
@JsonIgnoreProperties({"connector", "lineIcon", "flashIcon", "cosmicInsightIcon", "ionianBootsIcon", "positionY", "spell2"})
public class Liner {
    private static ObjectMapper mapper = new ObjectMapper();
    private AbstractWebSocketConnector connector;
    public static int positionY = imageMargin;
    private JLabel lineIcon;
    private CoolTimeReducer cosmicInsightIcon;
    private CoolTimeReducer ionianBootsIcon;

    private boolean cosmicInsight = false;
    private boolean ionianBoots = false;

    private String name;
    private Spell flash;
    private Spell spell2;



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
                        offSpell(flash);
                    }else{
                        onSpell(flash);
                    }
                    flash.startCount(Liner.this);
                    sendLinerStatus();
                }
            }
        });

//        lineIcon = getImage(name + ".jpg", imageSize, imageMargin, positionY);
//        cosmicInsightIcon = new CoolTimeReducer(getImageIcon("cosmicInsights.jpg", smallImageSize), getImageIcon("check-mark.jpg", smallImageSize), smallImageSize, imageSize + imageMargin + smallImageMargin, positionY);
//        ionianBootsIcon = new CoolTimeReducer(getImageIcon("ionianBoots.jpg", smallImageSize), getImageIcon("check-mark.jpg", smallImageSize), smallImageSize, imageSize + imageMargin + smallImageMargin, positionY + smallImageSize + smallImageMargin);

        lineIcon = getImage(name + ".jpg", imageSize,  smallImageSize+ imageMargin, positionY);
        cosmicInsightIcon = new CoolTimeReducer(getImageIcon("cosmicInsights.jpg", smallImageSize), getImageIcon("check-mark.jpg", smallImageSize), smallImageSize, smallImageMargin, positionY);
        ionianBootsIcon = new CoolTimeReducer(getImageIcon("ionianBoots.jpg", smallImageSize), getImageIcon("check-mark.jpg", smallImageSize), smallImageSize, smallImageMargin, positionY + smallImageSize + smallImageMargin);

        cosmicInsightIcon.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    touchCosmicInsight();
                    sendLinerStatus();
                }
            }
        });

        ionianBootsIcon.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    touchIonianBoots();
                    sendLinerStatus();
                }
            }
        });
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

    public void touchSpell(Spell spell) {
        if(spell.isOn()){
            offSpell(spell);
            spell.startCount(this);
        }else{
            onSpell(spell);
            spell.stopCount();
        }
    }

    public void offSpell(Spell spell){
        double reduction = 0;
        if(cosmicInsight){
            reduction += 18;
        }
        if(ionianBoots){
            reduction += 10;
        }

        int coolTime = (int) (spell.getSpellCoolTime() * (1 - (reduction / (reduction + 100))));
        spell.setCoolTime(coolTime);
    }

    public void onSpell(Spell spell){
        spell.setCoolTime(0);
    }

    public void touchCosmicInsight(){
        if(cosmicInsight){
            cosmicInsight = false;
        }else{
            cosmicInsight = true;
        }
        cosmicInsightIcon.setOn(cosmicInsight);
        cosmicInsightIcon.updateIcon();
    }
    public void touchIonianBoots(){
        if(ionianBoots){
            ionianBoots = false;
        }else{
            ionianBoots = true;
        }
        ionianBootsIcon.setOn(ionianBoots);
        ionianBootsIcon.updateIcon();
    }

    private ImageIcon getImageIcon(String path, int size){
        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource(path));
        Image scaledImage = imageIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private JLabel getImage(String path, int size, int x, int y) {
        JLabel result = new JLabel(getImageIcon(path, size));

        result.setLocation(x, y);
        result.setSize(size, size);
        return result;
    }

    public void setLiner(Liner model) {
        if(model == null){
            return;
        }

        flash.setSpell(model.getFlash());
        cosmicInsight = model.isCosmicInsight();
        cosmicInsightIcon.setOn(cosmicInsight);
        ionianBoots = model.isIonianBoots();
        ionianBootsIcon.setOn(ionianBoots);
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
