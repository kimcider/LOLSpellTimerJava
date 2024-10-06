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
        liner.touchCoolTimeReducer(liner.getCosmicInsightIcon());
        assertTrue(liner.isCosmicInsight());
        liner.touchCoolTimeReducer(liner.getCosmicInsightIcon());
        assertFalse(liner.isCosmicInsight());
    }

    @Test
    void touchIonianBootsTogglesState() {
        liner.touchCoolTimeReducer(liner.getIonianBootsIcon());
        assertTrue(liner.isIonianBoots());
        liner.touchCoolTimeReducer(liner.getIonianBootsIcon());
        assertFalse(liner.isIonianBoots());
    }
}
