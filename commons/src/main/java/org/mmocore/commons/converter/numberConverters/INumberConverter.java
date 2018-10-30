package org.mmocore.commons.converter.numberConverters;

/**
 * @author Mangol
 * @since 09.01.2016
 */
public interface INumberConverter<T extends Number> {
    default int[] toIntArraysPrimitive(final String value, final String split) {
        throw new NullPointerException();
    }

    default double[] toDoubleArraysPrimitive(final String value, final String split) {
        throw new NullPointerException();
    }

    default long[] toLongPrimitive(final String value, final String split) {
        throw new NullPointerException();
    }

    default float[] toFloatPrimitive(final String value, final String split) {
        throw new NullPointerException();
    }

    default byte[] toBytePrimitive(final String value, final String split) {
        throw new NullPointerException();
    }

    default short[] toShortPrimitive(final String value, final String split) {
        throw new NullPointerException();
    }

    T[] toArrays(final String value, final String split);

    T toObject(final String value);
}
