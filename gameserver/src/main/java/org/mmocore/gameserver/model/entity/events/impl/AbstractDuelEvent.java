package org.mmocore.gameserver.model.entity.events.impl;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.object.Player;

public abstract class AbstractDuelEvent extends SingleMatchEvent {
    public AbstractDuelEvent(final MultiValueSet<String> set) {
        super(set);
    }

    protected AbstractDuelEvent(final int id, final String name) {
        super(id, name);
    }

    public abstract boolean canDuel(final Player player, final Player target, final boolean first);

    public abstract void askDuel(final Player player, final Player target, final int arenaId);

    public abstract void createDuel(final Player player, final Player target, final int arenaId);
}