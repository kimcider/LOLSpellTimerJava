package org.example.liner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.example.connection.AbstractWebSocketConnector;
import org.example.connection.Connector;
import org.example.display.Board;
import org.example.liner.spell.Spell;
import org.example.liner.spell.impl.Flash;
import org.example.liner.spell.impl.NoSpell;
import org.example.liner.spell.impl.Teleport;
import org.example.liner.spell.impl.UpgradeTeleport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import static org.example.Setting.*;

@Setter
@Getter
@JsonIgnoreProperties({"mapper", "connector", "lineIcon", "flashIcon", "cosmicInsightIcon", "ionianBootsIcon", "positionY", "bootsHaste"})
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

    // 장화의 소환사 주문 가속. 명석함의 아이오니아 장화는 10, 업그레이드한 진홍빛 명석함은 20.
    // 라이브 게임 API로 읽은 로컬 값이라 서버로 보내지 않는다.
    private int bootsHaste = 10;

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
        spell1.getSpellIcon().addMouseListener(new SpellMouseAdapter(spell1));
        spell1.getSpellIcon().setLocation(spell1IconX, iconPositionY);
        spell2 = new NoSpell();
        spell2.getSpellIcon().addMouseListener(new SpellMouseAdapter(spell2));
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

        SpellMouseAdapter(Spell spell) {
            this.spell = spell;
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
            }
        }
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
            reduction += bootsHaste;
        }

        int coolTime = (int) (spell.getSpellCoolTime() * (1 - (reduction / (reduction + 100))));
        spell.setCoolTime(coolTime);
    }

    public void onSpell(Spell spell) {
        spell.setCoolTime(0);
    }

    /**
     * 라이브 게임 API에서 읽은 스펠로 슬롯을 바꾼다.
     * 같은 게임의 모든 PC가 같은 데이터를 직접 읽으므로 서버로 보내지 않는다.
     */
    public void applyGameSpells(Class<? extends Spell> spell1Class, Class<? extends Spell> spell2Class) {
        boolean changed = false;
        if (spell1.getClass() != spell1Class) {
            spell1 = replaceSpell(spell1, spell1Class);
            changed = true;
        }
        if (spell2.getClass() != spell2Class) {
            spell2 = replaceSpell(spell2, spell2Class);
            changed = true;
        }
        if (changed) {
            Board.getInstance().reloadBoard();
        }
    }

    private Spell replaceSpell(Spell oldSpell, Class<? extends Spell> newClass) {
        Spell newSpell = changeSpell(newClass.getName());
        newSpell.getSpellIcon().addMouseListener(new SpellMouseAdapter(newSpell));

        // 순간이동이 10분에 강화 순간이동으로 바뀌어도 돌던 쿨타임은 이어진다
        if (oldSpell instanceof Teleport && newSpell instanceof UpgradeTeleport && !oldSpell.isOn()) {
            int remaining = oldSpell.getCoolTime();
            oldSpell.setCoolTime(0);  // 버려지는 아이콘의 타이머를 멈춘다
            newSpell.setCoolTime(remaining);
        }
        return newSpell;
    }

    /** 라이브 게임 API에서 읽은 장화 상태를 반영한다. haste가 0이면 장화 없음. 서버로 보내지 않는다. */
    public void applyGameBoots(int haste) {
        if (haste > 0) {
            bootsHaste = haste;
        }
        if (isIonianBoots() != (haste > 0)) {
            setIonianBoots(haste > 0);
        }
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
            spell1.getSpellIcon().addMouseListener(new SpellMouseAdapter(spell1));
        } else {
            spell1.setSpell(model.getSpell1());
        }

        if (spell2.getClass() != model.getSpell2().getClass()) {
            spell2 = model.getSpell2();
            Board.getInstance().reloadBoard();
            spell2.getSpellIcon().addMouseListener(new SpellMouseAdapter(spell2));
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
