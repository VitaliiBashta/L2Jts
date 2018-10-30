package org.mmocore.commons.converter.numberConverters;

import java.util.stream.IntStream;

/**
 * @author Mangol
 * @since 09.01.2016
 */
public class DoubleConverter implements INumberConverter<Double> {
    @Override
    public Double toObject(final String value) {
        return Double.valueOf(value);
    }

    @Override
    public Double[] toArrays(final String value, final String split) {
        final String[] str = value.split(split);
        final Double[] array = new Double[str.length];
        IntStream.range(0, str.length).forEach(i -> array[i] = toObject(str[i]));
        return array;
    }

    @Override
    public double[] toDoubleArraysPrimitive(final String value, final String split) {
        final String[] str = value.split(split);
        final double[] array = new double[str.length];
        IntStream.range(0, str.length).forEach(i -> array[i] = toObject(str[i]));
        return array;
    }
}
