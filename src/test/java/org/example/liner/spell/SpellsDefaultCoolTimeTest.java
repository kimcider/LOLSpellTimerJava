package org.example.liner.spell;

import org.example.liner.spell.impl.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpellsDefaultCoolTimeTest {
    @Test
    public void testTP(){
        Teleport teleport = new Teleport();

        assertEquals(360, teleport.getSpellCoolTime());
    }

    @Test
    public void testFlash(){
        Flash flash = new Flash();

        assertEquals(300, flash.getSpellCoolTime());
    }

    @Test
    public void testBarrier(){
        Barrier barrier = new Barrier();

        assertEquals(180, barrier.getSpellCoolTime());
    }

    @Test
    public void testCleanse(){
        Cleanse cleanse = new Cleanse();

        assertEquals(240, cleanse.getSpellCoolTime());
    }

    @Test
    public void testExhaust(){
        Exhaustion exhaust = new Exhaustion();

        assertEquals(240, exhaust.getSpellCoolTime());
    }

    @Test
    public void testGhost(){
        Ghost ghost = new Ghost();

        assertEquals(240, ghost.getSpellCoolTime());
    }

    @Test
    public void testHeal(){
        Heal heal = new Heal();

        assertEquals(240, heal.getSpellCoolTime());
    }

    @Test
    public void testIgnite(){
        Ignite ignite = new Ignite();

        assertEquals(180, ignite.getSpellCoolTime());
    }

    @Test
    public void testSmite(){
        Smite smite = new Smite();

        assertEquals(0, smite.getSpellCoolTime());
    }
}
