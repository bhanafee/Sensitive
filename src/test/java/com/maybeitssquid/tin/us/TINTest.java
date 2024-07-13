package com.maybeitssquid.tin.us;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class TINTest {

    @Test
    void testTIN() {
        assertInstanceOf(SSN.class, TIN.create("123-45-6789"));
        assertInstanceOf(EIN.class, TIN.create("12-3456789"));
        assertInstanceOf(SSN.class, TIN.create("123456789"));
        assertInstanceOf(EIN.class, TIN.create("123456789", true));
        assertThrows(NullPointerException.class, () -> TIN.create(null));
        assertThrows(IllegalArgumentException.class, () -> TIN.create(""));
        assertThrows(IllegalArgumentException.class, () -> TIN.create("1"));
        assertThrows(IllegalArgumentException.class, () -> TIN.create("0123456789012345"));
    }

    @Test
    void testSSN() {
        final SSN ssn = new SSN("123-45-6789");
        assertEquals("123", ssn.getArea());
        assertEquals("45", ssn.getGroup());
        assertEquals("6789", ssn.getSerial());

        assertEquals("#####6789", ssn.toString());
        assertEquals("#####6789", String.format(Locale.US, "%s", ssn));
        assertEquals("#####6789", String.format(Locale.US, "%s", ssn));
        assertEquals("#####6789", String.format(Locale.US, "%9s", ssn));
        assertEquals(" #####6789", String.format(Locale.US, "%10s", ssn));
        assertEquals("#####6789 ", String.format(Locale.US, "%-10s", ssn));
        assertEquals("#######89", String.format(Locale.US, "%.2s", ssn));
        assertEquals("##3456789", String.format(Locale.US, "%.7s", ssn));
        assertEquals("###-##-6789", String.format(Locale.US, "%#s", ssn));
        assertEquals("#23-45-6789", String.format(Locale.US, "%#9.8s", ssn));
        assertEquals("##3-45-6789", String.format(Locale.US, "%#9.7s", ssn));
        assertEquals("###-45-6789", String.format(Locale.US, "%#9.6s", ssn));

        assertEquals("#####6789", new SSN("123456789").toString());
        assertEquals("#####6789", new SSN("123","45","6789").toString());
        assertEquals("#####6789", new SSN(123,45,6789).toString());

        assertThrows(NullPointerException.class, () -> new SSN(null));
        assertThrows(IllegalArgumentException.class, () -> new SSN(""));
        assertThrows(IllegalArgumentException.class, () -> new SSN("00"));
    }

    @Test
    void testEIN() {
        final EIN ein = new EIN("12-3456789");
        assertEquals("12",ein.getPrefix());
        assertEquals("3456789",ein.getSerial());

        assertEquals("#####6789", ein.toString());
        assertEquals("#####6789", String.format(Locale.US, "%s", ein));
        assertEquals("#####6789", String.format(Locale.US, "%9s", ein));
        assertEquals(" #####6789", String.format(Locale.US, "%10s", ein));
        assertEquals("#####6789 ", String.format(Locale.US, "%-10s", ein));
        assertEquals("#######89", String.format(Locale.US, "%.2s", ein));
        assertEquals("##3456789", String.format(Locale.US, "%.7s", ein));
        assertEquals("##-###6789", String.format(Locale.US, "%#s", ein));
        assertEquals("#2-3456789", String.format(Locale.US, "%#9.8s", ein));
        assertEquals("##-3456789", String.format(Locale.US, "%#9.7s", ein));
        assertEquals("##-#456789", String.format(Locale.US, "%#9.6s", ein));

        assertEquals("#####6789", new EIN("123456789").toString());
        assertEquals("#####6789", new EIN("12","3456789").toString());
        assertEquals("#####6789", new EIN(12,3456789).toString());

        assertThrows(NullPointerException.class, () -> new EIN(null));
        assertThrows(IllegalArgumentException.class, () -> new EIN(""));
        assertThrows(IllegalArgumentException.class, () -> new EIN("00"));
    }

}
