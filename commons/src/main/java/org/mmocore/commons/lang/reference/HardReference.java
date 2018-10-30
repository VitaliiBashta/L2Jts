package org.mmocore.commons.lang.reference;

/**
 * Интерфейс хранителя ссылки.
 *
 * @param <T>
 * @author G1ta0
 */
public interface HardReference<T> {
    /**
     * Получить объект, который удерживается *
     */
    T get();

    /**
     * Очистить сылку на удерживаемый объект *
     */
    void clear();
}
