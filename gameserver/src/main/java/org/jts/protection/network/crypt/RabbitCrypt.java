/**
 * The Java Implementation of Rabbit Stream Cipher
 *
 * @author cnbragon
 * @email cnbragon_dot_163_dot_com
 * @date 2009/09/25
 * @note Not implemented IV scheme
 */
package org.jts.protection.network.crypt;

public class RabbitCrypt {
    private int[] x = new int[8];
    private int[] c = new int[8];
    private int carry;

    public RabbitCrypt() {
        for (int i = 0; i < 8; i++)
            x[i] = c[i] = 0;
        carry = 0;
    }

    /**
     * @declaration An array of bytes into an integer, using Big-Endian format parsing
     * @param a byte array to be converted
     * @param i start index byte array
     * @return integer after conversion
     */
    public static int os2ip(final byte[] a, final int i) {
        final int x0 = a[i + 3] & 0x000000ff;
        final int x1 = a[i + 2] << 8 & 0x0000ff00;
        final int x2 = a[i + 1] << 16 & 0x00ff0000;
        final int x3 = a[i] << 24 & 0xff000000;

        return x0 | x1 | x2 | x3;
    }

    /**
     * @declaration will be converted to 4-byte integer x array, using Big-Endian format parsing
     * @param x the integer x to be transformed
     * @return byte array parsed length of 4
     */
    public static byte[] i2osp(final int x) {
        final byte[] s = new byte[4];
        s[3] = (byte) (x & 0x000000ff);
        s[2] = (byte) ((x & 0x0000ff00) >>> 8);
        s[1] = (byte) ((x & 0x00ff0000) >>> 16);
        s[0] = (byte) ((x & 0xff000000) >>> 24);
        return s;
    }

    private int g_func(final int x) {
        final int a = x & 0xffff;
        final int b = x >>> 16;

        final int h = ((((a * a) >>> 17) + (a * b)) >>> 15) + b * b;
        final int l = x * x;

        return h ^ l;
    }

    /**
     * @declaration Compare two signed integers hex size, that is, as an unsigned integer compare
     * @param x
     * @param y
     * @return If (unsigned x) <(unsigned y), returns 1, otherwise 0
     */
    private int compare(final int x, final int y) {
        long a = x;
        long b = y;
        a &= 0x00000000ffffffffL;
        b &= 0x00000000ffffffffL;

        return (a < b) ? 1 : 0;
    }

    private int rotL(final int x, final int y) {
        return (x << y) | (x >>> (32 - y));
    }

    private void next_state() {
        final int[] g = new int[8];
        final int[] c_old = new int[8];
        int i = 0;

        for (i = 0; i < 8; i++)
            c_old[i] = c[i];

        c[0] += 0x4d34d34d + carry;
        c[1] += 0xd34d34d3 + compare(c[0], c_old[0]);
        c[2] += 0x34d34d34 + compare(c[1], c_old[1]);
        c[3] += 0x4d34d34d + compare(c[2], c_old[2]);
        c[4] += 0xd34d34d3 + compare(c[3], c_old[3]);
        c[5] += 0x34d34d34 + compare(c[4], c_old[4]);
        c[6] += 0x4d34d34d + compare(c[5], c_old[5]);
        c[7] += 0xd34d34d3 + compare(c[6], c_old[6]);
        carry = compare(c[7], c_old[7]);

        for (i = 0; i < 8; i++)
            g[i] = g_func(x[i] + c[i]);

        x[0] = g[0] + rotL(g[7], 16) + rotL(g[6], 16);
        x[1] = g[1] + rotL(g[0], 8) + g[7];
        x[2] = g[2] + rotL(g[1], 16) + rotL(g[0], 16);
        x[3] = g[3] + rotL(g[2], 8) + g[1];
        x[4] = g[4] + rotL(g[3], 16) + rotL(g[2], 16);
        x[5] = g[5] + rotL(g[4], 8) + g[3];
        x[6] = g[6] + rotL(g[5], 16) + rotL(g[4], 16);
        x[7] = g[7] + rotL(g[6], 8) + g[5];
    }

    /**
     * @declaration initialization function keys
     * @param p_key 128-bit key length array, if the key length is less than 128.
          * This function must be called before filling!
     */
    public void keySetup(final byte[] p_key) {
        final int k0;
        final int k1;
        final int k2;
        final int k3;
        int i;

        k0 = os2ip(p_key, 12);
        k1 = os2ip(p_key, 8);
        k2 = os2ip(p_key, 4);
        k3 = os2ip(p_key, 0);

        x[0] = k0;
        x[2] = k1;
        x[4] = k2;
        x[6] = k3;
        x[1] = (k3 << 16) | (k2 >>> 16);
        x[3] = (k0 << 16) | (k3 >>> 16);
        x[5] = (k1 << 16) | (k0 >>> 16);
        x[7] = (k2 << 16) | (k1 >>> 16);

        c[0] = rotL(k2, 16);
        c[2] = rotL(k3, 16);
        c[4] = rotL(k0, 16);
        c[6] = rotL(k1, 16);
        c[1] = (k0 & 0xffff0000) | (k1 & 0x0000ffff);
        c[3] = (k1 & 0xffff0000) | (k2 & 0x0000ffff);
        c[5] = (k2 & 0xffff0000) | (k3 & 0x0000ffff);
        c[7] = (k3 & 0xffff0000) | (k0 & 0x0000ffff);

        carry = 0;
        for (i = 0; i < 4; i++)
            next_state();

        for (i = 0; i < 8; i++)
            c[(i + 4) & 7] ^= x[i];
    }

    /**
     * @declaration This function is used to generate 128 random keys for direct and explicit XOR processing
     * @param p_dest generated 128 random key
     * @param data_size number of random keys needed to produce and must be a multiple of 16
     */
    public void getRandomKey(final byte[] p_dest, final long data_size) {
        int i, j, m;
        final int[] k = new int[4];
        byte[] t = new byte[4];

        for (i = 0; i < data_size; i += 16) {
            next_state();
            k[0] = x[0] ^ (x[5] >>> 16) ^ (x[3] << 16);
            k[1] = x[2] ^ (x[7] >>> 16) ^ (x[5] << 16);
            k[2] = x[4] ^ (x[1] >>> 16) ^ (x[7] << 16);
            k[3] = x[6] ^ (x[3] >>> 16) ^ (x[1] << 16);
            for (j = 0; j < 4; j++) {
                t = i2osp(k[j]);
                for (m = 0; m < 4; m++)
                    p_dest[j * 4 + m] = t[m];
            }
        }
    }

    /**
     * @declaration encryption and decryption functions
     * @param p_src need to encrypt or decrypt the message, its length must be a multiple of 16 bytes, if it is a multiple of 16, you need to be filled before calling this function, the general padding Byte 0 *
     * @param p_dest encrypted or decrypted result, the length must be a multiple of 16 bytes and the length must be greater than or equal p_src length
     * @param data_size need to deal with the length of the message must be a multiple of 16 bytes the length of the value p_src
     */
    public void cipher(final byte[] p_src, final byte[] p_dest, final long data_size) {
        int i, j, m;
        final int[] k = new int[4];
        byte[] t = new byte[4];

        for (i = 0; i < data_size; i += 16) {
            next_state();
            k[0] = os2ip(p_src, i * 16 + 0) ^ x[0] ^ (x[5] >>> 16) ^ (x[3] << 16);
            k[1] = os2ip(p_src, i * 16 + 4) ^ x[2] ^ (x[7] >>> 16) ^ (x[5] << 16);
            k[2] = os2ip(p_src, i * 16 + 8) ^ x[4] ^ (x[1] >>> 16) ^ (x[7] << 16);
            k[3] = os2ip(p_src, i * 16 + 12) ^ x[6] ^ (x[3] >>> 16) ^ (x[1] << 16);
            for (j = 0; j < 4; j++) {
                t = i2osp(k[j]);
                for (m = 0; m < 4; m++)
                    p_dest[i * 16 + j * 4 + m] = t[m];
            }
        }
    }
}