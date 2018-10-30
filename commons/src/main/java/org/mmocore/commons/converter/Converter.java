package org.mmocore.commons.converter;

import org.mmocore.commons.converter.converters.IConverter;
import org.mmocore.commons.converter.numberConverters.*;

import java.util.Optional;

/**
 * @author Mangol
 * @since 09.01.2016
 */
public class Converter {
    @SuppressWarnings("unchecked")
    public static <T> T convert(final Class<T> type, final String value) {
        final Optional<INumberConverter> numberConverter = Optional.ofNullable(RegisterObject.getNumberConverter(type));
        if (numberConverter.isPresent()) {
            return (T) numberConverter.get().toObject(value);
        }
        final Optional<IConverter> converter = Optional.ofNullable(RegisterObject.getConverter(type));
        if (converter.isPresent()) {
            return (T) converter.get().toObject(value);
        }
        throw new NullPointerException();
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] convertArraysNumber(final Class<?> type, final String value, final String split) {
        final Optional<INumberConverter> numberConverter = Optional.ofNullable(RegisterObject.getNumberConverter(type));
        if (numberConverter.isPresent()) {
            return (T[]) numberConverter.get().toArrays(value, split);
        }
        throw new NullPointerException();
    }

    public static int[] toIntArray(final String value, final String split) {
        final INumberConverter converter = RegisterObject.getNumberConverter(Integer.class);
        final IntegerConverter integerConverter = (IntegerConverter) converter;
        return integerConverter.toIntArraysPrimitive(value, split);
    }

    public static double[] toDoubleArray(final String value, final String split) {
        final INumberConverter converter = RegisterObject.getNumberConverter(Double.class);
        final DoubleConverter doubleConverter = (DoubleConverter) converter;
        return doubleConverter.toDoubleArraysPrimitive(value, split);
    }

    public static float[] toFloatArray(final String value, final String split) {
        final INumberConverter converter = RegisterObject.getNumberConverter(Float.class);
        final FloatConverter floatConverter = (FloatConverter) converter;
        return floatConverter.toFloatPrimitive(value, split);
    }

    public static long[] toLongArray(final String value, final String split) {
        final INumberConverter converter = RegisterObject.getNumberConverter(Long.class);
        final LongConverter longConverter = (LongConverter) converter;
        return longConverter.toLongPrimitive(value, split);
    }

    public static byte[] toByteArray(final String value, final String split) {
        final INumberConverter converter = RegisterObject.getNumberConverter(Byte.class);
        final ByteConverter byteConverter = (ByteConverter) converter;
        return byteConverter.toBytePrimitive(value, split);
    }

    public static short[] toShortArray(final String value, final String split) {
        final INumberConverter converter = RegisterObject.getNumberConverter(Short.class);
        final ShortConverter shortConverter = (ShortConverter) converter;
        return shortConverter.toShortPrimitive(value, split);
    }
}
