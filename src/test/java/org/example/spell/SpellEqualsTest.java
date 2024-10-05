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

        assertEquals(f1, f2);
    }

    @Test
    public void Default() {
        assertEquals(f1, f2);
    }

    @Test
    public void IsOn() {
        f1.setCoolTime(0);
        f2.setCoolTime(0);
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
}
