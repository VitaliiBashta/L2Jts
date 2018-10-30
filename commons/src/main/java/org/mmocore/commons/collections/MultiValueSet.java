package org.mmocore.commons.collections;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Хранилище параметров разного типа.
 *
 * @param <T> ключ для доступа к значению параметра
 * @author G1ta0
 */
public class MultiValueSet<T> extends HashMap<T, Object> {
    private static final long serialVersionUID = 8071544899414292397L;

    private static final Pattern PATTERN = Pattern.compile(";");

    public MultiValueSet() {
    }

    public MultiValueSet(final int size) {
        super(size);
    }

    public MultiValueSet(final Map<T, Object> set) {
        super(set);
    }

    public void set(final T key, final Object value) {
        put(key, value);
    }

    public void set(final T key, final String value) {
        put(key, value);
    }

    public void set(final T key, final boolean value) {
        put(key, value ? Boolean.TRUE : Boolean.FALSE);
    }

    public void set(final T key, final int value) {
        put(key, value);
    }

    public void set(final T key, final int[] value) {
        put(key, value);
    }

    public void set(final T key, final long value) {
        put(key, value);
    }

    public void set(final T key, final double value) {
        put(key, value);
    }

    public void set(final T key, final Enum<?> value) {
        put(key, value);
    }

    public void unset(final T key) {
        remove(key);
    }

    public boolean isSet(final T key) {
        return get(key) != null;
    }

    @Override
    public MultiValueSet<T> clone() {
        return new MultiValueSet<>(this);
    }

    public boolean getBool(final T key) {
        final Object val = get(key);

        if (val instanceof Number) {
            return ((Number) val).intValue() != 0;
        }
        if (val instanceof String) {
            return Boolean.parseBoolean((String) val);
        }
        if (val instanceof Boolean) {
            return (Boolean) val;
        }

        throw new IllegalArgumentException("Boolean value required, but found: " + val + '!');
    }

    public boolean getBool(final T key, final boolean defaultValue) {
        final Object val = get(key);

        if (val instanceof Number) {
            return ((Number) val).intValue() != 0;
        }
        if (val instanceof String) {
            return Boolean.parseBoolean((String) val);
        }
        if (val instanceof Boolean) {
            return (Boolean) val;
        }

        return defaultValue;
    }

    public int getInteger(final T key) {
        final Object val = get(key);

        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        if (val instanceof String) {
            return Integer.parseInt((String) val);
        }
        if (val instanceof Boolean) {
            return (Boolean) val ? 1 : 0;
        }

        throw new IllegalArgumentException("Integer value required, but found: " + val + '!');
    }

    public int getInteger(final T key, final int defaultValue) {
        final Object val = get(key);

        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        if (val instanceof String) {
            return Integer.parseInt((String) val);
        }
        if (val instanceof Boolean) {
            return (Boolean) val ? 1 : 0;
        }

        return defaultValue;
    }

    public int[] getIntegerArray(final T key) {
        final Object val = get(key);

        if (val instanceof int[]) {
            return (int[]) val;
        }
        if (val instanceof Number) {
            return new int[]{((Number) val).intValue()};
        }
        if (val instanceof String) {
            final String[] vals = PATTERN.split((String) val);

            final int[] result = new int[vals.length];

            int i = 0;
            for (final String v : vals) {
                result[i++] = Integer.parseInt(v);
            }

            return result;
        }

        throw new IllegalArgumentException("Integer array required, but found: " + val + '!');
    }

    public int[] getIntegerArray(final T key, final int[] defaultArray) {
        try {
            return getIntegerArray(key);
        } catch (IllegalArgumentException e) {
            return defaultArray;
        }
    }

    public long getLong(final T key) {
        final Object val = get(key);

        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        if (val instanceof String) {
            return Long.parseLong((String) val);
        }
        if (val instanceof Boolean) {
            return (Boolean) val ? 1L : 0L;
        }

        throw new IllegalArgumentException("Long value required, but found: " + val + '!');
    }

    public long getLong(final T key, final long defaultValue) {
        final Object val = get(key);

        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        if (val instanceof String) {
            return Long.parseLong((String) val);
        }
        if (val instanceof Boolean) {
            return (Boolean) val ? 1L : 0L;
        }

        return defaultValue;
    }

    public long[] getLongArray(final T key) {
        final Object val = get(key);

        if (val instanceof long[]) {
            return (long[]) val;
        }
        if (val instanceof Number) {
            return new long[]{((Number) val).longValue()};
        }
        if (val instanceof String) {
            final String[] vals = PATTERN.split((String) val);

            final long[] result = new long[vals.length];

            int i = 0;
            for (final String v : vals) {
                result[i++] = Integer.parseInt(v);
            }

            return result;
        }

        throw new IllegalArgumentException("Integer array required, but found: " + val + '!');
    }

    public double getDouble(final T key) {
        final Object val = get(key);

        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        if (val instanceof String) {
            return Double.parseDouble((String) val);
        }
        if (val instanceof Boolean) {
            return (Boolean) val ? 1. : 0.;
        }

        throw new IllegalArgumentException("Double value required, but found: " + val + '!');
    }

    public double getDouble(final T key, final double defaultValue) {
        final Object val = get(key);

        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        if (val instanceof String) {
            return Double.parseDouble((String) val);
        }
        if (val instanceof Boolean) {
            return (Boolean) val ? 1. : 0.;
        }

        return defaultValue;
    }

    public String getString(final T key) {
        final Object val = get(key);

        if (val != null) {
            return String.valueOf(val);
        }

        throw new IllegalArgumentException("String value required, but not specified!");
    }

    public String getString(final T key, final String defaultValue) {
        final Object val = get(key);

        if (val != null) {
            return String.valueOf(val);
        }

        return defaultValue;
    }

    public Object getObject(final T key) {
        return get(key);
    }

    public Object getObject(final T key, final Object defaultValue) {
        final Object val = get(key);

        if (val != null) {
            return val;
        }

        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> E getEnum(final T name, final Class<E> enumClass) {
        final Object val = get(name);

        if (val != null && enumClass.isInstance(val)) {
            return (E) val;
        }
        if (val instanceof String) {
            return Enum.valueOf(enumClass, (String) val);
        }

        throw new IllegalArgumentException("Enum value of type " + enumClass.getName() + "required, but found: " + val + '!');
    }

    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> E getEnum(final T name, final Class<E> enumClass, final E defaultValue) {
        final Object val = get(name);

        if (val != null && enumClass.isInstance(val)) {
            return (E) val;
        }
        if (val instanceof String) {
            return Enum.valueOf(enumClass, (String) val);
        }

        return defaultValue;
    }
}
