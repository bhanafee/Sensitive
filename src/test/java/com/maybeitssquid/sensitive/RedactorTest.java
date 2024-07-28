package com.maybeitssquid.sensitive;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedactorTest {

    private final Redactor<Object> after;
    private final BiFunction<CharSequence, Integer, CharSequence> afterString;
    private final Function<Object, Integer> length;

    public RedactorTest(final @Mock Redactor<Object> after, final @Mock Redactor<CharSequence> afterString, final @Mock Function<Object, Integer> length) {
        this.after = after;
        this.afterString = afterString;
        this.length = length;
    }

    @Test
    void testEmpty() {
        final Redactor<Object> test = Redactor.empty();
        assertEquals("", test.apply(new Object(), 1));
    }

    @Test
    void testLimitedMax() {
        final Object obj = new Object();
        final Redactor<Object> test = Redactor.limited(2, after);

        test.apply(obj, 1);
        verify(after).apply(obj, 1);

        test.apply(obj, 2);
        test.apply(obj, 3);
        test.apply(obj, -1);
        verify(after, times(3)).apply(obj, 2);
    }

    @Test
    void testLimitedCharSequence() {
        final Redactor<CharSequence> test = Redactor.limited(afterString);

        test.apply("abcd", 1);
        verify(afterString).apply("abcd", 1);

        test.apply("abcd", 2);
        test.apply("abcd", 3);
        test.apply("abcd", -1);
        verify(afterString, times(3)).apply("abcd", 2);
    }

    @Test
    void testLimitedFunction() {
        final Redactor<Object> test = Redactor.limited(length, after);
        when(length.apply("abcd")).thenReturn(4);

        test.apply("abcd", 1);
        verify(after).apply("abcd", 1);

        test.apply("abcd", 2);
        test.apply("abcd", 3);
        test.apply("abcd", -1);
        verify(after, times(3)).apply("abcd", 2);
    }

    @Test
    void testDefaultedCharSequence() {
        final Redactor<CharSequence> test = Redactor.defaulted(afterString);

        test.apply("abcd", 1);
        verify(afterString).apply("abcd", 1);

        test.apply("abcd", 2);
        test.apply("abcd", -1);
        verify(afterString, times(2)).apply("abcd", 2);

        test.apply("abcd", 3);
        verify(afterString).apply("abcd", 3);
    }

    @Test
    void testDefaultedFunction() {
        final Redactor<Object> test = Redactor.defaulted(length, after);
        when(length.apply("abcd")).thenReturn(4);

        test.apply("abcd", 1);
        verify(after).apply("abcd", 1);

        test.apply("abcd", 2);
        test.apply("abcd", -1);
        verify(after, times(2)).apply("abcd", 2);

        test.apply("abcd", 3);
        verify(after).apply("abcd", 3);
    }

    @Test
    void testMaskChar() {
        final Redactor<CharSequence> test = Redactor.mask('*');

        assertEquals("***", test.apply("abc", -1));
        assertEquals("***", test.apply("abc", 0));
        assertEquals("**c", test.apply("abc", 1));
        assertEquals("*bc", test.apply("abc", 2));
        assertEquals("abc", test.apply("abc", 3));
        assertEquals("abc", test.apply("abc", 4));
    }

    @Test
    void testMask() {
        final Redactor<CharSequence> test = Redactor.mask();

        assertEquals("###", test.apply("abc", -1));
        assertEquals("###", test.apply("abc", 0));
        assertEquals("##c", test.apply("abc", 1));
        assertEquals("#bc", test.apply("abc", 2));
        assertEquals("abc", test.apply("abc", 3));
        assertEquals("abc", test.apply("abc", 4));
    }
}
