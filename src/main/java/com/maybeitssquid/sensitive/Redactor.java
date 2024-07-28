package com.maybeitssquid.sensitive;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Factory for commonly used redaction strategies.
 *
 * @param <T> the type of data to protect.
 */
@SuppressWarnings("unused")
public interface Redactor<T> extends BiFunction<T, Integer, CharSequence> {

    /**
     * Commonly used masking character.
     */
    char DEFAULT_MASK = '#';

    /**
     * Returns a redactor that always returns an empty string.
     *
     * @param <T> The type of sensitive data to be protected.
     * @return a redactor that always returns an empty string.
     */
    static <T> Redactor<T> empty() {
        return (t, p) -> "";
    }

    /**
     * Wraps a redaction with a hard maximum precision.
     *
     * @param max   the maximum precision allowed.
     * @param after the redaction to wrap.
     * @param <T>   The type of sensitive data to be protected.
     * @return function to apply a maximum precision.
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
     * @param <T>    The type of sensitive data to be protected.
     * @return function to apply a maximum precision based on the input length.
     */
    static <T> Redactor<T> limited(final Function<T, Integer> length, final BiFunction<T, Integer, CharSequence> after) {
        return (t, p) -> {
            final int max = length.apply(t) / 2;
            final int exposed = p == -1 ? max : Math.min(p, max);
            return after.apply(t, exposed);
        };
    }

    /**
     * Wraps a Redactor with a precision limit of half the non-redacted length.
     *
     * @param after the redaction to wrap.
     * @return function to apply a maximum precision based on the input length.
     */
    static Redactor<CharSequence> limited(final BiFunction<CharSequence, Integer, CharSequence> after) {
        return limited(CharSequence::length, after);
    }

    /**
     * Wraps a Redactor with a default precision of half the non-redacted length. If the precision is specified, it is
     * allowed without a limit.
     *
     * @param length a function that computes the non-redacted length
     * @param after  the redaction to wrap.
     * @param <T>    The type of sensitive data to be protected.
     * @return function to apply a default number of characters redacted.
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
     * @return function to apply a default number of characters redacted.
     */
    static Redactor<CharSequence> defaulted(final BiFunction<CharSequence, Integer, CharSequence> after) {
        return defaulted(CharSequence::length, after);
    }

    /**
     * Returns a function that replaces a number of characters from an input character sequence with a masking
     * character.
     *
     * @param masking the masking character to use.
     * @return function to mask the data.
     */
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

    /**
     * Returns a function that replaces a number of characters from an input character sequence with
     * {@link #DEFAULT_MASK}.
     *
     * @return function to mask the data.
     */
    static Redactor<CharSequence> mask() {
        return mask(DEFAULT_MASK);
    }

}