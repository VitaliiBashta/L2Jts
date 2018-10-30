package org.mmocore.commons.converter;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import org.mmocore.commons.converter.converters.BooleanConverter;
import org.mmocore.commons.converter.converters.IConverter;
import org.mmocore.commons.converter.numberConverters.*;

/**
 * @author Mangol
 * @since 22.01.2016
 */
public final class RegisterObject {
    private static final TMap<Class<?>, INumberConverter> numberConverter = new THashMap<>();
    private static final TMap<Class<?>, IConverter> converter = new THashMap<>();
    private static final IntegerConverter integerConverter = new IntegerConverter();
    private static final LongConverter longConverter = new LongConverter();
    private static final ByteConverter byteConverter = new ByteConverter();
    private static final DoubleConverter doubleConverter = new DoubleConverter();
    private static final FloatConverter floatConverter = new FloatConverter();
    private static final ShortConverter shortConverter = new ShortConverter();
    private static final BooleanConverter booleanConverter = new BooleanConverter();

    static {
        registerObject();
    }

    private static void registerObject() {
        converter.put(Boolean.class, booleanConverter);
        numberConverter.put(Integer.class, integerConverter);
        numberConverter.put(Long.class, longConverter);
        numberConverter.put(Byte.class, byteConverter);
        numberConverter.put(Double.class, doubleConverter);
        numberConverter.put(Float.class, floatConverter);
        numberConverter.put(Short.class, shortConverter);
        //primitive
        numberConverter.put(int.class, integerConverter);
        numberConverter.put(long.class, longConverter);
        numberConverter.put(byte.class, byteConverter);
        numberConverter.put(double.class, doubleConverter);
        numberConverter.put(float.class, floatConverter);
        numberConverter.put(short.class, shortConverter);
        converter.put(boolean.class, booleanConverter);
    }

    public static INumberConverter getNumberConverter(final Class<?> clazz) {
        return numberConverter.get(clazz);
    }

    public static IConverter getConverter(final Class<?> clazz) {
        return converter.get(clazz);
    }
}
