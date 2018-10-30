package org.mmocore.commons.lang.reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Вспомогательный класс для работы с HardReference
 *
 * @author G1ta0
 */
public class HardReferences {
    private static final HardReference<?> EMPTY_REF = new EmptyReferencedHolder(null);

    private HardReferences() {
    }

    @SuppressWarnings("unchecked")
    public static <T> HardReference<T> emptyRef() {
        return (HardReference<T>) EMPTY_REF;
    }

    /**
     * Получить список объектов, исходя из коллекции ссылок. Нулевые ссылки будут отфильтрованы.
     *
     * @param <T>
     * @param refs коллекция ссылок
     * @return коллекцию объектов, на которые указываю ссылки
     */
    public static <T> Collection<T> unwrap(final Collection<HardReference<T>> refs) {
        final Collection<T> result = new ArrayList<>(refs.size());
        for (final HardReference<T> ref : refs) {
            final T obj = ref.get();
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }

    /**
     * Итерация по коллекции ссылок на объекты.
     *
     * @param <T>
     * @param refs коллекция ссылок на объекты
     * @return враппер, который будет возвращать при итерации объекты, на которые указывают ссылки
     */
    public static <T> Iterable<T> iterate(final Iterable<HardReference<T>> refs) {
        return new WrappedIterable<>(refs);
    }

    private static class EmptyReferencedHolder extends AbstractHardReference<Object> {
        public EmptyReferencedHolder(final Object reference) {
            super(reference);
        }
    }

    private static class WrappedIterable<T> implements Iterable<T> {
        final Iterable<HardReference<T>> refs;

        WrappedIterable(final Iterable<HardReference<T>> refs) {
            this.refs = refs;
        }

        @Override
        public Iterator<T> iterator() {
            return new WrappedIterator<>(refs.iterator());
        }

        private static class WrappedIterator<T> implements Iterator<T> {
            final Iterator<HardReference<T>> iterator;

            WrappedIterator(final Iterator<HardReference<T>> iterator) {
                this.iterator = iterator;
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next().get();
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        }
    }
}
