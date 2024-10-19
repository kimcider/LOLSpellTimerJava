package org.example.liner.spell;

import org.example.liner.spell.CounterLabel;
import org.example.connection.AbstractWebSocketConnector;
import org.example.liner.spell.Flash;
import org.example.liner.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class setSpell {
    AbstractWebSocketConnector connector;
    Spell spellTarget ;
    Spell spellModel ;
    @BeforeEach
    void setUp() {
        spellTarget = new Flash();
        spellModel = new Flash();
        connector = Mockito.mock(AbstractWebSocketConnector.class);
    }

    @Test
    public void setSpell_AssertSpellIconDoesNotChange(){
        Spell spell = new Flash();
        CounterLabel originalSpellIcon = spell.getSpellIcon();

        Flash flash = new Flash();
        spell.setSpell(null, flash);

        assertEquals(originalSpellIcon, spell.getSpellIcon());
    }

    @Test
    public void setSpell_AssertSetSameSpellWillNotChangeSpell(){
        Spell spell = new Flash();
        assertEquals(spell, spellTarget);
        assertEquals(spell, spellModel);

        spellTarget.setSpell(null, spellModel);
        assertEquals(spell, spellTarget);
        assertEquals(spell, spellModel);
    }

    @Test
    public void setSpell_changeCollTime(){
        spellModel.setCoolTime(50);

        spellTarget.setSpell(null, spellModel);

        assertEquals(50, spellTarget.getCoolTime());
    }


    @Test
    public void setSpell_SpyFunctionCall(){
        Spell mockSpellTarget = Mockito.spy(spellTarget);
        Spell mockSpellModel = Mockito.spy(spellModel);

        mockSpellTarget.setSpell(null, mockSpellModel);

        verify(mockSpellTarget, times(1)).setSpell(null, mockSpellModel);
        verify(mockSpellModel, times(1)).getCoolTime();
    }

}
