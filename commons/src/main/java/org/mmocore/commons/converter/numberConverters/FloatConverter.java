package org.mmocore.commons.converter.numberConverters;

import java.util.stream.IntStream;

/**
 * @author Mangol
 * @since 22.01.2016
 */
public class FloatConverter implements INumberConverter<Float> {
    @Override
    public Float toObject(final String value) {
        return Float.valueOf(value);
    }

    @Override
    public Float[] toArrays(final String value, final String split) {
        final String[] str = value.split(split);
        final Float[] array = new Float[str.length];
        IntStream.range(0, str.length).forEach(i -> array[i] = toObject(str[i]));
        return array;
    }

    @Override
    public float[] toFloatPrimitive(final String value, final String split) {
        final String[] str = value.split(split);
        final float[] array = new float[str.length];
        IntStream.range(0, str.length).forEach(i -> array[i] = toObject(str[i]));
        return array;
    }
}
