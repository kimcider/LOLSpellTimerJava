package org.example.liner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.example.connection.AbstractWebSocketConnector;
import org.example.connection.Connector;
import org.example.display.Board;
import org.example.display.ReloadSpellPage;
import org.example.liner.spell.Spell;
import org.example.liner.spell.impl.Flash;
import org.example.liner.spell.impl.NoSpell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import static org.example.Setting.*;

@Setter
@Getter
@JsonIgnoreProperties({"mapper", "connector", "lineIcon", "flashIcon", "cosmicInsightIcon", "ionianBootsIcon", "positionY"})
@JsonPropertyOrder({"name", "flash", "cosmicInsight", "ionianBoots"})
public class Liner {
    private static ObjectMapper mapper = new ObjectMapper();
    private AbstractWebSocketConnector connector;
    private JLabel lineIcon;
    private CoolTimeReducer cosmicInsightIcon;
    private CoolTimeReducer ionianBootsIcon;

    private String name;
    private Spell spell1;
    private Spell spell2;

    public Liner() {
        spell1 = new Flash();
        connector = Connector.getInstance();
        cosmicInsightIcon = new CoolTimeReducer(getImageIcon("cosmicInsights.jpg", 1), getImageIcon("check-mark.jpg", 1), 0, 0, 0);
        ionianBootsIcon = new CoolTimeReducer(getImageIcon("ionianBoots.jpg", 1), getImageIcon("check-mark.jpg", 1), 0, 0, 0);
    }

    public Liner(String name, AbstractWebSocketConnector connector) {
        this.connector = connector;
        this.name = name;
        spell1 = new Flash();
        spell1.getSpellIcon().addMouseListener(new SpellMouseAdapter(spell1, 1));
        spell1.getSpellIcon().setLocation(spell1IconX, iconPositionY);
        spell2 = new NoSpell();
        spell2.getSpellIcon().addMouseListener(new SpellMouseAdapter(spell2, 2));
        spell2.getSpellIcon().setLocation(spell2IconX, iconPositionY);

        lineIcon = getImage(name + ".jpg", imageSize, iconPositionX, iconPositionY);
        cosmicInsightIcon = new CoolTimeReducer(getImageIcon("cosmicInsights.jpg", smallImageSize), getImageIcon("check-mark.jpg", smallImageSize), smallImageSize, coolTimeReducer, iconPositionY);
        ionianBootsIcon = new CoolTimeReducer(getImageIcon("ionianBoots.jpg", smallImageSize), getImageIcon("check-mark.jpg", smallImageSize), smallImageSize, coolTimeReducer, iconPositionY + smallImageSize + smallImageMargin);


        cosmicInsightIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    touchCoolTimeReducer(cosmicInsightIcon);
                    sendLinerStatus();
                }
            }
        });

        ionianBootsIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    touchCoolTimeReducer(ionianBootsIcon);
                    sendLinerStatus();
                }
            }
        });

    }

    class SpellMouseAdapter extends MouseAdapter {
        Spell spell;
        int spellNumber;

        SpellMouseAdapter(Spell spell, int spellNumber) {
            this.spell = spell;
            this.spellNumber = spellNumber;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (spell.isOn()) {
                    offSpell(spell);
                } else {
                    onSpell(spell);
                }
                sendLinerStatus();
            } else if (SwingUtilities.isRightMouseButton(e)) {
                reloadSpell(spellNumber);
            }
        }
    }

    private void reloadSpell(int spellNumber) {
        ReloadSpellPage spellReloadWindow = ReloadSpellPage.getInstance();

        spellReloadWindow.setRunnable(() -> {
            String className = "org.example.liner.spell.impl." + spellReloadWindow.getSpellName();

            if(spellNumber == 1){
                spell1 = changeSpell(className);
                spell1.getSpellIcon().addMouseListener(new SpellMouseAdapter(spell1, 1));
            }else{
                spell2 = changeSpell(className);
                spell2.getSpellIcon().addMouseListener(new SpellMouseAdapter(spell2, 2));
            }
            Board.getInstance().reloadBoard();
            sendLinerStatus();
        });

        Point boardLocation = Board.getInstance().getLocationOnScreen();
        int dialogX = boardLocation.x - (reloadBoardWidth - boardWidth) / 2;
        int dialogY = boardLocation.y + boardHeight;
        spellReloadWindow.setLocation(dialogX, dialogY);
    }

    public Spell changeSpell(String spellName) {
        try {
            Class<?> clazz = Class.forName(spellName);
            return (Spell) clazz.getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isCosmicInsight() {
        return cosmicInsightIcon.isOn();
    }

    public boolean isIonianBoots() {
        return ionianBootsIcon.isOn();
    }

    public void setCosmicInsight(boolean cosmicInsight) {
        cosmicInsightIcon.setOn(cosmicInsight);
    }

    public void setIonianBoots(boolean ionianBoots) {
        ionianBootsIcon.setOn(ionianBoots);
    }

    public void sendLinerStatus() {
        try {
            String json = mapper.writeValueAsString(this);
            System.out.println(json);
            connector.sendMessage("sendLinerStatus", json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void offSpell(Spell spell) {
        double reduction = 0;
        if (isCosmicInsight()) {
            reduction += 18;
        }
        if (isIonianBoots()) {
            reduction += 10;
        }

        int coolTime = (int) (spell.getSpellCoolTime() * (1 - (reduction / (reduction + 100))));
        spell.setCoolTime(coolTime);
    }

    public void onSpell(Spell spell) {
        spell.setCoolTime(0);
    }

    public void touchCoolTimeReducer(CoolTimeReducer coolTimeReducer) {
        coolTimeReducer.setOn(!coolTimeReducer.isOn());
    }

    private ImageIcon getImageIcon(String path, int size) {
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
        if (model == null) {
            return;
        }

        if (spell1.getClass() != model.getSpell1().getClass()) {
            spell1 = model.getSpell1();
            Board.getInstance().reloadBoard();
            spell1.getSpellIcon().addMouseListener(new SpellMouseAdapter(spell1, 1));
        } else {
            spell1.setSpell(model.getSpell1());
        }

        if (spell2.getClass() != model.getSpell2().getClass()) {
            spell2 = model.getSpell2();
            Board.getInstance().reloadBoard();
            spell2.getSpellIcon().addMouseListener(new SpellMouseAdapter(spell2, 2));
        } else {
            spell2.setSpell(model.getSpell2());
        }
        setCosmicInsight(model.isCosmicInsight());
        setIonianBoots(model.isIonianBoots());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        Liner other = (Liner) obj;

        if (!name.equals(other.name)) {
            return false;
        }
        if (!spell1.equals(other.spell1)) {
            return false;
        }
        if (!spell2.equals(other.spell2)) {
            return false;
        }
        if (isCosmicInsight() != other.isCosmicInsight()) {
            return false;
        }
        if (isIonianBoots() != other.isIonianBoots()) {
            return false;
        }
        return true;
    }


    public void reloadIcon() {
        lineIcon.setIcon(getImageIcon(name + ".jpg", imageSize));
        lineIcon.setSize(imageSize, imageSize);
        lineIcon.setLocation(iconPositionX, iconPositionY);

        cosmicInsightIcon.setIcon(getImageIcon("cosmicInsights.jpg", smallImageSize));
        cosmicInsightIcon.setSize(smallImageSize, smallImageSize);
        cosmicInsightIcon.setLocation(coolTimeReducer, iconPositionY);
        cosmicInsightIcon.updateIcon();

        ionianBootsIcon.setIcon(getImageIcon("ionianBoots.jpg", smallImageSize));
        ionianBootsIcon.setSize(smallImageSize, smallImageSize);
        ionianBootsIcon.setLocation(coolTimeReducer, iconPositionY + smallImageSize + smallImageMargin);
        ionianBootsIcon.updateIcon();

        spell1.getSpellIcon().setIcon(getImageIcon(spell1.getSpellImagePath(), imageSize));
        spell1.getSpellIcon().setSize(imageSize, imageSize);
        spell1.getSpellIcon().setLocation(spell1IconX, iconPositionY);

        spell2.getSpellIcon().setIcon(getImageIcon(spell2.getSpellImagePath(), imageSize));
        spell2.getSpellIcon().setSize(imageSize, imageSize);
        spell2.getSpellIcon().setLocation(spell2IconX, iconPositionY);
    }

}
