package com.maybeitssquid.sensitive;

import java.io.IOException;
import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.function.BiFunction;

/**
 * Container for sensitive data to protect it being inadvertently rendered as a plain {@link String}.
 * <p>
 * <strong>There is no accessor for the raw sensitive data.</strong> Implementors of subclasses should access the value
 * using the {@code final protected transient} field {@code sensitive}.
 * /p>
 *
 * @param <T> The type of sensitive data to be protected.
 */
public class Sensitive<T> implements Formattable {

    /** The data being protected. */
    final protected transient T sensitive;

    /**
     * Creates a wrapper for a sensitive object.
     *
     * @param sensitive the data to protect.
     */
    public Sensitive(final T sensitive) {
        if (sensitive == null) throw new NullPointerException("Sensitive value cannot be null");
        this.sensitive = sensitive;
    }

    /**
     * Gets the default redactor for this data.
     *
     * @return a redactor that always returns an empty string.
     */
    protected BiFunction<T, Integer, CharSequence> redactor() {
        return Redactor.empty();
    }

    /**
     * Gets the alternate redactor for this data, invoked by using a '#' in a format string.
     *
     * @return returns the {@link #redactor() default redactor}.
     */
    protected BiFunction<T, Integer, CharSequence> alternate() {
        return redactor();
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        final boolean alternate = (flags & FormattableFlags.ALTERNATE) == FormattableFlags.ALTERNATE;
        final boolean upper = ((flags & FormattableFlags.UPPERCASE) == FormattableFlags.UPPERCASE);
        final boolean left = ((flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY);

        final StringBuilder buffer;
        if (alternate) {
            buffer = new StringBuilder(alternate().apply(this.sensitive, precision));
        } else {
            buffer = new StringBuilder(redactor().apply(this.sensitive, precision));
        }

        if (upper) {
            buffer.replace(0, buffer.length(), buffer.toString().toUpperCase(formatter.locale()));
        }

        final int pad = width - buffer.length();
        if (pad > 0) {
            if (left) {
                buffer.append(" ".repeat(pad));
            } else {
                buffer.insert(0, " ".repeat(pad));
            }
        }

        try {
            formatter.out().append(buffer);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the result of applying default string formatting to this value. Equivalent to
     * {@code String.format("%s", this)}.
     *
     * @return the result of applying default string formatting to this value.
     */
    @Override
    public final String toString() {
        return String.format("%s", this);
    }

    /**
     * Returns the hash of the enclosed sensitive data.
     *
     * @return the hash of the enclosed sensitive data.
     */
    @Override
    public int hashCode() {
        return sensitive.hashCode();
    }

    /**
     * Returns true if the types match and the enclosed sensitive data are equal.
     *
     * @param o {@inheritDoc}
     * @return if the types match and the enclosed sensitive data are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.sensitive.equals(((Sensitive<?>) o).sensitive);
    }

}
