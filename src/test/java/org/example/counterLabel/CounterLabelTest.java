package org.example.counterLabel;

import org.example.CounterLabel;
import org.example.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class CounterLabelTest {
    ImageIcon mockIcon;
    Spell spell;
    CounterLabel counterLabel;

    @BeforeEach
    public void setUp() {
        mockIcon = Mockito.mock(ImageIcon.class);
        spell = Mockito.mock(Spell.class);
        counterLabel = new CounterLabel(mockIcon, spell);
    }

    @Test
    public void createCounterLabel(){
        assertNotNull(counterLabel.getIcon());
        assertNotNull(counterLabel.getSpell());
        assertNull(counterLabel.getTimer());
    }

    @Test
    public void startTimerWhenTimerIsNotNull(){
        assertNull(counterLabel.getTimer());
        Timer mockTimer = Mockito.spy(Mockito.mock(Timer.class));
        counterLabel.setTimer(mockTimer);

        counterLabel.startTimer();

        verify(mockTimer, times(1)).start();
    }

    @Test
    public void startTimerWhenTimerIsNull(){
        assertNull(counterLabel.getTimer());
        Timer mockTimer = Mockito.spy(Mockito.mock(Timer.class));

        counterLabel.startTimer();

        verify(mockTimer, never()).start();
    }
}
