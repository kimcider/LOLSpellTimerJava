package org.example.spell;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.CounterLabel;
import org.example.Liner;
import org.example.connection.AbstractWebSocketConnector;
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
                {"type":"flash","spellCoolTime":300,"coolTime":0,"cosmicInsight":false,"ionianBoots":false}""", json);
    }

    @Test
    public void jsonToSpellFlash() throws JsonProcessingException {
        String json = """
                {"type":"flash","spellCoolTime":300,"coolTime":0,"cosmicInsight":false,"ionianBoots":false}""";

        Spell spell = mapper.readValue(json, new TypeReference<Spell>(){});
        assertEquals(new Flash(), spell);
    }

    @Test
    public void jsonToSpellFlashWithCosmicInsight() throws JsonProcessingException {
        String json = """
                {"type":"flash","spellCoolTime":300,"coolTime":0,"cosmicInsight":true,"ionianBoots":false}""";

        Spell spell = mapper.readValue(json, new TypeReference<Spell>(){});
        Flash flash = new Flash();
        flash.setCosmicInsight(true);
        assertEquals(flash, spell);
    }

    @Test
    public void jsonToSpellFlashWithIonianBoots() throws JsonProcessingException {
        String json = """
                {"type":"flash","spellCoolTime":300,"coolTime":0,"cosmicInsight":false,"ionianBoots":true}""";

        Spell spell = mapper.readValue(json, new TypeReference<Spell>(){});
        Flash flash = new Flash();
        flash.setIonianBoots(true);
        assertEquals(flash, spell);
    }

    @Test
    public void jsonToSpellFlashWithIonianBootsAndCosmicInsight() throws JsonProcessingException {
        String json = """
                {"type":"flash","spellCoolTime":300,"coolTime":0,"cosmicInsight":true,"ionianBoots":true}""";

        Spell spell = mapper.readValue(json, new TypeReference<Spell>(){});
        Flash flash = new Flash();
        flash.setIonianBoots(true);
        flash.setCosmicInsight(true);
        assertEquals(flash, spell);
    }

    @Test
    public void setSpell_AssertSpellIconDoesNotChange(){
        Spell spell = new Flash();
        CounterLabel originalSpellIcon = spell.getSpellIcon();

        Flash flash = new Flash();
        spell.setSpell(flash);

        assertEquals(originalSpellIcon, spell.getSpellIcon());
    }

    @Test
    public void setSpell_AssertSetSameSpellWillNotChangeSpell(){
        Spell spell = new Flash();
        assertEquals(spell, spellTarget);
        assertEquals(spell, spellModel);

        spellTarget.setSpell(spellModel);
        assertEquals(spell, spellTarget);
        assertEquals(spell, spellModel);
    }

    @Test
    public void setSpell_changeCollTime(){
        spellModel.setCoolTime(50);

        spellTarget.setSpell(spellModel);

        assertEquals(50, spellTarget.getCoolTime());
    }

    @Test
    public void setSpell_changeSpellCollTime(){
        spellModel.setSpellCoolTime(50);

        spellTarget.setSpell(spellModel);

        assertEquals(50, spellTarget.getSpellCoolTime());
    }

    @Test
    public void setSpell_changeCosmicInsight(){
        spellModel.setCosmicInsight(true);

        spellTarget.setSpell(spellModel);

        assertTrue(spellTarget.isCosmicInsight());
    }

    @Test
    public void setSpell_changeIonianBoots(){
        spellModel.setIonianBoots(true);

        spellTarget.setSpell(spellModel);

        assertTrue(spellTarget.isIonianBoots());
    }

    @Test
    public void setSpell_SpyFunctionCall(){
        Spell mockSpellTarget = Mockito.spy(spellTarget);
        Spell mockSpellModel = Mockito.spy(spellModel);

        mockSpellTarget.setSpell(mockSpellModel);

        verify(mockSpellTarget, times(1)).setSpell(mockSpellModel);
        verify(mockSpellModel, times(1)).getCoolTime();
        verify(mockSpellModel, times(1)).getSpellCoolTime();
        verify(mockSpellModel, times(1)).isCosmicInsight();
        verify(mockSpellModel, times(1)).isIonianBoots();
    }


    @Test
    public void testOnOff() {
        spellTarget.off();
        assertFalse(spellTarget.isOn());
        spellTarget.on();
        assertTrue(spellTarget.isOn());
    }

    @Test
    public void testOffWhenCoolTimeIsNotZero() {
        Spell flash = Mockito.spy(spellTarget);

        flash.setCoolTime(flash.getSpellCoolTime() - 15);
        flash.off();
        assertNotEquals(flash.getSpellCoolTime(), flash.getCoolTime());
    }



    @Test
    public void testStartCount() {
        Liner liner = new Liner("top", connector);
        liner.getFlash().setCoolTime(100);

        Spell mockFlash = Mockito.spy(liner.getFlash());
        CounterLabel spyFlashIcon = Mockito.spy(Mockito.mock(CounterLabel.class));
        mockFlash.setSpellIcon(spyFlashIcon);

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

        Spell mockFlash = Mockito.spy(liner.getFlash());
        CounterLabel spyFlashIcon = Mockito.spy(Mockito.mock(CounterLabel.class));
        mockFlash.setSpellIcon(spyFlashIcon);

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

        Spell mockFlash = Mockito.spy(liner.getFlash());
        CounterLabel spyFlashIcon = Mockito.spy(Mockito.mock(CounterLabel.class));
        mockFlash.setSpellIcon(spyFlashIcon);

        assertTrue(liner.getFlash().isOn());

        mockFlash.stopCount();

        verify(spyFlashIcon, never()).startTimer();
        verify(spyFlashIcon, times(1)).repaint();
        verify(spyFlashIcon, times(1)).stopTimer();
    }
}
