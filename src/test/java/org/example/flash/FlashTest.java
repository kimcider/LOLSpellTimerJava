package org.example.flash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.CounterLabel;
import org.example.Flash;
import org.example.Liner;
import org.example.connection.AbstractWebSocketConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FlashTest {
    private static ObjectMapper mapper = new ObjectMapper();
    AbstractWebSocketConnector connector;

    @BeforeEach
    public void setUp() {
        connector = Mockito.mock(AbstractWebSocketConnector.class);
    }

    @Test
    public void createFlash() {
        Flash flash = new Flash();
        assertEquals(300, flash.flashCoolTime);
        assertEquals(0, flash.getCoolTime());
        assertTrue(flash.isOn());
        assertNull(flash.getFlashIcon());
    }

    @Test
    public void createFlash2() {
        Liner liner = new Liner("top", connector);
        Flash flash = new Flash(liner, connector);
        assertEquals(300, flash.flashCoolTime);
        assertEquals(0, flash.getCoolTime());
        assertTrue(flash.isOn());
        assertNotNull(flash.getFlashIcon());
    }


    @Test
    public void testOnOff() {
        Flash flash = new Flash();
        flash.off();
        assertFalse(flash.isOn());
        flash.on();
        assertTrue(flash.isOn());
    }

    @Test
    public void startCountWhenFlashOn() {
        Liner liner = new Liner("top", connector);
        Flash mockFlash = Mockito.spy(liner.getFlash());
        CounterLabel spyFlashIcon = Mockito.spy(Mockito.mock(CounterLabel.class));
        mockFlash.setFlashIcon(spyFlashIcon);

        mockFlash.startCount(liner);

        verify(mockFlash, times(1)).isOn();
        verify(spyFlashIcon, times(1)).stopTimer();
        verify(spyFlashIcon, never()).startTimer();
    }
    @Test
    public void startCount2WhenFlashOff() {
        Liner liner = new Liner("top", connector);
        liner.getFlash().setCoolTime(100);
        Flash mockFlash = Mockito.spy(liner.getFlash());

        CounterLabel spyFlashIcon = Mockito.spy(Mockito.mock(CounterLabel.class));
        mockFlash.setFlashIcon(spyFlashIcon);

        mockFlash.startCount(liner);

        verify(mockFlash, times(1)).isOn();
        verify(spyFlashIcon, times(1)).stopTimer();
        verify(spyFlashIcon, times(1)).startTimer();
    }

    @Test
    public void startTestCountWhenFlashIsAlreadyUsed() {
        Liner liner = new Liner("top", connector);
        liner.getFlash().off();
        Flash flash = Mockito.spy(liner.getFlash());
        flash.startCount(liner);

        Mockito.verify(flash, Mockito.times(1)).on();
    }




}
