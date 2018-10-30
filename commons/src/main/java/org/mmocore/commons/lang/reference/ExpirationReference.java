package org.mmocore.commons.lang.reference;

import java.lang.ref.WeakReference;

/**
 * @author Java-man
 */
public class ExpirationReference<T> {
    private final WeakReference<T> reference;
    private final T defaultReference;
    private final long expirationTime;

    public ExpirationReference(final T reference, final long expirationTime) {
        this.reference = new WeakReference<>(reference);
        this.defaultReference = null;
        this.expirationTime = System.currentTimeMillis() + expirationTime;
    }

    public ExpirationReference(final T reference, final T defaultReference, final long expirationTime) {
        this.reference = new WeakReference<>(reference);
        this.defaultReference = defaultReference;
        this.expirationTime = expirationTime;
    }

    public T get() {
        if (expirationTime - System.currentTimeMillis() < 0)
            return defaultReference;

        return reference.get();
    }
}
