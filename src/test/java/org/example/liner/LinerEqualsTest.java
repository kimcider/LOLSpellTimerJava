package org.example.liner;

import org.example.Liner;
import org.example.connection.AbstractWebSocketConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

public class LinerEqualsTest {
    Liner l1;
    Liner l2;

    @Mock
    AbstractWebSocketConnector connector;

    @BeforeEach
    public void setUp() {
        l1 = new Liner("top", connector);
        l2 = new Liner("top", connector);

        assertEquals(l1, l2);
    }

    @Test
    public void Diffrentname(){
        l1.setName("mid");
        assertNotEquals(l1, l2);
    }

    @Test
    public void DiffentFlashOn(){
        l1.getFlash().off();
        assertNotEquals(l1, l2);
    }

    @Test
    public void DifferentSpellCoolTime(){
        l1.getFlash().setSpellCoolTime(1);
        assertNotEquals(l1, l2);
    }

    @Test
    public void DifferentCosmicInsight(){
        l1.setCosmicInsight(true);
        assertNotEquals(l1, l2);
    }

    @Test
    public void DifferentIonianBoots(){
        l1.setIonianBoots(true);
        assertNotEquals(l1, l2);
    }

    @Test
    public void eualsReturnsFalseForNullObject(){
        l2 = null;
        assertFalse(l1.equals(l2));
    }
}
