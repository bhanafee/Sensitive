package com.maybeitssquid.sensitive;

import java.util.function.BiFunction;

/**
 * Wrapper for a sensitive text field.
 */
@SuppressWarnings("unused")
public class MaskedField extends Sensitive<CharSequence> {

    private final BiFunction<CharSequence, Integer, CharSequence> masking;

    /**
     * Creates a wrapper for a sensitive text field.
     *
     * @param sensitive the sensitive text.
     * @param mask      the masking character used to replace redacted characters.
     */
    public MaskedField(final CharSequence sensitive, final char mask) {
        super(sensitive);
        this.masking = Redactor.mask(mask);
    }

    /**
     * Creates a wrapper for a sensitive text field. Uses {@link Redactor#DEFAULT_MASK} to replace redacted characters.
     *
     * @param sensitive the sensitive text.
     */
    public MaskedField(final CharSequence sensitive) {
        this(sensitive, Redactor.DEFAULT_MASK);
    }

    @Override
    protected BiFunction<CharSequence, Integer, CharSequence> redactor() {
        return this.masking;
    }
}
