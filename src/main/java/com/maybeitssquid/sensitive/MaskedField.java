package com.maybeitssquid.sensitive;

import java.util.function.BiFunction;

@SuppressWarnings("unused")
public class MaskedField extends Sensitive<CharSequence> {

    private final BiFunction<CharSequence, Integer, CharSequence> masking;

    public MaskedField(final CharSequence sensitive, final char mask) {
        super(sensitive);
        this.masking = Redactor.mask(mask);
    }

    public MaskedField(final CharSequence sensitive) {
        this(sensitive, Redactor.DEFAULT_MASK);
    }

    @Override
    protected BiFunction<CharSequence, Integer, CharSequence> redactor() {
        return this.masking;
    }
}
