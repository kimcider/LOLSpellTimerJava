package org.example.liner.spell;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.liner.Liner;
import org.example.connection.AbstractWebSocketConnector;
import org.example.liner.spell.impl.Flash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class SpellTest {
    private static ObjectMapper mapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
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
    public void createFlash() {
        Flash flash = new Flash();
        assertNotNull(flash);
        assertEquals(300, flash.getSpellCoolTime());
        assertEquals(0, flash.getCoolTime());
        assertTrue(flash.isOn());
        assertNotNull(flash.getSpellIcon());
    }


    @Test
    public void spellFlashToJson() throws JsonProcessingException {
        Spell spell = new Flash();

        String json = mapper.writeValueAsString(spell);
        assertEquals("""
                {"type":"flash","coolTime":0}""", json);
    }

    @Test
    public void jsonToSpellFlash() throws JsonProcessingException {
        String json = """
                {"type":"flash","coolTime":0}""";

        Spell spell = mapper.readValue(json, new TypeReference<Spell>(){});
        assertEquals(new Flash(), spell);
    }
    @Test
    public void testStartCount() {
        Liner liner = new Liner("top", connector);
        liner.offSpell(liner.getFlash());


        Spell mockFlash = Mockito.spy(liner.getFlash());
        liner.setFlash(mockFlash);
        CounterLabel spyFlashIcon = Mockito.spy(Mockito.mock(CounterLabel.class));
        mockFlash.setSpellIcon(spyFlashIcon);

        assertFalse(liner.getFlash().isOn());

        liner.offSpell(mockFlash);

        verify(spyFlashIcon, times(1)).repaint();
        verify(spyFlashIcon, times(1)).stopTimer();
        verify(spyFlashIcon, times(1)).startTimer();
    }

    @Test
    public void testStartCountWhenFlashIsAlreadyUsed2() {
        Liner liner = new Liner("top", connector);
        liner.getFlash().setCoolTime(100);

        Spell mockFlash = Mockito.spy(liner.getFlash());
        liner.setFlash(mockFlash);
        CounterLabel spyFlashIcon = Mockito.spy(Mockito.mock(CounterLabel.class));
        mockFlash.setSpellIcon(spyFlashIcon);

        assertFalse(liner.getFlash().isOn());

        liner.offSpell(mockFlash);

        verify(spyFlashIcon, times(1)).repaint();
        verify(spyFlashIcon, times(1)).stopTimer();
        verify(spyFlashIcon, times(1)).startTimer();
    }

    @Test
    public void stopCount(){
        Liner liner = new Liner("top", connector);

        Spell mockFlash = Mockito.spy(liner.getFlash());
        liner.setFlash(mockFlash);
        CounterLabel spyFlashIcon = Mockito.spy(Mockito.mock(CounterLabel.class));
        mockFlash.setSpellIcon(spyFlashIcon);

        assertTrue(liner.getFlash().isOn());

        liner.onSpell(mockFlash);

        verify(spyFlashIcon, never()).startTimer();
        verify(spyFlashIcon, times(1)).repaint();
        verify(spyFlashIcon, times(1)).stopTimer();
    }
}
