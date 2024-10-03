package org.example.spell.flash;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.CounterLabel;
import org.example.Liner;
import org.example.connection.AbstractWebSocketConnector;
import org.example.spell.Flash;
import org.example.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
    public void createFlashWithName() {
        Liner liner = new Liner("top", connector);
        assertEquals(300, liner.getFlash().getSpellCoolTime());
        assertEquals(0, liner.getFlash().getCoolTime());
        assertTrue(liner.getFlash().isOn());
        assertNotNull(liner.getFlash().getSpellIcon());
    }

    @Test
    public void testTouchCosmicInsightCoolTimeOnFlash(){
        // CoolTime 제대로 적용되나 체크
        assertNotNull(null);
    }

    @Test
    public void testBuyIonianBootsCoolTimeOnFlash(){
        // CoolTime 제대로 적용되나 체크
        assertNotNull(null);
    }

    @Test
    public void testBothCoolTimeReduceCoolTimeOnFlash(){
        // CoolTime 제대로 적용되나 체크
        assertNotNull(null);
    }
}
