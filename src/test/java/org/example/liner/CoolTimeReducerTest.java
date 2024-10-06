package org.example.liner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CoolTimeReducerTest {
    CoolTimeReducer reducer;
    ImageIcon baseIcon;
    ImageIcon checkMark;
    @BeforeEach
    void setUp() {
        baseIcon = mock(ImageIcon.class);
        checkMark = mock(ImageIcon.class);
        reducer = new CoolTimeReducer(baseIcon, checkMark, 50, 0, 0);
        assertFalse(reducer.isOn());
    }

    @Test
    void setOn_updatesIconToCheckMarkWhenOnIsTrue() {
        reducer.setOn(true);

        assertEquals(checkMark, reducer.getIcon());
    }

    @Test
    void setOn_updatesIconToBaseIconWhenOnIsFalse() {
        reducer.setOn(false);

        assertEquals(baseIcon, reducer.getIcon());
    }

    @Test
    void isOn_returnsTrueWhenOnIsTrue() {
        reducer.setOn(true);

        assertTrue(reducer.isOn());
    }

    @Test
    void isOn_returnsFalseWhenOnIsFalse() {
        reducer.setOn(false);

        assertFalse(reducer.isOn());
    }
}
