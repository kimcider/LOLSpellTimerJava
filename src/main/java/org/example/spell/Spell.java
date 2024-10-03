package org.example.spell;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.example.CounterLabel;
import org.example.Liner;

import javax.swing.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Flash.class, name = "flash"),
        //, @JsonSubTypes.Type(value = TP.class, name = "tp")
})
@Getter
@Setter
@JsonIgnoreProperties({"mapper"})
/**
 * 우추/쿨감신을 달면 Liner입장에서 TouchFlash()를 할 떄, 우추/쿨감신이 있으면
 * */
public abstract class Spell {
    protected int spellCoolTime;
    protected int coolTime = 0;
    protected CounterLabel spellIcon = null;

    public Spell() {

    }

    public boolean isOn() {
        return coolTime == 0;
    }

    public void on() {
        coolTime = 0;
    }

    public void off() {
        if (coolTime == 0) {
            coolTime = spellCoolTime;
        }
    }

    public void setSpell(Spell model) {
        coolTime = model.getCoolTime();
        spellCoolTime = model.getSpellCoolTime();
    }

    public void startCount(Liner liner) {
        spellIcon.repaint();

        spellIcon.stopTimer();
        spellIcon.setTimer(new Timer(1000, e -> {
            coolTime--;
            spellIcon.repaint();

            if (getCoolTime() <= 0) {
                on();
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
        if (spellCoolTime == other.spellCoolTime && isOn() == other.isOn()) {
            return true;
        }
        return false;
    }
}
