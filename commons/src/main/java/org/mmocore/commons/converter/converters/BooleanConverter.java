package org.mmocore.commons.converter.converters;

/**
 * @author Mangol
 * @since 23.01.2016
 */
public class BooleanConverter implements IConverter<Boolean> {
    @Override
    public Boolean toObject(final String value) {
        return Boolean.valueOf(value);
    }
}
