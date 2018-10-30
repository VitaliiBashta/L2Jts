package org.mmocore.commons.converter.numberConverters;

import java.util.stream.IntStream;

/**
 * @author Mangol
 * @since 22.01.2016
 */
public class ShortConverter implements INumberConverter<Short> {
    @Override
    public Short toObject(final String value) {
        return Short.valueOf(value);
    }

    @Override
    public Short[] toArrays(final String value, final String split) {
        final String[] str = value.split(split);
        final Short[] array = new Short[str.length];
        IntStream.range(0, str.length).forEach(i -> array[i] = toObject(str[i]));
        return array;
    }

    @Override
    public short[] toShortPrimitive(final String value, final String split) {
        final String[] str = value.split(split);
        final short[] array = new short[str.length];
        IntStream.range(0, str.length).forEach(i -> array[i] = toObject(str[i]));
        return array;
    }
}
