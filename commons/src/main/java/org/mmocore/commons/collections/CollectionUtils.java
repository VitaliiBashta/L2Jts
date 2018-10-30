package org.mmocore.commons.collections;

import java.util.List;

public final class CollectionUtils {
    private CollectionUtils() {
    }

    public static <E> E safeGet(final List<E> list, final int index) {
        return list.size() > index ? list.get(index) : null;
    }
}