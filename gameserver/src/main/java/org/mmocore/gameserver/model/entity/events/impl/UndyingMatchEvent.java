package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 18:03/22.08.2011
 */
public abstract class UndyingMatchEvent extends Event {
    protected UndyingMatchEvent(final MultiValueSet<String> set) {
        super(set);
    }

    protected UndyingMatchEvent(final int id, final String name) {
        super(id, name);
    }

    public abstract void onDie(Player player);
}
