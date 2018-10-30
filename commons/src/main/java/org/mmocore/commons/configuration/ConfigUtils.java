package org.mmocore.commons.configuration;

import org.mmocore.commons.converter.Converter;

import java.util.Optional;

/**
 * @author Mangol
 * @since 08.01.2016
 */
public class ConfigUtils {
    public static Optional<?> toObject(final Class clazz, final String value, final Optional<Double> minValue, final Optional<Double> maxValue, final Optional<Double> increase) {
        if (Integer.class == clazz || int.class == clazz) {
            Integer converted = (Integer) Converter.convert(clazz, value);
            if (increase.isPresent()) {
                converted *= increase.get().intValue();
            }
            if (minValue.isPresent()) {
                converted = Math.max(converted, minValue.get().intValue());
            }
            if (maxValue.isPresent()) {
                converted = Math.min(converted, maxValue.get().intValue());
            }
            converted = Math.toIntExact(converted);
            return Optional.of(converted);
        } else if (Long.class == clazz || long.class == clazz) {
            Long converted = (Long) Converter.convert(clazz, value);
            if (increase.isPresent()) {
                converted *= increase.get().intValue();
            }
            if (minValue.isPresent()) {
                converted = Math.max(converted, minValue.get().longValue());
            }
            if (maxValue.isPresent()) {
                converted = Math.min(converted, maxValue.get().longValue());
            }
            return Optional.of(converted);
        } else if (Double.class == clazz || double.class == clazz) {
            Double converted = (Double) Converter.convert(clazz, value);
            if (increase.isPresent()) {
                converted *= increase.get();
            }
            if (minValue.isPresent()) {
                converted = Math.max(converted, minValue.get());
            }
            if (maxValue.isPresent()) {
                converted = Math.min(converted, maxValue.get());
            }
            return Optional.of(converted);
        } else if (String.class == clazz) {
            return Optional.of(value);
        }
        return Optional.ofNullable(Converter.convert(clazz, value));
    }
}
