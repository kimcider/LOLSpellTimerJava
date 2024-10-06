package org.example.liner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.example.connection.AbstractWebSocketConnector;
import org.example.connection.Connector;
import org.example.liner.spell.Flash;
import org.example.liner.spell.Spell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static org.example.Setting.*;

@Setter
@Getter
@JsonIgnoreProperties({"connector", "lineIcon", "flashIcon", "cosmicInsightIcon", "ionianBootsIcon", "positionY", "spell2"})
@JsonPropertyOrder({"name", "flash", "cosmicInsight", "ionianBoots"})
public class Liner {
    private static ObjectMapper mapper = new ObjectMapper();
    private AbstractWebSocketConnector connector;
    private JLabel lineIcon;
    private CoolTimeReducer cosmicInsightIcon;
    private CoolTimeReducer ionianBootsIcon;

    private String name;
    private Spell flash;

    public Liner() {
        flash = new Flash();
        connector = Connector.getInstance();
        cosmicInsightIcon = new CoolTimeReducer(getImageIcon("cosmicInsights.jpg", 1), getImageIcon("check-mark.jpg", 1), 0, 0, 0);
        ionianBootsIcon = new CoolTimeReducer(getImageIcon("ionianBoots.jpg", 1), getImageIcon("check-mark.jpg", 1), 0, 0, 0);
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

        lineIcon = getImage(name + ".jpg", imageSize, lineIconX, iconPositionY);
        cosmicInsightIcon = new CoolTimeReducer(getImageIcon("cosmicInsights.jpg", smallImageSize), getImageIcon("check-mark.jpg", smallImageSize), smallImageSize, cosmicInsightIconX, iconPositionY);
        ionianBootsIcon = new CoolTimeReducer(getImageIcon("ionianBoots.jpg", smallImageSize), getImageIcon("check-mark.jpg", smallImageSize), smallImageSize, ionianBootsIconX, iconPositionY + smallImageSize + smallImageMargin);
        iconPositionY += imageSize + imageMargin;

        cosmicInsightIcon.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    touchCoolTimeReducer(cosmicInsightIcon);
                    sendLinerStatus();
                }
            }
        });

        ionianBootsIcon.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    touchCoolTimeReducer(ionianBootsIcon);
                    sendLinerStatus();
                }
            }
        });

    }

    public void reloadIcon(){
        lineIcon = getImage(name + ".jpg", imageSize, lineIconX, iconPositionY);

    }

    public boolean isCosmicInsight(){
        return cosmicInsightIcon.isOn();
    }
    public boolean isIonianBoots(){
        return ionianBootsIcon.isOn();
    }

    public void setCosmicInsight(boolean cosmicInsight){
        cosmicInsightIcon.setOn(cosmicInsight);
    }

    public void setIonianBoots(boolean ionianBoots){
        ionianBootsIcon.setOn(ionianBoots);
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
        if(isCosmicInsight()){
            reduction += 18;
        }
        if(isIonianBoots()){
            reduction += 10;
        }

        int coolTime = (int) (spell.getSpellCoolTime() * (1 - (reduction / (reduction + 100))));
        spell.setCoolTime(coolTime);
    }

    public void onSpell(Spell spell){
        spell.setCoolTime(0);
    }

    public void touchCoolTimeReducer(CoolTimeReducer coolTimeReducer){
        if(coolTimeReducer.isOn()){
            coolTimeReducer.setOn(false);
        }else{
            coolTimeReducer.setOn(true);
        }
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
        setCosmicInsight(model.isCosmicInsight());
        setIonianBoots(model.isIonianBoots());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }

        Liner other = (Liner) obj;
        if (name.equals(other.name) && flash.equals(other.flash)  && isCosmicInsight() == other.isCosmicInsight() && isIonianBoots() == other.isIonianBoots()) {
            return true;
        }
        return false;
    }
}
