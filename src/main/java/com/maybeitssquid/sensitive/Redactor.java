package com.maybeitssquid.sensitive;

import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("unused")
public interface Redactor<T> extends BiFunction<T, Integer, CharSequence> {

    char DEFAULT_MASK = '#';
    char DEFAULT_DELIMITER = '-';

    static <T> Redactor<T> empty() {
        return (t, p) -> "";
    }

    /**
     * Wraps a redaction with a hard maximum precision.
     *
     * @param max   the maximum precision allowed.
     * @param after the redaction to wrap.
     */
    static <T> Redactor<T> limited(final int max, final BiFunction<T, Integer, CharSequence> after) {
        return (t, p) -> {
            final int exposed = p == -1 ? max : Math.min(p, max);
            return after.apply(t, exposed);
        };
    }

    /**
     * Wraps a Redactor with a precision limit of half the non-redacted length.
     *
     * @param length a function that computes the non-redacted length
     * @param after  the redaction to wrap.
     */
    static <T> Redactor<T> limited(final Function<T, Integer> length, final BiFunction<T, Integer, CharSequence> after) {
        return (t, p) -> {
            final int max = length.apply(t) / 2;
            final int exposed = p == -1 ? max : Math.min(max, p);
            return after.apply(t, exposed);
        };
    }

    /**
     * Wraps a Redactor with a precision limit of half the non-redacted length.
     *
     * @param after the redaction to wrap.
     */
    static Redactor<CharSequence> limited(final BiFunction<CharSequence, Integer, CharSequence> after) {
        return limited(CharSequence::length, after);
    }

    /**
     * Wraps a Redactor with a default precision of half the non-redacted length. If the precision is specified, it is
     * allowed without a limit.
     *
     * @param length a function that computes the non-redacted length
     * @param after the redaction to wrap.
     */
    static <T> Redactor<T> defaulted(final Function<T, Integer> length, final BiFunction<T, Integer, CharSequence> after) {
        return (t, p) -> {
            final int exposed = p == -1 ? length.apply(t) / 2 : p;
            return after.apply(t, exposed);
        };
    }

    /**
     * Wraps a Redactor with a default precision of half the total length. If the precision is specified, it is allowed
     * without a limit.
     *
     * @param after the redaction to wrap.
     */
    static Redactor<CharSequence> defaulted(final BiFunction<CharSequence, Integer, CharSequence> after) {
        return defaulted(CharSequence::length, after);
    }

    static Redactor<CharSequence> mask(final char masking) {
        return (t, p) -> {
            final int len = t.length();
            if (p >= len) {
                return t;
            } else if (p <= 0) {
                return Character.toString(masking).repeat(len);
            } else {
                return Character.toString(masking).repeat(len - p) +
                        t.subSequence(len - p, len);
            }
        };
    }

    static Function<CharSequence[], CharSequence> concatenate() {
        return (t) -> String.join("", t);
    }

    static Function<CharSequence[], CharSequence> delimit(final CharSequence delimiter) {
        return (t) -> String.join(delimiter, t);
    }

    static Function<CharSequence[], CharSequence> delimit(final char delimiter) {
        return delimit(String.valueOf(delimiter));
    }

    static <T> Function<T[], CharSequence> delimit(final CharSequence delimiter, final Function <T, CharSequence> extractor) {
        return (t) -> {
            final StringBuilder buffer = new StringBuilder();
            buffer.append(t[0]);
            for (int i = 1; i < t.length; i++) buffer.append(delimiter).append(t[i]);
            return buffer.toString();
        };
    }

}
