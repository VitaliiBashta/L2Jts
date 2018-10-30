package org.mmocore.commons.listeners;

/**
 * @author Java-man
 */
public interface ListenableObject<E extends Enum<?>> {
    ListenersContainer<E> getListenerContainer();

    E getListenerType();

    default void fireEvent(final Listener listener) {
        getListenerContainer().fireEvent(getListenerType(), listener);
    }

    default void addListener(final Class<? extends Listener> listener) {
        getListenerContainer().addListener(getListenerType(), listener);
    }

    default void removeListener(final Class<? extends Listener> listener) {
        getListenerContainer().removeListener(getListenerType(), listener);
    }
}
