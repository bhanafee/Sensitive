package com.maybeitssquid.tin.us;

import com.maybeitssquid.sensitive.Redactor;
import com.maybeitssquid.sensitive.SensitiveArray;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public sealed class TIN extends SensitiveArray<CharSequence> {

    protected static final BiFunction<CharSequence[], Integer, CharSequence> redactor;

    static {
        // Concatenate, then mask with default length
        final Function<CharSequence[], CharSequence> concatenate = Redactor.concatenate();
        final Redactor<CharSequence> mask = Redactor.defaulted(Redactor.mask(Redactor.DEFAULT_MASK));
        redactor = (t, p) -> mask.apply(concatenate.apply(t), p);
    }

    protected TIN(final CharSequence... sensitive) {
        super(sensitive);
    }

    public static TIN create(final CharSequence raw, final boolean preferEIN) {
        if (raw == null) throw new NullPointerException("Cannot create a TIN from a null");
        else return switch (raw.length()) {
            case 0 -> throw new IllegalArgumentException("Cannot parse empty TIN");
            case 9 -> preferEIN ? new EIN(raw) : new SSN(raw); // Expecting #########
            case 10 -> new EIN(raw);                           // Expecting ##-#######
            case 11 -> new SSN(raw);                           // Expecting ###-##-####
            default -> throw new IllegalArgumentException("Cannot identify TIN to parse");
        };
    }

    @SuppressWarnings("unused")
    public static TIN create(final CharSequence raw) {
        return create(raw, false);
    }

    @Override
    protected BiFunction<CharSequence[], Integer, CharSequence> redactor() {
        return redactor;
    }
}

@SuppressWarnings("unused")
final class SSN extends TIN {
    public static final String SSN_REGEX = "(?<area>\\d{3})-?(?<group>\\d{2})-?(?<serial>\\d{4})";

    private static final Pattern SSN_PATTERN = Pattern.compile(SSN_REGEX);

    private static final BiFunction<CharSequence[], Integer, CharSequence> alternate;

    static {
        // Redact, then insert the delimiters
        alternate = (t, p) -> {
            final CharSequence redacted = TIN.redactor.apply(t, p);
            final StringBuilder buffer = redacted instanceof StringBuilder ? (StringBuilder) redacted : new StringBuilder(redacted);
            return buffer.insert(5, Redactor.DEFAULT_DELIMITER).insert(3, Redactor.DEFAULT_DELIMITER);
        };
    }

    private static String[] parse(final CharSequence value) {
        final Matcher matcher = SSN_PATTERN.matcher(value);
        if (matcher.matches()) {
            return new String[]{
                    matcher.group("area"),
                    matcher.group("group"),
                    matcher.group("serial")
            };
        } else {
            // Do not include the bad value here, to avoid sensitive data being logged
            throw new IllegalArgumentException("Invalid SSN format");
        }
    }

    public SSN(final CharSequence value) {
        super(parse(value));
    }

    public SSN(final CharSequence area, final CharSequence group, final CharSequence serial) {
        super(area, group, serial);
    }

    public SSN(final int area, final int group, final int serial) {
        this(
                String.format(Locale.US, "%03d", area),
                String.format(Locale.US, "%02d", group),
                String.format(Locale.US, "%04d", serial)
        );
    }

    @Override
    protected BiFunction<CharSequence[], Integer, CharSequence> alternate() {
        return alternate;
    }

    public CharSequence getArea() {
        return sensitive[0];
    }

    public CharSequence getGroup() {
        return sensitive[1];
    }

    public CharSequence getSerial() {
        return sensitive[2];
    }

}

@SuppressWarnings("unused")
final class EIN extends TIN {
    public static final String EIN_REGEX = "(?<prefix>\\d{2})-?(?<serial>\\d{7})";

    private static final Pattern EIN_PATTERN = Pattern.compile(EIN_REGEX);

    private static final BiFunction<CharSequence[], Integer, CharSequence> alternate;

    static {
        // Redact, then insert the delimiter
        alternate = (t, p) -> {
            final CharSequence redacted = TIN.redactor.apply(t, p);
            final StringBuilder buffer = redacted instanceof StringBuilder ? (StringBuilder) redacted : new StringBuilder(redacted);
            return buffer.insert(2, Redactor.DEFAULT_DELIMITER);
        };
    }

    private static String[] parse(final CharSequence value) {
        final Matcher matcher = EIN_PATTERN.matcher(value);
        if (matcher.matches()) {
            return new String[]{
                    matcher.group("prefix"),
                    matcher.group("serial")
            };
        } else {
            // Do not include the bad value here, to avoid sensitive data being logged
            throw new IllegalArgumentException("Invalid EIN format");
        }
    }

    public EIN(final CharSequence value) {
        super(parse(value));
    }

    public EIN(final CharSequence prefix, final CharSequence serial) {
        super(prefix, serial);
    }

    public EIN(final int prefix, final int serial) {
        this(
                String.format(Locale.US, "%02d", prefix),
                String.format(Locale.US, "%07d", serial)
        );
    }

    @Override
    protected BiFunction<CharSequence[], Integer, CharSequence> alternate() {
        return alternate;
    }

    public CharSequence getPrefix() {
        return sensitive[0];
    }

    public CharSequence getSerial() {
        return sensitive[1];
    }
}