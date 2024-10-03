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
    public void testOffWhenCoolTimeIsNotZero() {
        Flash flash = Mockito.spy(new Flash());

        flash.setCoolTime(flash.getFlashCoolTime() - 15);
        flash.off();
        assertNotEquals(flash.getFlashCoolTime(), flash.getCoolTime());
    }

    @Test
    public void testStartCount() {
        Liner liner = new Liner("top", connector);
        liner.getFlash().setCoolTime(100);

        Flash mockFlash = Mockito.spy(liner.getFlash());
        CounterLabel spyFlashIcon = Mockito.spy(Mockito.mock(CounterLabel.class));
        mockFlash.setFlashIcon(spyFlashIcon);

        assertFalse(liner.getFlash().isOn());

        mockFlash.startCount(liner);

        verify(mockFlash, never()).isOn();
        verify(spyFlashIcon, times(1)).repaint();
        verify(spyFlashIcon, times(1)).stopTimer();
        verify(spyFlashIcon, times(1)).startTimer();
    }

    @Test
    public void testStartCountWhenFlashIsAlreadyUsed2() {
        // testStartCountWhenFlashIsAlreadyUsed와 유사.
        Liner liner = new Liner("top", connector);
        liner.getFlash().off(); //여기만 다름 setCoolTime()인지 off()인지.

        Flash mockFlash = Mockito.spy(liner.getFlash());
        CounterLabel spyFlashIcon = Mockito.spy(Mockito.mock(CounterLabel.class));
        mockFlash.setFlashIcon(spyFlashIcon);

        assertFalse(liner.getFlash().isOn());

        mockFlash.startCount(liner);

        verify(mockFlash, never()).isOn();
        verify(spyFlashIcon, times(1)).repaint();
        verify(spyFlashIcon, times(1)).stopTimer();
        verify(spyFlashIcon, times(1)).startTimer();
    }

    @Test
    public void stopCount(){
        Liner liner = new Liner("top", connector);

        Flash mockFlash = Mockito.spy(liner.getFlash());
        CounterLabel spyFlashIcon = Mockito.spy(Mockito.mock(CounterLabel.class));
        mockFlash.setFlashIcon(spyFlashIcon);

        assertTrue(liner.getFlash().isOn());

        mockFlash.stopCount();

        verify(spyFlashIcon, never()).startTimer();
        verify(spyFlashIcon, times(1)).repaint();
        verify(spyFlashIcon, times(1)).stopTimer();
    }

}
