package org.example.liner;

import org.example.liner.spell.CounterLabel;
import org.example.liner.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class onOffSpellTest {
    Liner liner;
    Spell ghost;
    @BeforeEach
    public void setUp() {
        liner = new Liner();

        ghost = new Spell(240, "flash.jpg"){};
        CounterLabel spyFlashIcon = Mockito.mock(CounterLabel.class);
        ghost.setSpellIcon(spyFlashIcon);
    }

    @Test
    void offFlash_DoesNotReduceCoolTimeWithoutCosmicInsightOrIonianBoots() {
        liner.offSpell(liner.getSpell1());

        assertEquals(300, liner.getSpell1().getCoolTime());
    }
    @Test
    void offFlash_ReducesCoolTimeWithCosmicInsight() {
        liner.setCosmicInsight(true);

        liner.offSpell(liner.getSpell1());
        assertEquals(254, liner.getSpell1().getCoolTime());
    }

    @Test
    void offFlash_ReducesCoolTimeWithIonianBoots() {
        liner.setIonianBoots(true);

        liner.offSpell(liner.getSpell1());
        assertEquals(272, liner.getSpell1().getCoolTime());
    }

    @Test
    void offFlash_ReducesCoolTimeWithBothCosmicInsightAndIonianBoots() {
        liner.setCosmicInsight(true);
        liner.setIonianBoots(true);

        liner.offSpell(liner.getSpell1());
        assertEquals(234, liner.getSpell1().getCoolTime());
    }

    @Test
    void onFlash(){
        liner.offSpell(liner.getSpell1());
        assertFalse(liner.getSpell1().isOn());

        liner.onSpell(liner.getSpell1());
        assertEquals(0, liner.getSpell1().getCoolTime());
    }


    @Test
    void offGhost_DoesNotReduceCoolTimeWithoutCosmicInsightOrIonianBoots() {
        Liner liner = new Liner();

        liner.offSpell(ghost);

        assertEquals(240, ghost.getCoolTime());
    }
    @Test
    void ooffGhost_ReducesCoolTimeWithCosmicInsight() {
        Liner liner = new Liner();
        liner.setCosmicInsight(true);

        liner.offSpell(ghost);
        assertEquals(203, ghost.getCoolTime());
    }

    @Test
    void offGhost_ReducesCoolTimeWithIonianBoots() {
        Liner liner = new Liner();
        liner.setIonianBoots(true);

        liner.offSpell(ghost);
        assertEquals(218, ghost.getCoolTime());
    }

    @Test
    void offGhost_ReducesCoolTimeWithBothCosmicInsightAndIonianBoots() {
        Liner liner = new Liner();
        liner.setCosmicInsight(true);
        liner.setIonianBoots(true);

        liner.offSpell(ghost);
        assertEquals(187, ghost.getCoolTime());
    }

    @Test
    void onGhost(){
        liner.offSpell(ghost);
        assertFalse(ghost.isOn());

        liner.onSpell(ghost);
        assertEquals(0, ghost.getCoolTime());
    }

}
