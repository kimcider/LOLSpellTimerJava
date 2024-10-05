package org.example.liner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Liner;
import org.example.connection.AbstractWebSocketConnector;
import org.example.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class touchSpellTest {
    AbstractWebSocketConnector connector;

    @BeforeEach
    public void setup() {
        connector = Mockito.mock(AbstractWebSocketConnector.class);
    }

    @Test
    public void createLinerTest1() {
        Liner liner = new Liner();
        assertNotNull(liner.getConnector());
        assertNull(liner.getLineIcon());
        assertNotNull(liner.getFlash().getSpellIcon());
        assertNull(liner.getName());
        assertNotNull(liner.getFlash());

        assertFalse(liner.isCosmicInsight());
        assertFalse(liner.isIonianBoots());
    }

    @Test
    public void touchFlash() {
        Liner liner = new Liner("top", connector);
        // CounterLabel mock 생성
        Spell mockFlash = Mockito.spy(liner.getFlash());
        // liner 객체의 flashIcon 필드를 mockCounterLabel로 교체
        liner.setFlash(mockFlash);

        Mockito.verify(mockFlash, Mockito.never()).startCount(liner);

        try {
            liner.touchSpell(liner.getFlash());
        } catch (Exception e) {
        }

        Mockito.verify(mockFlash, Mockito.times(1)).startCount(liner);
    }

    @Test
    public void touchFlashWhenFlashIsOn() {
        Liner liner = new Liner("top", connector);
        Liner mockLiner = Mockito.spy(liner);
        Spell mockFlash = Mockito.spy(mockLiner.getFlash());
        mockLiner.setFlash(mockFlash);

        mockLiner.touchSpell(mockLiner.getFlash());

        verify(mockFlash, times(1)).isOn();
        verify(mockLiner, times(1)).offSpell(mockFlash);
        verify(mockFlash, times(1)).startCount(mockLiner);

        assertFalse(mockLiner.getFlash().isOn());
    }

    @Test
    public void touchFlashWhenFlashIsOff() {
        Liner liner = new Liner("top", connector);
        liner.offSpell(liner.getFlash());

        Liner mockLiner = Mockito.spy(liner);
        Spell mockFlash = Mockito.spy(mockLiner.getFlash());
        mockLiner.setFlash(mockFlash);

        mockLiner.touchSpell(mockLiner.getFlash());

        verify(mockFlash, times(1)).isOn();
        verify(mockLiner, times(1)).onSpell(mockFlash);
        verify(mockFlash, times(1)).stopCount();

        assertTrue(mockLiner.getFlash().isOn());
    }
}
