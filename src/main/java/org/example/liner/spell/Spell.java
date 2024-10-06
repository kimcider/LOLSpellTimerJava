package org.example.liner.spell;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import org.example.liner.Liner;

import javax.swing.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Flash.class, name = "flash"),
        //, @JsonSubTypes.Type(value = TP.class, name = "tp")
})

@Getter@Setter
@JsonIgnoreProperties({"mapper"})
public abstract class Spell {
    private final int spellCoolTime;
    protected int coolTime = 0;
    protected CounterLabel spellIcon = null;

    public Spell(int spellCoolTime) {
        this.spellCoolTime = spellCoolTime;
    }

    public boolean isOn() {
        return coolTime == 0;
    }

    public void setSpell(Spell model) {
        coolTime = model.getCoolTime();
    }

    public void startCount(Liner liner) {
        spellIcon.repaint();

        spellIcon.stopTimer();
        spellIcon.setTimer(new Timer(1000, e -> {
            coolTime--;
            spellIcon.repaint();

            if (getCoolTime() <= 0) {
                liner.onSpell(this);
                liner.sendLinerStatus();
                spellIcon.stopTimer();
            }
        }));
        spellIcon.startTimer();
    }

    public void stopCount() {
        spellIcon.repaint();
        spellIcon.stopTimer();
    }

    @Override
    public boolean equals(Object obj) {
        Spell other = (Spell) obj;
        if (isOn() == other.isOn()) {
            return true;
        }
        return false;
    }
}
