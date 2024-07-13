package com.maybeitssquid.sensitive;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class MaskedFieldTest {
    static final String field = "test case";

    @SuppressWarnings("all")
    @Test
    void testMaskedField() {
        assertNotNull(new MaskedField(""));
        assertThrows(NullPointerException.class, () -> new MaskedField(null));
        assertNotNull(new MaskedField("a").redactor());
    }

    @Test
    void testFormatTo() {
        final MaskedField test = new MaskedField(field);
        assertEquals("#########", String.format("%s", test));
        assertEquals("#########", String.format("%S", test));
        assertEquals("   #########", String.format("%12s", test));
        assertEquals("#########   ", String.format("%-12s", test));

        assertEquals("#####case", String.format("%.4s", test));
        assertEquals("######ASE", String.format("%.3S", test));
        assertEquals("   ###t case", String.format("%12.6s", test));
        assertEquals("#######se   ", String.format("%-12.2s", test));

        assertEquals("test case", String.format("%9.9s", test));
        assertEquals("test case", String.format("%#.9s", test));
        assertEquals("TEST CASE", String.format("%.9S", test));
        assertEquals(" test case", String.format("%10.9s", test));
        assertEquals("   test case", String.format("%12.9s", test));
        assertEquals("test case ", String.format("%-10.10s", test));
        assertEquals("test case   ", String.format("%-12.12s", test));
    }

    @Test
    void testToString() {
        assertEquals("#########", new MaskedField(field).toString());
    }
}
