package org.example.liner.spell.flash;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.liner.Liner;
import org.example.connection.AbstractWebSocketConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(300, liner.getSpell1().getSpellCoolTime());
        assertEquals(0, liner.getSpell1().getCoolTime());
        assertTrue(liner.getSpell1().isOn());
        assertNotNull(liner.getSpell1().getSpellIcon());
    }
}
