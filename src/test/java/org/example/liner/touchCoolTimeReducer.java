package org.example.liner;

import org.example.Liner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class touchCoolTimeReducer {
    Liner liner;
    @BeforeEach
    public void setUp() {
        liner = new Liner();
        assertFalse(liner.isCosmicInsight());
        assertFalse(liner.isIonianBoots());
    }

    @Test
    void touchCosmicInsightTogglesState() {
        liner.touchCosmicInsight();
        assertTrue(liner.isCosmicInsight());
        liner.touchCosmicInsight();
        assertFalse(liner.isCosmicInsight());
    }

    @Test
    void touchIonianBootsTogglesState() {
        liner.touchIonianBoots();
        assertTrue(liner.isIonianBoots());
        liner.touchIonianBoots();
        assertFalse(liner.isIonianBoots());
    }
}
