package org.example.spell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SpellEqualsTest {
    Spell f1;
    Spell f2;
    @BeforeEach
    public void setUp() {
        f1 = new Flash();
        f2 = new Flash();

        assertEquals(300, f1.getSpellCoolTime());
        assertEquals(300, f2.getSpellCoolTime());
        assertEquals(0, f1.getCoolTime());
        assertEquals(0, f2.getCoolTime());

        assertFalse(f1.isCosmicInsight());
        assertFalse(f2.isCosmicInsight());
        assertFalse(f1.isIonianBoots());
        assertFalse(f2.isIonianBoots());
        assertEquals(f1, f2);
    }

    @Test
    public void Default() {
        assertEquals(f1, f2);
    }

    @Test
    public void SpellCoolTime(){
        f1.setSpellCoolTime(1);
        assertNotEquals(f1, f2);
        f2.setSpellCoolTime(1);
        assertEquals(f1, f2);
    }

    @Test
    public void IsOn() {
        f1.setCoolTime(0);
        f2.setCoolTime(0);
        assertEquals(f1, f2);
    }

    @Test
    public void Off() {
        f1.off();
        assertNotEquals(f1, f2);

        f2.off();
        assertEquals(f1, f2);
    }
    @Test
    public void SpellOffDifferentValue() {
        f1.setCoolTime(111111111);
        assertNotEquals(f1, f2);
        f2.setCoolTime(999999999);
        assertEquals(f1, f2);
    }

    @Test
    public void SpellOffSameValue() {
        f1.setCoolTime(150);
        assertNotEquals(f1, f2);
        f2.setCoolTime(150);
        assertEquals(f1, f2);
    }

    @Test
    public void CoolTimeAndSpellCoolTime() {
        f1.setCoolTime(15);
        assertNotEquals(f1, f2);
        f2.setCoolTime(15);
        f1.setSpellCoolTime(150);
        assertNotEquals(f1, f2);
        f2.setSpellCoolTime(150);
        assertEquals(f1, f2);
    }

    @Test
    public void CosmicInsight(){
        f1.setCosmicInsight(true);
        assertNotEquals(f1, f2);
        f2.setCosmicInsight(true);
        assertEquals(f1, f2);
    }

    @Test
    public void IonianBoots(){
        f1.setIonianBoots(true);
        assertNotEquals(f1, f2);
        f2.setIonianBoots(true);
        assertEquals(f1, f2);
    }

    @Test
    public void CosmicAndIonian(){
        f1.setCosmicInsight(true);
        assertNotEquals(f1, f2);
        f2.setCosmicInsight(true);
        assertEquals(f1, f2);
        f1.setIonianBoots(true);
        assertNotEquals(f1, f2);
        f2.setIonianBoots(true);
        assertEquals(f1, f2);
    }

    @Test
    public void AllTest(){
        CoolTimeAndSpellCoolTime();
        CosmicAndIonian();
    }
}
