package org.mmocore.commons.crypt.adapt.gnu.crypto.util;

// ----------------------------------------------------------------------------
// $Id: Util.java,v 1.10 2003/09/27 00:03:01 raif Exp $
//
// Copyright (C) 2001, 2002, 2003 Free Software Foundation, Inc.
//
// This file is part of GNU Crypto.
//
// GNU Crypto is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
//
// GNU Crypto is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; see the file COPYING.  If not, write to the
//
//    Free Software Foundation Inc.,
//    59 Temple Place - Suite 330,
//    Boston, MA 02111-1307
//    USA
//
// Linking this library statically or dynamically with other modules is
// making a combined work based on this library.  Thus, the terms and
// conditions of the GNU General Public License cover the whole
// combination.
//
// As a special exception, the copyright holders of this library give
// you permission to link this library with independent modules to
// produce an executable, regardless of the license terms of these
// independent modules, and to copy and distribute the resulting
// executable under terms of your choice, provided that you also meet,
// for each linked independent module, the terms and conditions of the
// license of that module.  An independent module is a module which is
// not derived from or based on this library.  If you modify this
// library, you may extend this exception to your version of the
// library, but you are not obligated to do so.  If you do not wish to
// do so, delete this exception statement from your version.
// ----------------------------------------------------------------------------

import java.math.BigInteger;

/**
 * <p>A collection of utility methods used throughout this project.</p>
 *
 * @version $Revision: 1.10 $
 */
public class Util {

    // Constants and variables
    // -------------------------------------------------------------------------

    // Hex charset
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    // Constructor(s)
    // -------------------------------------------------------------------------

    /**
     * Trivial constructor to enforce Singleton pattern.
     */
    private Util() {
        super();
    }

    // Class methods
    // -------------------------------------------------------------------------

    /**
     * <p>Returns a string of hexadecimal digits from a byte array. Each byte is
     * converted to 2 hex symbols; zero(es) included.</p>
     * <p/>
     * <p>This method calls the method with same name and three arguments as:</p>
     * <p/>
     * <pre>
     *    toString(ba, 0, ba.length);
     * </pre>
     *
     * @param ba the byte array to convert.
     * @return a string of hexadecimal characters (two for each byte)
     * representing the designated input byte array.
     */
    public static String toString(final byte[] ba) {
        return toString(ba, 0, ba.length);
    }

    /**
     * <p>Returns a string of hexadecimal digits from a byte array, starting at
     * <code>offset</code> and consisting of <code>length</code> bytes. Each byte
     * is converted to 2 hex symbols; zero(es) included.</p>
     *
     * @param ba     the byte array to convert.
     * @param offset the index from which to start considering the bytes to
     *               convert.
     * @param length the count of bytes, starting from the designated offset to
     *               convert.
     * @return a string of hexadecimal characters (two for each byte)
     * representing the designated input byte sub-array.
     */
    public static String toString(final byte[] ba, final int offset, final int length) {
        final char[] buf = new char[length * 2];
        for (int i = 0, j = 0, k; i < length; ) {
            k = ba[offset + i++];
            buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
            buf[j++] = HEX_DIGITS[k & 0x0F];
        }
        return new String(buf);
    }

    /**
     * <p>Returns a string of 8 hexadecimal digits (most significant digit first)
     * corresponding to the unsigned integer <code>n</code>.</p>
     *
     * @param n the unsigned integer to convert.
     * @return a hexadecimal string 8-character long.
     */
    public static String toString(int n) {
        final char[] buf = new char[8];
        for (int i = 7; i >= 0; i--) {
            buf[i] = HEX_DIGITS[n & 0x0F];
            n >>>= 4;
        }
        return new String(buf);
    }

    /**
     * <p>Returns a string of hexadecimal digits from an integer array. Each int
     * is converted to 4 hex symbols.</p>
     */
    public static String toString(final int[] ia) {
        final int length = ia.length;
        final char[] buf = new char[length * 8];
        for (int i = 0, j = 0, k; i < length; i++) {
            k = ia[i];
            buf[j++] = HEX_DIGITS[(k >>> 28) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>> 24) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>> 20) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>> 16) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>> 12) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>> 8) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
            buf[j++] = HEX_DIGITS[k & 0x0F];
        }
        return new String(buf);
    }

    /**
     * <p>Returns a string of 16 hexadecimal digits (most significant digit first)
     * corresponding to the unsigned long <code>n</code>.</p>
     *
     * @param n the unsigned long to convert.
     * @return a hexadecimal string 16-character long.
     */
    public static String toString(long n) {
        final char[] b = new char[16];
        for (int i = 15; i >= 0; i--) {
            b[i] = HEX_DIGITS[(int) (n & 0x0FL)];
            n >>>= 4;
        }
        return new String(b);
    }

    // BigInteger utilities ----------------------------------------------------

    /**
     * <p>Treats the input as the MSB representation of a number, and discards
     * leading zero elements. For efficiency, the input is simply returned if no
     * leading zeroes are found.</p>
     *
     * @param n the {@link java.math.BigInteger} to trim.
     * @return the byte array representation of the designated {@link java.math.BigInteger}
     * with no leading 0-bytes.
     */
    public static byte[] trim(final BigInteger n) {
        final byte[] in = n.toByteArray();
        if (in.length == 0 || in[0] != 0) {
            return in;
        }
        final int len = in.length;
        int i = 1;
        while (in[i] == 0 && i < len) {
            ++i;
        }
        final byte[] result = new byte[len - i];
        System.arraycopy(in, i, result, 0, len - i);
        return result;
    }

}
