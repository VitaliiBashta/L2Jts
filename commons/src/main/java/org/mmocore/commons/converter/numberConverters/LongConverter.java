package org.mmocore.commons.converter.numberConverters;

import java.util.stream.IntStream;

/**
 * @author Mangol
 * @since 22.01.2016
 */
public class LongConverter implements INumberConverter<Long> {
    @Override
    public Long toObject(final String value) {
        return Long.valueOf(value);
    }

    @Override
    public Long[] toArrays(final String value, final String split) {
        final String[] str = value.split(split);
        final Long[] array = new Long[str.length];
        IntStream.range(0, str.length).forEach(i -> array[i] = toObject(str[i]));
        return array;
    }

    @Override
    public long[] toLongPrimitive(final String value, final String split) {
        final String[] str = value.split(split);
        final long[] array = new long[str.length];
        IntStream.range(0, str.length).forEach(i -> array[i] = toObject(str[i]));
        return array;
    }
}
