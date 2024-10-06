package org.example.liner.spell;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

import static org.example.Setting.*;

@Getter
@Setter
@JsonIgnoreProperties({"spellIcon", "spellCoolTime", "on"})
public class Flash extends Spell {
    public Flash() {
        super(300);
        spellIcon = getCounterImage("flash.jpg",  spellIconX, iconPositionY);
    }

    private CounterLabel getCounterImage(String path, int x, int y) {
        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource(path));
        Image scaledImage = imageIcon.getImage().getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        CounterLabel result = new CounterLabel(scaledIcon, this);

        result.setLocation(x, y);
        result.setSize(imageSize, imageSize);
        return result;
    }
}
