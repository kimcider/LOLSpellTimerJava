package org.example.flash;

import org.example.Flash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class FlashEqualsTest {
    Flash f1;
    Flash f2;
    @BeforeEach
    public void setUp() {
        f1 = new Flash();
        f2 = new Flash();
    }

    @Test
    public void equals() {
        assertEquals(f1, f2);
    }

    @Test
    public void equals1() {
        f1.off();
        assertNotEquals(f1, f2);

        f2.off();
        assertEquals(f1, f2);
    }
    @Test
    public void equals2() {
        f1.setCoolTime(111111111);
        assertNotEquals(f1, f2);
        f2.setCoolTime(999999999);
        assertEquals(f1, f2);
    }

    @Test
    public void equals3() {
        f1.setCoolTime(150);
        assertNotEquals(f1, f2);
        f2.setCoolTime(150);
        assertEquals(f1, f2);
    }

    @Test
    public void equals4() {
        f1.setCoolTime(0);
        f2.setCoolTime(0);
        assertEquals(f1, f2);
    }

    @Test
    public void equals5() {
        f1.setFlashCoolTime(150);
        assertNotEquals(f1, f2);
        f2.setFlashCoolTime(150);
        assertEquals(f1, f2);
    }

    @Test
    public void equals6() {
        f1.setCoolTime(15);
        f2.setCoolTime(15);
        f1.setFlashCoolTime(150);
        assertNotEquals(f1, f2);
        f2.setFlashCoolTime(150);
        assertEquals(f1, f2);
    }
}
