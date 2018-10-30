package org.mmocore.commons.converter.numberConverters;

import java.util.stream.IntStream;

/**
 * @author Mangol
 * @since 22.01.2016
 */
public class ByteConverter implements INumberConverter<Byte> {
    @Override
    public Byte toObject(final String value) {
        return Byte.valueOf(value);
    }

    @Override
    public Byte[] toArrays(final String value, final String split) {
        final String[] str = value.split(split);
        final Byte[] array = new Byte[str.length];
        IntStream.range(0, str.length).forEach(i -> array[i] = toObject(str[i]));
        return array;
    }

    @Override
    public byte[] toBytePrimitive(final String value, final String split) {
        final String[] str = value.split(split);
        final byte[] array = new byte[str.length];
        IntStream.range(0, str.length).forEach(i -> array[i] = toObject(str[i]));
        return array;
    }
}
