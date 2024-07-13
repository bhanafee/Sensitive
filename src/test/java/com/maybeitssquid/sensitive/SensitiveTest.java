package com.maybeitssquid.sensitive;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SensitiveTest {

    private final Object containedObj = new Object();

    private final String containedString = "test case";

    private final Sensitive<Object> sensitiveObj = new Sensitive<>(containedObj);

    private final Sensitive<String> sensitiveString = new Sensitive<>(containedString);

    @Test
    @SuppressWarnings("all")
    void testSensitive() {
        assertNotNull(new Sensitive<Object>(new Object()));
        assertThrows(NullPointerException.class, () -> new Sensitive<Object>(null));
    }

    @Test
    void formatTo() {
        // Default always renders an empty string
        assertEquals("", String.format("%s", sensitiveString));
        assertEquals("", String.format("%s", sensitiveObj));

        // Check width
        assertEquals(" ", String.format("%1s", sensitiveObj));
        assertEquals("  ", String.format("%2s", sensitiveObj));
        assertEquals("   ", String.format("%3s", sensitiveObj));

        // Smoke test flags: justification, upper, alternate
        assertEquals(" ", String.format("%-1s", sensitiveObj));
        assertEquals(" ", String.format("%1S", sensitiveObj));
        assertEquals(" ", String.format("%#1s", sensitiveObj));
    }


    @Test
    void testToString() {
        assertEquals("", sensitiveString.toString());
        assertEquals("", sensitiveObj.toString());
    }


    @Test
    void testHashCode() {
        assertEquals(containedObj.hashCode(), sensitiveObj.hashCode());
        assertEquals(containedString.hashCode(), sensitiveString.hashCode());
        assertNotEquals(containedObj.hashCode(), sensitiveString.hashCode());
    }

    @SuppressWarnings("all")
    @Test
    void testEquals() {
        assertTrue(sensitiveObj.equals(sensitiveObj));
        assertTrue(sensitiveString.equals(sensitiveString));

        assertTrue(sensitiveString.equals(new Sensitive<>(containedString)));
        assertFalse(sensitiveObj.equals(null));
        assertFalse(sensitiveObj.equals("foo"));
        assertFalse(sensitiveObj.equals(sensitiveString));
        assertFalse(sensitiveString.equals(sensitiveObj));
    }

}
