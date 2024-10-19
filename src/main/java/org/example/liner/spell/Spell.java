package org.example.liner.spell;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import org.example.liner.spell.impl.Flash;

import javax.swing.*;
import java.awt.*;

import static org.example.Setting.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Flash.class, name = "flash"),
        //, @JsonSubTypes.Type(value = TP.class, name = "tp")
})

@Getter
@Setter
public abstract class Spell {
    @JsonIgnore
    private final int spellCoolTime;
    @JsonIgnore
    protected CounterLabel spellIcon = null;

    @JsonIgnore
    String spellImagePath = null;

    protected int coolTime = 0;


    public Spell(int spellCoolTime, String spellImagePath) {
        this.spellCoolTime = spellCoolTime;
        this.spellImagePath = spellImagePath;
        spellIcon = getCounterImage(getSpellImagePath(), spellIconX, iconPositionY);
    }

    @JsonIgnore
    public boolean isOn() {
        return coolTime == 0;
    }

    public void setCoolTime(int coolTime) {
        this.coolTime = coolTime;
        if (isOn()) {
            stopCount();
        } else {
            startCount();
        }
    }

    public void setSpell(Spell model) {
        setCoolTime(model.getCoolTime());
    }

    private void startCount() {
        spellIcon.repaint();
        spellIcon.stopTimer();
        spellIcon.setTimer(new Timer(1000, e -> {
            coolTime--;
            spellIcon.repaint();

            if (getCoolTime() <= 0) {
                spellIcon.stopTimer();
            }
        }));
        spellIcon.startTimer();
    }

    private void stopCount() {
        spellIcon.repaint();
        spellIcon.stopTimer();
    }

    @Override
    public boolean equals(Object obj) {
        Spell other = (Spell) obj;
        if (isOn() != other.isOn()) {
            return false;
        }
        return true;
    }



    protected CounterLabel getCounterImage(String path, int x, int y) {
        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource(path));
        Image scaledImage = imageIcon.getImage().getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        CounterLabel result = new CounterLabel(scaledIcon, this);

        result.setLocation(x, y);
        result.setSize(imageSize, imageSize);
        return result;
    }
}
