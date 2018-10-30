package org.jts.protection.network.crypt;

/**
 * @author ALF
 */
public class ProtectionCrypt {
    private byte[] state = new byte[256];
    private int x;
    private int y;

    /**
     * Инициализация класса с ключевыми байтами массива. Длина нормального ключа должна быть от 1 до 2048 бит.
     * Но этот метод не проверяет длину. Мы используем 128битный ключ.
     */
    public void setKey(final byte[] key) throws NullPointerException {
        for (int i = 0; i < 256; i++)
            state[i] = (byte) i;

        x = 0;
        y = 0;

        int index1 = 0;
        int index2 = 0;

        byte tmp;

        if (key == null || key.length == 0)
            throw new NullPointerException();

        for (int i = 0; i < 256; i++) {
            index2 = (key[index1] & 0xff) + (state[i] & 0xff) + index2 & 0xff;

            tmp = state[i];
            state[i] = state[index2];
            state[index2] = tmp;

            index1 = (index1 + 1) % key.length;
        }
    }

    /**
     * Енкрипт\Декрипт массива байтов, оптимизировано под эмуляторы
     */
    public void chiper(final byte[] buf, final int offset, final int size) {
        int xorIndex;
        byte tmp;

        for (int i = 0; i < size; i++) {
            x = x + 1 & 0xff;
            y = (state[x] & 0xff) + y & 0xff;

            tmp = state[x];
            state[x] = state[y];
            state[y] = tmp;

            xorIndex = (state[x] & 0xff) + (state[y] & 0xff) & 0xff;
            buf[offset + i] ^= state[xorIndex];
        }
    }
}