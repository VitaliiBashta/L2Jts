package org.mmocore.gameserver.model.reference;

import org.mmocore.commons.lang.reference.AbstractHardReference;

public class L2Reference<T> extends AbstractHardReference<T> {
    public L2Reference(final T reference) {
        super(reference);
    }
}
