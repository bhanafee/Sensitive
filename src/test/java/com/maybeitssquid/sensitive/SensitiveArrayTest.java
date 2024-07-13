package com.maybeitssquid.sensitive;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class SensitiveArrayTest {

    static Object[] test = new Object[]{"a", "b"};

    @SuppressWarnings("all")
    @Test
    void testSensitiveArray() {
        assertNotNull(new SensitiveArray<Object>(test));
        assertThrows(NullPointerException.class, () -> new SensitiveArray<Object>(null));
    }

    @Test
    void testHashCode() {
        assertEquals(Arrays.hashCode(test), new SensitiveArray<Object>(test).hashCode());
    }

    @SuppressWarnings("all")
    @Test
    void testEquals() {
        final SensitiveArray<Object> sa = new SensitiveArray<>(test);
        assertTrue(sa.equals(sa));
        assertFalse(sa.equals(null));
        assertFalse(sa.equals(new Object()));
        assertTrue(sa.equals(new SensitiveArray<>(test)));
        assertTrue(sa.equals(new SensitiveArray<Object>(new Object[]{"a", "b"})));
        assertFalse(sa.equals(new SensitiveArray<Object>(new Object[]{"a", "b", "c"})));
    }
}
