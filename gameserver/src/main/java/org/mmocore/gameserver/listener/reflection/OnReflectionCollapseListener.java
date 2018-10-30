package org.mmocore.gameserver.listener.reflection;

import org.mmocore.commons.listener.Listener;
import org.mmocore.gameserver.model.entity.Reflection;

@FunctionalInterface
public interface OnReflectionCollapseListener extends Listener<Reflection> {
    void onReflectionCollapse(Reflection reflection);
}
