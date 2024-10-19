package org.example.liner;

import org.example.liner.spell.impl.Flash;
import org.example.liner.spell.Spell;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

public class setLinerTest {
    @Test
    void setLinerUpdatesFlash() {
        Liner liner1 = new Liner();
        Spell spell = Mockito.spy(new Flash());
        liner1.setFlash(spell);
        assertTrue(liner1.getFlash().isOn());

        Liner liner2 = new Liner();
        liner2.offSpell(liner2.getFlash());

        liner1.setLiner(liner2);

        assertFalse(liner1.getFlash().isOn());
    }

    @Test
    void setLinerUpdatesCosmicInsight() {
        Liner liner1 = new Liner();
        Liner liner2 = new Liner();
        liner2.setCosmicInsight(true);

        liner1.setLiner(liner2);

        assertTrue(liner1.isCosmicInsight());
    }

    @Test
    void setLinerUpdatesIonianBoots() {
        Liner liner1 = new Liner();
        Liner liner2 = new Liner();
        liner2.setIonianBoots(true);

        liner1.setLiner(liner2);

        assertTrue(liner1.isIonianBoots());
    }

    @Test
    void setLinerWithNullModelDoesNotThrowException() {
        Liner liner1 = new Liner();

        assertDoesNotThrow(() -> liner1.setLiner(null));
    }
}
