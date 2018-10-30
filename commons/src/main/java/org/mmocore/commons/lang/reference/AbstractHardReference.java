package org.mmocore.commons.lang.reference;

import java.util.Objects;

public class AbstractHardReference<T> implements HardReference<T> {
    private T reference;

    public AbstractHardReference(final T reference) {
        this.reference = reference;
    }

    @Override
    public T get() {
        return reference;
    }

    @Override
    public void clear() {
        reference = null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof AbstractHardReference)) {
            return false;
        }
        if (((HardReference) o).get() == null) {
            return false;
        }
        return Objects.equals(((HardReference) o).get(), get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }
}
