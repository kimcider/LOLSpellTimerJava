package org.example.spell;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.example.CounterLabel;

import javax.swing.*;
import java.awt.*;

import static org.example.Board.*;
import static org.example.Liner.positionY;

@Getter
@Setter
@JsonIgnoreProperties({"spellIcon", "spellCoolTime", "on"})
public class Flash extends Spell {
    public Flash() {
        super(300);
        spellIcon = getCounterImage("flash.jpg",  imageSize  + imageMargin + smallImageSize  + imageMargin, positionY);
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
