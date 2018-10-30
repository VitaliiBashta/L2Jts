package org.mmocore.commons.crypt;

import java.math.BigInteger;
import java.text.DecimalFormat;

public class Service {
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    public static String right(final long number, final int blanks) {
        final StringBuilder sb = new StringBuilder(String.valueOf(number));
        while (sb.length() < blanks) {
            sb.insert(0, ' ');
        }
        return sb.toString();
    }

    public static String decformat(final long number, final String mask) {
        final DecimalFormat df = new DecimalFormat(mask);
        return df.format(number);
    }

    public static String hexformat(final long value, final int nibbles) {
        final StringBuilder sb = new StringBuilder(Long.toHexString(value));
        while (sb.length() < nibbles) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }

    public static String format(final byte[] bytes) {
        return format(bytes, false);
    }

    private static StringBuilder insertBlanks(final StringBuilder sb, final int group, final char groupChar) {
        final int bytecount = sb.length() / 2; // we expect a hex string
        if (bytecount <= group) {
            return sb; // avoid unnecessary action
        }
        final StringBuilder sb2 = new StringBuilder(sb.length() + (bytecount / group - 1));
        final int group2 = group * 2;

        for (int i = 0; i < sb.length(); i++) {
            if ((i > 0) && (i % (group2)) == 0) {
                sb2.append(groupChar);
            }
            sb2.append(sb.charAt(i));
        }
        return sb2;

    }

    public static String format(final byte[] bytes, final boolean uppercase, final int group, final char groupChar) {
        if (bytes == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        int b;
        for (int i = 0; i < bytes.length; i++) {
            b = bytes[i] & 0xFF;
            sb.append(HEX[b >>> 4]);
            sb.append(HEX[b & 0x0F]);
        }
        if (group > 0) {
            sb = insertBlanks(sb, group, groupChar);
        }
        return (uppercase ? sb.toString().toUpperCase() : sb.toString());
    }

    public static String formatAsBits(final byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(bytes.length);
        final BigInteger big = new BigInteger(1, bytes);
        sb.append(big.toString(2)); // dual
        while (sb.length() < (bytes.length * 8)) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }

    public static String format(final byte[] bytes, final boolean uppercase) {
        return format(bytes, false, 0, ' ');
    }
}
