package org.example.liner;

import org.example.liner.spell.impl.Barrier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReloadSpellTest {
    private String path = "org.example.liner.spell.impl.";
    @Test
    public void testChangeSpell() {
        Liner liner = new Liner("top", null);
        liner.changeSpell(liner.getSpell1(), path + "Barrier");
        assertEquals(liner.getSpell1().getClass(), Barrier.class);
    }
}
