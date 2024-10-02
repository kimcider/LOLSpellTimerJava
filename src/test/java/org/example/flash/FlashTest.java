package org.example.flash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        assertEquals(300, flash.getCoolTime());
        assertEquals(true, flash.isOn());
        assertNull(flash.getFlashIcon());
    }

    @Test
    public void createFlash2() {
        Liner liner = new Liner("top", connector);
        Flash flash = new Flash(liner, connector);
        assertEquals(300, flash.flashCoolTime);
        assertEquals(300, flash.getCoolTime());
        assertEquals(true, flash.isOn());
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
    public void startCount() {
        Liner liner = new Liner("top", connector);
        Flash flash = Mockito.spy(liner.getFlash());
        flash.startCount(liner);

        Mockito.verify(flash, Mockito.never()).on();
    }

    @Test
    public void startTestCountWhenFlashIsAlreadyUsed() {
        Liner liner = new Liner("top", connector);
        liner.getFlash().off();
        Flash flash = Mockito.spy(liner.getFlash());
        flash.startCount(liner);

        Mockito.verify(flash, Mockito.times(1)).on();
    }

    @Test
    public void equals() {
        Flash f1 = new Flash();
        Flash f2 = new Flash();
        assertEquals(f1, f2);

        f1.off();
        assertNotEquals(f1, f2);

        f2.off();
        assertEquals(f1, f2);

        f1.setCoolTime(150);
        assertNotEquals(f1, f2);

        f2.setCoolTime(150);
        assertEquals(f1, f2);
    }
}
