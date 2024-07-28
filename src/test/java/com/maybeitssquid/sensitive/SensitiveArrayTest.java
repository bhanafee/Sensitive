package com.maybeitssquid.sensitive;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.function.Function;

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

    @Test
    void testConcatenate() {
        final Function<CharSequence[], CharSequence> test = SensitiveArray.concatenate();
        assertEquals("abcd", test.apply(new CharSequence[]{"a", "b", "cd"}));
    }

    @Test
    void testDelimitCharSequence() {
        final Function<CharSequence[], CharSequence> test = SensitiveArray.delimit("++");
        assertEquals("a++b++cd", test.apply(new CharSequence[]{"a", "b", "cd"}));
    }

    @Test
    void testDelimitChar() {
        final Function<CharSequence[], CharSequence> test = SensitiveArray.delimit('+');
        assertEquals("a+b+cd", test.apply(new CharSequence[]{"a", "b", "cd"}));
    }

    @Test
    void testDelimitFunction() {
        final Function<Object[], CharSequence> test = SensitiveArray.delimit("-", Object::toString);
        assertEquals("a-b-cd", test.apply(new String[]{"a", "b", "cd"}));
    }
}
